package com.anasmusa.portfolio

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import portfolio.composeapp.generated.resources.Res
import portfolio.composeapp.generated.resources.education_description_tuit_0
import portfolio.composeapp.generated.resources.education_tuit_field
import portfolio.composeapp.generated.resources.education_tuit_title
import portfolio.composeapp.generated.resources.ic_android
import portfolio.composeapp.generated.resources.ic_autostatus
import portfolio.composeapp.generated.resources.ic_bito
import portfolio.composeapp.generated.resources.ic_github
import portfolio.composeapp.generated.resources.ic_globe_24
import portfolio.composeapp.generated.resources.ic_globe_32
import portfolio.composeapp.generated.resources.ic_market
import portfolio.composeapp.generated.resources.ic_play
import portfolio.composeapp.generated.resources.ic_server
import portfolio.composeapp.generated.resources.ic_taxi
import portfolio.composeapp.generated.resources.ic_telegram
import portfolio.composeapp.generated.resources.job_description_unical_0
import portfolio.composeapp.generated.resources.job_description_unical_1
import portfolio.composeapp.generated.resources.job_description_unical_2
import portfolio.composeapp.generated.resources.job_description_unical_3
import portfolio.composeapp.generated.resources.job_description_unical_4
import portfolio.composeapp.generated.resources.job_description_unical_5
import portfolio.composeapp.generated.resources.job_description_unical_6
import portfolio.composeapp.generated.resources.job_description_unical_7
import portfolio.composeapp.generated.resources.job_description_unical_8
import portfolio.composeapp.generated.resources.job_description_unical_9
import portfolio.composeapp.generated.resources.job_description_upwork_0
import portfolio.composeapp.generated.resources.job_description_upwork_1
import portfolio.composeapp.generated.resources.job_description_upwork_2
import portfolio.composeapp.generated.resources.job_description_upwork_3
import portfolio.composeapp.generated.resources.job_position_unical
import portfolio.composeapp.generated.resources.job_position_upwork
import portfolio.composeapp.generated.resources.project_description_autostatus
import portfolio.composeapp.generated.resources.project_description_bito
import portfolio.composeapp.generated.resources.project_description_bito_0
import portfolio.composeapp.generated.resources.project_description_bito_1
import portfolio.composeapp.generated.resources.project_description_bito_2
import portfolio.composeapp.generated.resources.project_description_bito_3
import portfolio.composeapp.generated.resources.project_description_market
import portfolio.composeapp.generated.resources.project_description_market_0
import portfolio.composeapp.generated.resources.project_description_market_1
import portfolio.composeapp.generated.resources.project_description_market_2
import portfolio.composeapp.generated.resources.project_description_taxi
import portfolio.composeapp.generated.resources.project_description_taxi_0
import portfolio.composeapp.generated.resources.project_description_taxi_1
import portfolio.composeapp.generated.resources.project_description_taxi_2
import portfolio.composeapp.generated.resources.project_description_taxi_3


object Data {

    const val linkedin: String = "https://linkedin.com/in/anas-erkinjonov"
    const val github: String = "https://github.com/anaserkinov"
    const val email = "anaserkinjonov@gmail.com"
    const val telegram = "https://t.me/anas_erkinjonov"
    const val cv: String = ""

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

    val experience = listOf(
        Experience(
            "Unical",
            LocalDate(2021, 6, 1),
            null,
            Res.string.job_position_unical,
            listOf(
                Res.string.job_description_unical_0,
                Res.string.job_description_unical_1,
                Res.string.job_description_unical_2,
                Res.string.job_description_unical_3,
                Res.string.job_description_unical_4,
                Res.string.job_description_unical_5,
                Res.string.job_description_unical_6,
                Res.string.job_description_unical_7,
                Res.string.job_description_unical_8,
                Res.string.job_description_unical_9
            )
        ),
        Experience(
            "Upwork",
            LocalDate(2021, 2, 1),
            LocalDate(2021, 6, 1),
            Res.string.job_position_upwork,
            listOf(
                Res.string.job_description_upwork_0,
                Res.string.job_description_upwork_1,
                Res.string.job_description_upwork_2,
                Res.string.job_description_upwork_3
            )
        )
    )

    val education = listOf(
        Education(
            Res.string.education_tuit_title,
            Res.string.education_tuit_field,
            LocalDate(2019, 9, 1),
            LocalDate(2021, 4, 1),
            false,
            listOf(
                Res.string.education_description_tuit_0
            )
        )
    )

