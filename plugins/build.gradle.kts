buildscript {
    dependencies {
        classpath(libs.kotlinGradlePlugin)
    }
}

plugins {
    alias(libs.plugins.kotlin) apply false
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}