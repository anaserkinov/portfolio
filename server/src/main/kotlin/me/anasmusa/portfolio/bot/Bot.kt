package me.anasmusa.portfolio.bot

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.document
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatAction
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.extensions.filters.Filter
import com.github.kotlintelegrambot.webhook
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromStream
import me.anasmusa.portfolio.Config
import me.anasmusa.portfolio.db.JsonDatabase
import me.anasmusa.portfolio.db.QdrantDatabase
import java.io.File


object Bot {

    fun start(){
        bot.startWebhook()
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val bot = bot {
        token = Config.ADMIN_BOT_TOKEN

        webhook {
            url = Config.ADMIN_BOT_WEBHOOK_URL
        }

        dispatch {

            command("start"){
                if (message.chat.id != Config.ADMIN_TG_ID)
                    return@command
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = """
                         Please send:
                         • One photo with a file name containing only lowercase letters (a–z) and underscores (_).
                         • One JSON file with the message "content" or "context".
                     """.trimIndent()
                )
            }

            document {
                if (message.chat.id != Config.ADMIN_TG_ID)
                    return@document
                bot.sendChatAction(
                    ChatId.fromId(message.chat.id),
                    ChatAction.TYPING
                )
                val error = StringBuilder()

                if (message.text.isNullOrBlank())
                    error.appendLine("* Enter file name (content or context)")
                else if (message.text != "content" && message.text != "context")
                    error.appendLine("* Acceptable file names: 'content' or 'context'")

                if (error.isEmpty()) {
                    val remoteFilePath = bot.getFile(message.document!!.fileId).first?.body()?.result?.filePath
                    if (remoteFilePath == null) {
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = "❗Couldn't download file"
                        )
                        return@document
                    }
                    if (remoteFilePath.substring(remoteFilePath.lastIndexOf('.') + 1) != "json") {
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = "❗Only json files"
                        )
                        return@document
                    }
                    val remoteFile = bot.downloadFile(remoteFilePath).first?.body()
                    if (remoteFile == null) {
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = "❗Couldn't download file"
                        )
                        return@document
                    }

                    val json = Json.decodeFromStream<JsonObject>(remoteFile.byteStream())
                    val result = if (message.text == "content") {
                        JsonDatabase.save(json)
                    } else {
                        QdrantDatabase.save(json)
                    }

                    if (result)
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = "✅"
                        )
                    else
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = "❗Error occurred while saving file"
                        )
                } else {
                    error.insert(0, "❗️ \n")
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = error.toString()
                    )
                }
            }

            message(filter = Filter.Photo, handleMessage = {
                if (message.chat.id != Config.ADMIN_TG_ID)
                    return@message

                val error = StringBuilder()

                if (message.photo!!.size > 1)
                    error.appendLine("* Send only one photo")
                if (message.text.isNullOrBlank())
                    error.appendLine("* Enter file name")
                else if ("^[a-z_]+$".toRegex().matches(message.text!!))
                    error.appendLine("* Only lowercase letters (a-z) and '_' are allowed.")

                if (error.isEmpty()) {
                    val remoteFilePath = bot.getFile(message.photo!!.first().fileId).first?.body()?.result?.filePath
                    if (remoteFilePath == null) {
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = "❗Couldn't download file"
                        )
                        return@message
                    }
                    val remoteFile = bot.downloadFile(remoteFilePath).first?.body()
                    if (remoteFile == null) {
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = "❗Couldn't download file"
                        )
                        return@message
                    }

                    val folder = File("data/images")
                    folder.mkdirs()
                    val file = File(folder, "${message.text}.${remoteFilePath.substringAfterLast('.')}")
                    file.writeBytes(remoteFile.bytes())

                    val response = "images/${file.absolutePath}"
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "✅  -  `$response`",
                        parseMode = ParseMode.MARKDOWN
                    )
                } else {
                    error.insert(0, "❗️ \n")
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = error.toString()
                    )
                }
            })
        }
    }


}