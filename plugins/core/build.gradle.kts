plugins {
    kotlin("jvm")
    kotlin("kapt")
    `java-gradle-plugin`
    base
//    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    api(gradleApi())
    api(kotlin("stdlib"))
    api(libs.kotlinReflect)
//    kapt(libs.googleAutoService)
    api(libs.googleAutoService)
    annotationProcessor(libs.googleAutoService)

    api(libs.boosterAndroidGradleApi)
//    api(libs.boosterTransformSpi)
//    api(libs.boosterTransformUtil)

    api (libs.boosterBuild)
    api("org.apache.commons:commons-compress:1.21")
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.10.0.202012080955-r")

    api(libs.asm)
    api(libs.asmAnalysis)
    api(libs.asmCommons)
    api(libs.asmTree)
    api(libs.asmUtil)
//    compileOnly("com.android.tools.build:gradle:4.0.0")
    compileOnly(libs.androidBuildTools)
}

gradlePlugin {
    plugins {
        create("extensions") {
            id = "com.jiaoay.plugins"
            implementationClass = "com.jiaoay.plugins.core.ExtensionsPlugin"
        }
    }
}