plugins {
    kotlin("jvm")
    kotlin("kapt")
    `java-gradle-plugin`
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    api(gradleApi())
    api(kotlin("stdlib"))
    api(libs.kotlinReflect)
    kapt(libs.googleAutoService)
    api(libs.boosterApi)
    api(libs.asm)
    api(libs.asmAnalysis)
    api(libs.asmCommons)
    api(libs.asmTree)
    api(libs.asmUtil)
    compileOnly("com.android.tools.build:gradle:4.0.0")
//    api(libs.androidBuildTools)
}

gradlePlugin {
    plugins {
        create("extensions") {
            id = "com.jiaoay.plugins"
            implementationClass = "com.jiaoay.plugins.core.ExtensionsPlugin"
        }
    }
}