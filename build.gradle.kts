// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.6.21"))
        classpath("com.jiaoay.plugins:core")
        classpath("com.jiaoay.plugins:plugin-demo")
    }
}

plugins {
    id("com.android.application") version "7.2.1" apply false
    id("com.android.library") version "7.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.6.21" apply false
//    alias(libs.plugins.androidApplication) apply false
//    alias(libs.plugins.androidLibrary) apply false
//    alias(libs.plugins.kotlinAndroid) apply false
}