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
    implementation(project(":core"))
    implementation(project(":sdk-patcher-extensions"))
}