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

rootProject.name = "plugin-extensions-demo"
include(":app")

includeBuild("plugins") {
    dependencySubstitution {
        substitute(module("com.jiaoay.plugins:core")).using(project(":core"))
        substitute(module("com.jiaoay.plugins:plugin-demo")).using(project(":plugin-demo"))
        substitute(module("com.jiaoay.plugins:core-extensions")).using(project(":core-extensions"))
    }
}
enableFeaturePreview("VERSION_CATALOGS")