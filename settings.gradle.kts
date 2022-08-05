pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "plugin-extensions"
include(":app")

includeBuild("plugins") {
    dependencySubstitution {
//        substitute(module("com.jiaoay.plugins:core")).using(project(":core"))
        substitute(module("com.jiaoay.plugins:sdk-patcher")).using(project(":sdk-patcher"))
        substitute(module("com.jiaoay.plugins:sdk-patcher-extensions")).using(project(":sdk-patcher-extensions"))
    }
}
enableFeaturePreview("VERSION_CATALOGS")