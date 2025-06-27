import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import javax.xml.parsers.DocumentBuilderFactory

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        compilations.all {
            kotlinOptions.freeCompilerArgs += "-Xwasm-use-new-exception-proposal"
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
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
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
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
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

