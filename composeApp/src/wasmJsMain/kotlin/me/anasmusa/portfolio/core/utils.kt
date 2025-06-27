package me.anasmusa.portfolio.core

import androidx.compose.runtime.Composable
import me.anasmusa.portfolio.LocalWindowSize

@Composable
fun isTablet() = LocalWindowSize.current.width >= 768

@Composable
fun select(portrait: Int, landscape: Int) = if (isTablet()) landscape else portrait

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
