package me.anasmusa.portfolio.core

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.MonthNames

object Localization {

    var lang: String = "en"
        set(value) {
            field = value
            dateFormatter = LocalDate.Format {
                monthName(MonthNames(getMonths()))
                chars(" ")
                year()
            }
        }

    lateinit var dateFormatter: DateTimeFormat<LocalDate>
        private set

    private fun getMonths() = if (lang == "en")
        MonthNames.ENGLISH_ABBREVIATED.names
    else if (lang == "uz")
        listOf(
            "Yanvar",
            "Fevral",
            "Mart",
            "Aprel",
            "May",
            "Iyun",
            "Iyul",
            "Avgust",
            "Sentabr",
            "Oktabr",
            "Noyabr",
            "Dekabr"
        )
    else
        listOf(
            "Yanvar",
            "Fevral",
            "Mart",
            "Aprel",
            "May",
            "Iyun",
            "Iyul",
            "Avgust",
            "Sentabr",
            "Oktabr",
            "Noyabr",
            "Dekabr"
        )

}