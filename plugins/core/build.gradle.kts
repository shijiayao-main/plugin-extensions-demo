plugins {
    kotlin("jvm")
    kotlin("kapt")
    `java-gradle-plugin`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    api(gradleApi())
    api(kotlin("stdlib"))
    api(libs.kotlinReflect)
    api(group = "com.android.tools.build", name = "gradle", version = "7.2.1")
    kapt(libs.googleAutoService)
    api(libs.boosterApi)
    api(libs.boosterTransformAsm)
//    api(libs.androidBuildTools)
}