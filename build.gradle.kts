import com.diffplug.gradle.spotless.SpotlessExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.9.22"))
        classpath("com.jiaoay.plugins:core")
        classpath("com.jiaoay.plugins:plugin-demo")
        classpath("com.jiaoay.plugins:plugin-trace")
    }
}

plugins {
    id("com.diffplug.spotless") version "6.25.0" apply false

    id("com.android.application") version "8.3.0" apply false
    id("com.android.library") version "8.3.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
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
                ktlint("0.50.0")
            }
            java {
                target("**/*.java")
                googleJavaFormat()
                indentWithSpaces(2)
                trimTrailingWhitespace()
                removeUnusedImports()
            }
            kotlinGradle {
                target("*.gradle.kts")
                ktlint("0.50.0")
            }
        }
    }
}