    val languages = listOf(
        Language("uz", "native"),
        Language("en", "b2")
    )

    val projects = listOf(
        Project(
            logo = Res.drawable.ic_bito,
            title = "Bito",
            systems = listOf(
                System(
                    icon = Res.drawable.ic_android,
                    type = "android"
                )
            ),
            startDate = LocalDate(2021, 6, 1),
            endDate = null,
            description = Res.string.project_description_bito,
            subDescription = listOf(
                Res.string.project_description_bito_0,
                Res.string.project_description_bito_1,
                Res.string.project_description_bito_2,
                Res.string.project_description_bito_3,
            ),
            isWhiteLabel = false,
            links = listOf(
                Link(
                    icon = Res.drawable.ic_play,
                    title = "Google Play",
                    link = "https://play.google.com/store/apps/details?id=uz.unical.bito.pro"
                ),
                Link(
                    icon = Res.drawable.ic_globe_24,
                    title = "Website",
                    link = "https://bito.uz/"
                )
            )
        ),
        Project(
            logo =  Res.drawable.ic_taxi,
            title = "Taxi Driver",
            systems = listOf(
                System(
                    icon = Res.drawable.ic_android,
                    type = "android"
                )
            ),            startDate = LocalDate(2023, 5, 1),
            endDate = null,
            description = Res.string.project_description_taxi,
            subDescription = listOf(
                Res.string.project_description_taxi_0,
                Res.string.project_description_taxi_1,
                Res.string.project_description_taxi_2,
                Res.string.project_description_taxi_3,
            ),
            isWhiteLabel = true,
            links = listOf(
                Link(
                    icon = Res.drawable.ic_play,
                    title = "Google Play",
                    link = "https://play.google.com/store/apps/details?id=uz.unical.salom.taxi.driver"
                )
            )
        ),
        Project(
            logo = Res.drawable.ic_market,
            title = "Market",
            systems = listOf(
                System(
                    icon = Res.drawable.ic_android,
                    type = "android"
                )
            ),            startDate = LocalDate(2023, 8, 1),
            endDate = null,
            description = Res.string.project_description_market,
            subDescription = listOf(
                Res.string.project_description_market_0,
                Res.string.project_description_market_1,
                Res.string.project_description_market_2
            ),
            isWhiteLabel = true,
            links = listOf(
                Link(
                    icon = Res.drawable.ic_play,
                    title = "Google Play",
                    link = "https://play.google.com/store/apps/details?id=uz.unical.cardin.market"
                )
            )
        )
    )

    val otherProjects = listOf(
        Project(
            logo = Res.drawable.ic_autostatus,
            title = "Autostatus",
            systems = listOf(
                System(
                    icon = Res.drawable.ic_globe_24,
                    type = "webapp"
                ),
                System(
                    icon = Res.drawable.ic_server,
                    type = "server"
                )
            ),
            startDate = LocalDate(2024, 12, 1),
            endDate = LocalDate(2025, 1, 1),
            description = Res.string.project_description_autostatus,
            subDescription = emptyList(),
            isWhiteLabel = false,
            links = listOf(
                Link(
                    icon = Res.drawable.ic_telegram,
                    title = "Telegram",
                    link = "https://t.me/autostatusrobot"
                ),
                Link(
                    icon = Res.drawable.ic_github,
                    title = "Github",
                    link = "https://github.com/anaserkinov/autostatus_web"
                )
            )
        )
    )

    class Experience(
        val company: String,
        val startDate: LocalDate,
        val endDate: LocalDate?,
        val position: StringResource,
        val items: List<StringResource>
    )

    class Education(
        val university: StringResource,
        val field: StringResource,
        val startDate: LocalDate,
        val endDate: LocalDate?,
        val completed: Boolean,
        val items: List<StringResource>
    )

    class Language(
        val code: String,
        val level: String
    )

    class Project(
        val logo: DrawableResource,
        val title: String,
        val systems: List<System>,
        val startDate: LocalDate,
        val endDate: LocalDate?,
        val description: StringResource,
        val subDescription: List<StringResource>,
        val isWhiteLabel: Boolean,
        val links: List<Link>
    )

    class System(
        val icon: DrawableResource,
        val type: String
    )

    class Link(
        val icon: DrawableResource,
        val title: String,
        val link: String
    )

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

