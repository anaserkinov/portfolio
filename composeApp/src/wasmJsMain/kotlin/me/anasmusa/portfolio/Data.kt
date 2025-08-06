package me.anasmusa.portfolio

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.MonthNames
import org.jetbrains.compose.resources.DrawableResource
import portfolio.composeapp.generated.resources.Res
import portfolio.composeapp.generated.resources.ic_android
import portfolio.composeapp.generated.resources.ic_autostatus
import portfolio.composeapp.generated.resources.ic_bito
import portfolio.composeapp.generated.resources.ic_compose
import portfolio.composeapp.generated.resources.ic_github
import portfolio.composeapp.generated.resources.ic_globe_24
import portfolio.composeapp.generated.resources.ic_market
import portfolio.composeapp.generated.resources.ic_play
import portfolio.composeapp.generated.resources.ic_portfolio
import portfolio.composeapp.generated.resources.ic_server
import portfolio.composeapp.generated.resources.ic_snowflake
import portfolio.composeapp.generated.resources.ic_taxi
import portfolio.composeapp.generated.resources.ic_telegram


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
            Strings.job_position_unical,
            listOf(
                Strings.job_description_unical_0,
                Strings.job_description_unical_1,
                Strings.job_description_unical_2,
                Strings.job_description_unical_3,
                Strings.job_description_unical_4,
                Strings.job_description_unical_5,
                Strings.job_description_unical_6,
                Strings.job_description_unical_7,
                Strings.job_description_unical_8,
                Strings.job_description_unical_9
            )
        ),
        Experience(
            "Upwork",
            LocalDate(2021, 2, 1),
            LocalDate(2021, 6, 1),
            Strings.job_position_upwork,
            listOf(
                Strings.job_description_upwork_0,
                Strings.job_description_upwork_1,
                Strings.job_description_upwork_2,
                Strings.job_description_upwork_3
            )
        )
    )

    val education = listOf(
        Education(
            Strings.education_tuit_title,
            Strings.education_tuit_field,
            LocalDate(2019, 9, 1),
            LocalDate(2021, 4, 1),
            false,
            listOf(
                Strings.education_description_tuit_0
            )
        )
    )

    val languages = listOf(
        Language("uz", Strings.uz, "native", Strings.native),
        Language("en", Strings.en, "b2", Strings.b2)
    )

    val skills = listOf(
        Skill(
            Strings.proficient,
            listOf(
                Skill.Item(
                    "Kotlin",
                    null
                ),
                Skill.Item(
                    "Java",
                    null
                )
            )
        ),
        Skill(
            null,
            listOf(
                Skill.Item(
                    "Jetpack Compose",
                    null
                ),
                Skill.Item(
                    "Dagger/Hilt",
                    null
                ),
                Skill.Item(
                    "Coroutines/RxJava",
                    null
                ),
                Skill.Item(
                    "SQLite",
                    null
                ),
                Skill.Item(
                    "Socket/RESTful APIs",
                    null
                ),
                Skill.Item(
                    "MVVM/MVI",
                    null
                ),
                Skill.Item(
                    "Unit/UI Testing",
                    null
                ),
                Skill.Item(
                    "Git",
                    null
                ),
                Skill.Item(
                    "Docker",
                    null
                )
            )
        ),
        Skill(
            Strings.competent,
            listOf(
                Skill.Item(
                    "Kotlin Multiplatform",
                    null
                ),
                Skill.Item(
                    "Compose Multiplatform",
                    null
                )
            )
        ),
        Skill(
            Strings.familiar,
            listOf(
                Skill.Item(
                    "C/C++",
                    null
                ),
                Skill.Item(
                    "Android NDK",
                    null
                ),
                Skill.Item(
                    "CMake",
                    null
                ),
                Skill.Item(
                    "GraphQL",
                    null
                )
            )
        )
    )

    val projects = listOf(
        Project(
            logo = Res.drawable.ic_bito,
            title = "Bito",
            systems = listOf(
                System(
                    icon = Res.drawable.ic_android,
                    title = Strings.android_app,
                    isTintUnspecified = true
                )
            ),
            startDate = LocalDate(2021, 6, 1),
            endDate = null,
            description = Strings.project_description_bito,
            subDescription = listOf(
                Strings.project_description_bito_0,
                Strings.project_description_bito_1,
                Strings.project_description_bito_2,
                Strings.project_description_bito_3,
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
                    title = Strings.android_app,
                    isTintUnspecified = true
                )
            ),
            startDate = LocalDate(2023, 5, 1),
            endDate = null,
            description = Strings.project_description_taxi,
            subDescription = listOf(
                Strings.project_description_taxi_0,
                Strings.project_description_taxi_1,
                Strings.project_description_taxi_2,
                Strings.project_description_taxi_3,
            ),
            isWhiteLabel = true,
            links = listOf(
                Link(
                    icon = Res.drawable.ic_play,
                    title = "Google Play",
                    link = "https://play.google.com/store/apps/details?id=uz.unical.salomtaxi.driver"
                )
            )
        ),
        Project(
            logo = Res.drawable.ic_market,
            title = "Market",
            systems = listOf(
                System(
                    icon = Res.drawable.ic_android,
                    title = Strings.android_app,
                    isTintUnspecified = true
                )
            ),
            startDate = LocalDate(2023, 8, 1),
            endDate = LocalDate(2024, 12, 1),
            description = Strings.project_description_market,
            subDescription = listOf(
                Strings.project_description_market_0,
                Strings.project_description_market_1,
                Strings.project_description_market_2
            ),
            isWhiteLabel = true,
            links = listOf(
                Link(
                    icon = Res.drawable.ic_play,
                    title = "Google Play",
                    link = "https://play.google.com/store/apps/details?id=uz.unical.uygaayt.markett"
                )
            )
        )
    )

    val otherProjects = listOf(
        Project(
            logo = Res.drawable.ic_portfolio,
            title = "Portfolio",
            systems = listOf(
                System(
                    icon = Res.drawable.ic_globe_24,
                    title = Strings.compose_web_app
                ),
                System(
                    icon = Res.drawable.ic_server,
                    title = Strings.ktor_backend_service
                )
            ),
            startDate = LocalDate(2025, 1, 1),
            endDate = LocalDate(2025, 1, 1),
            description = Strings.project_description_portfolio,
            subDescription = listOf(
                Strings.project_description_portfolio_0,
                Strings.project_description_portfolio_1
            ),
            isWhiteLabel = false,
            links = listOf(
                Link(
                    icon = Res.drawable.ic_globe_24,
                    title = "Website",
                    link = "https://anasmusa.me/portfolio"
                ),
                Link(
                    icon = Res.drawable.ic_github,
                    title = "Github",
                    link = "https://github.com/anaserkinov/portfolio"
                )
            )
        ),
        Project(
            logo = Res.drawable.ic_snowflake,
            title = "Snowflake",
            systems = listOf(
                System(
                    icon = Res.drawable.ic_compose,
                    title = Strings.compose_lib,
                    isTintUnspecified = true
                )
            ),
            startDate = LocalDate(2024, 1, 1),
            endDate = LocalDate(2024, 1, 1),
            description = Strings.project_description_snowflake,
            subDescription = listOf(
                Strings.project_description_snowflake_0,
                Strings.project_description_snowflake_1
            ),
            isWhiteLabel = false,
            links = listOf(
                Link(
                    icon = Res.drawable.ic_github,
                    title = "Github",
                    link = "https://github.com/anaserkinov/snowflake"
                )
            )
        ),
        Project(
            logo = Res.drawable.ic_autostatus,
            title = "Autostatus",
            systems = listOf(
                System(
                    icon = Res.drawable.ic_globe_24,
                    title = Strings.react_web_app
                ),
                System(
                    icon = Res.drawable.ic_server,
                    title = Strings.ktor_backend_service
                )
            ),
            startDate = LocalDate(2024, 1, 1),
            endDate = LocalDate(2024, 1, 1),
            description = Strings.project_description_autostatus,
            subDescription = listOf(
                Strings.project_description_autostatus_0,
                Strings.project_description_autostatus_1
            ),
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
        val position: Int,
        val items: List<Int>
    )

    class Education(
        val university: Int,
        val field: Int,
        val startDate: LocalDate,
        val endDate: LocalDate?,
        val completed: Boolean,
        val items: List<Int>
    )

    class Language(
        val code: String,
        val name: Int,
        val level: String,
        val levelName: Int
    )

    class Skill(
        val title: Int?,
        val items: List<Item>
    ){
        class Item(
            val title: String,
            val description: Int?
        )
    }

    class Project(
        val logo: DrawableResource,
        val title: String,
        val systems: List<System>,
        val startDate: LocalDate,
        val endDate: LocalDate?,
        val description: Int,
        val subDescription: List<Int>,
        val isWhiteLabel: Boolean,
        val links: List<Link>
    )

    class System(
        val icon: DrawableResource,
        val title: Int,
        val isTintUnspecified: Boolean = false
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

