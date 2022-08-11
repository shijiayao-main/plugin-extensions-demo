// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = libs.versions.kotlin.get()))
        classpath("com.jiaoay.plugins:core")
        classpath("com.jiaoay.plugins:plugin-demo")
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
}