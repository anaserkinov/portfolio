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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromStream
import me.anasmusa.portfolio.core.AppJson
import me.anasmusa.portfolio.core.Config
import me.anasmusa.portfolio.db.JsonDatabase
import me.anasmusa.portfolio.db.QdrantDatabase
import java.io.File


object Bot {

    fun start(){
        bot.startWebhook()
    }

    suspend fun processUpdate(updateJson: String){
        bot.processUpdate(updateJson)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val bot = bot {
        token = Config.ADMIN_BOT_TOKEN

        webhook {
            url = Config.ADMIN_BOT_WEBHOOK_URL
            secretToken = Config.ADMIN_BOT_SECRET_TOKEN
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
                         • One PDF file with the message "resume".
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

                val fileName = message.caption
                if (fileName.isNullOrBlank())
                    error.appendLine("* Enter file name (content or context or resume)")
                else if (fileName != "content" && fileName != "context" && fileName != "resume")
                    error.appendLine("* Acceptable file names: 'content' or 'context' or 'resume'")

                if (error.isEmpty()) {
                    val remoteFilePath = bot.getFile(message.document!!.fileId).first?.body()?.result?.filePath
                    if (remoteFilePath == null) {
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = "❗Couldn't download file"
                        )
                        return@document
                    }
                    val ext = remoteFilePath.substring(remoteFilePath.lastIndexOf('.') + 1)

                    when(fileName) {
                        "context", "content" -> {
                            if (ext != "json"){
                                bot.sendMessage(
                                    chatId = ChatId.fromId(message.chat.id),
                                    text = "❗Only JSON files are allowed for $fileName type."
                                )
                                return@document
                            }
                        }
                        "resume" -> {
                            if (ext != "pdf"){
                                bot.sendMessage(
                                    chatId = ChatId.fromId(message.chat.id),
                                    text = "❗Only PDF files are allowed for resume type."
                                )
                                return@document
                            }
                        }
                    }

                    val remoteFile = bot.downloadFile(remoteFilePath).first?.body()
                    if (remoteFile == null) {
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = "❗Couldn't download file"
                        )
                        return@document
                    }

                    if (fileName == "resume"){
                        val folder = File("data")
                        folder.mkdirs()
                        val resumeFile = File(folder, "resume.pdf")
                        resumeFile.writeBytes(remoteFile.bytes())
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = "✅  -  `resume.pdf`",
                            parseMode = ParseMode.MARKDOWN
                        )
                        return@document
                    }

                    val json = AppJson.decodeFromStream<JsonObject>(remoteFile.byteStream())
                    val result = if (fileName == "content") {
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
                    error.insert(0, "❗️Error\n")
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = error.toString()
                    )
                }
            }

            message(filter = Filter.Photo, handleMessage = {
                if (message.chat.id != Config.ADMIN_TG_ID)
                    return@message

                val fileName = message.caption
                val error = StringBuilder()

                if (fileName.isNullOrBlank())
                    error.appendLine("* Enter file name")
                else if (!"^[a-z_]+$".toRegex().matches(fileName))
                    error.appendLine("* Only lowercase letters (a-z) and '_' are allowed.")

                if (error.isEmpty()) {
                    val remoteFilePath = bot.getFile(message.photo!!.last().fileId).first?.body()?.result?.filePath
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
                    val file = File(folder, "${fileName}.${remoteFilePath.substringAfterLast('.')}")
                    file.writeBytes(remoteFile.bytes())

                    val response = "images/${file.name}"
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