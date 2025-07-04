import com.diffplug.gradle.spotless.SpotlessExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = pluginLibs.versions.kotlin.get()))
        classpath("com.jiaoay.plugins:core")
        classpath("com.jiaoay.plugins:plugin-demo")
        classpath("com.jiaoay.plugins:plugin-trace")
    }
}

plugins {
    alias(pluginLibs.plugins.spotless) apply false

    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(pluginLibs.plugins.kotlinAndroid) apply false
}

subprojects {
    project.afterEvaluate {
        apply(plugin = "com.diffplug.spotless")

        if (project.file("build.gradle").exists().not() && project.file("build.gradle.kts").exists()
                .not()
        ) {
            return@afterEvaluate
        }

        configure<SpotlessExtension>() {
            kotlin {
                target("**/*.kt")
                ktlint("1.6.0").editorConfigOverride(
                    mapOf(
                        "android" to "true",
                    ),
                )
            }
            java {
                target("**/*.java")
                googleJavaFormat()
                leadingTabsToSpaces(2)
                trimTrailingWhitespace()
                removeUnusedImports()
            }
            kotlinGradle {
                target("*.gradle.kts")
                ktlint("1.6.0")
            }
        }
    }
}