package me.anasmusa.portfolio.core

import androidx.compose.runtime.Composable
import me.anasmusa.portfolio.LocalWindowSize
import me.anasmusa.portfolio.api.model.Link
import me.anasmusa.portfolio.api.model.LinkType
import me.anasmusa.portfolio.data.network.ApiClient
import org.jetbrains.compose.resources.DrawableResource
import portfolio.composeapp.generated.resources.Res
import portfolio.composeapp.generated.resources.ic_github
import portfolio.composeapp.generated.resources.ic_linkedin
import portfolio.composeapp.generated.resources.ic_resume
import portfolio.composeapp.generated.resources.ic_telegram

@Composable
fun isTablet() = LocalWindowSize.current.width >= 768

@Composable
fun deviceValue(portrait: Int, landscape: Int) = if (isTablet()) landscape else portrait

fun String.withDownloadBaseUrl(): String {
    return "${ApiClient.BASE_URL}download/$this"
}

fun Link.textAndIcon(): Pair<String, DrawableResource> {
    return when(type) {
        LinkType.LinkedIn -> {
            Pair("LinkedIn", Res.drawable.ic_linkedin)
        }
        LinkType.Github -> {
            Pair("GitHub", Res.drawable.ic_github)
        }
        LinkType.Telegram -> {
            Pair("Telegram", Res.drawable.ic_telegram)
        }
        else -> {
            Pair("CV", Res.drawable.ic_resume)
        }
    }
}

fun stringResource(key: Int) = Resource.strings[key]

fun stringResource(key: Int, arg: Any?): String{
    return stringResource(key, args = arrayOf(arg))
}

fun stringResource(key: Int, vararg args: Any?): String{
    var result: String = Resource.strings[key]
    args.forEach {
        result = result.replaceFirst("%s", it.toString())
    }
    return result
}
