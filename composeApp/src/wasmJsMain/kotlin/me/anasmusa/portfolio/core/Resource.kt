package me.anasmusa.portfolio.core

import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.parsing.DOMParser
import org.w3c.fetch.Response

object Resource {

    private var currentLocale: String? = null
    internal val strings = ArrayList<String>()

    val isLoaded: Boolean
        get() = currentLocale != null && strings.isNotEmpty()

    fun setLocale(locale: String, onLoad: () -> Unit){
        if (currentLocale == locale)
            return
        readStringFile(locale){
            this.strings.clear()
            this.strings.add("")
            this.strings.addAll(it)
            currentLocale = locale
            onLoad()
        }
    }

    private fun readStringFile(locale: String, onLoad: (List<String>) -> Unit) {
        var path = "values"
        if (locale != "en")
            path += "-$locale"
        path = "$path/strings.xml"
        val response = window.fetch(path)
        CoroutineScope(ioDispatcher).launch {
            val text = response.await<Response>().text().await<JsString>()
            withContext(Dispatchers.Main) {
                onLoad(
                    parseStringsXml(text.toString())
                )
            }
        }
    }

    private fun parseStringsXml(xmlContent: String): List<String> {
        val result = arrayListOf<String>()

        try {
            val parser = DOMParser()
            val doc = parser.parseFromString(xmlContent, "application/xml".toJsString())

            run {
                val stringTags = doc.getElementsByTagName("string")
                for (i in 0 until stringTags.length) {
                    val element = stringTags.item(i)
                    val value = element?.textContent
                    if (!value.isNullOrBlank()) {
                        result.add(value.trim())
                    }
                }
            }

            run {
                val quantities = arrayOf("zero", "one", "two", "few", "many", "other")

                val pluralTags = doc.getElementsByTagName("plurals")

                for (i in 0 until pluralTags.length) {
                    repeat(7) { result.add("") }

                    val pluralElement = pluralTags.item(i) ?: continue
                    val itemNodes = pluralElement.getElementsByTagName("item")

                    for (j in 0 until itemNodes.length) {
                        val item = itemNodes.item(j) ?: continue
                        val quantity = item.getAttribute("quantity") ?: continue
                        val value = item.textContent ?: continue

                        val index = quantities.indexOf(quantity)
                        result[result.size - 6 + index] = value.trim()
                    }
                }
            }
        } catch (e: Exception) {
        }

        return result
    }
}