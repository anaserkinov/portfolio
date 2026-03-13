import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import javax.xml.parsers.DocumentBuilderFactory

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        compilerOptions {
            freeCompilerArgs.add("-Xwasm-use-new-exception-proposal")
        }
        provider {
            outputModuleName = "composeApp"
        }
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static(rootDirPath)
                    static(projectDirPath)
               }
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlin.serialization)
            implementation(libs.shimmer)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization)

            implementation(projects.shared)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
    }
}

tasks.register("generateStringResources") {
    val inputFile = file("src/commonMain/resources/assets/strings.xml")
    val outputFile = file("src/wasmJsMain/kotlin/me/anasmusa/portfolio/Strings.kt")

    doLast {
        val orders = HashMap<String, Int>()
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputFile)
        doc.documentElement.normalize()

        var pluralFirstIndex: Int
        val constants = mutableListOf<String>()

        let {
            val strings = doc.getElementsByTagName("string")
            repeat(strings.length){
                val index = constants.size
                val node = strings.item(it)
                val name = node.attributes.getNamedItem("name").nodeValue
                orders[name] = index
                constants.add("    const val $name = ${index + 1}")
            }
        }

        let {
            pluralFirstIndex = constants.size + 1
            val plurals = doc.getElementsByTagName("plurals")
            var index = constants.size
            repeat(plurals.length){
                val node = plurals.item(it)
                val name = node.attributes.getNamedItem("name").nodeValue
                orders[name] = index
                constants.add("    const val $name = ${index + 1}")
                index += 7
            }
        }

        val content = """
            |package me.anasmusa.portfolio
            |
            |object Strings {
            |
            |    internal const val pluralFirstIndex = $pluralFirstIndex
            |
            |${constants.joinToString("\n")}
            |}
        """.trimMargin()

        outputFile.parentFile.mkdirs()
        outputFile.writeText(content)

        val sourceFile = file("src/commonMain/resources/assets/strings.xml")
        val targetDir = file("$rootDir/composeApp/src/wasmJsMain/resources/values/")
        targetDir.mkdirs()
        sourceFile.copyTo(targetDir.resolve("strings.xml"), overwrite = true)
    }
}

