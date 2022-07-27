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
        substitute(module("com.jiaoay.plugins:sdk-patcher")).using(project(":sdk-patcher"))
        substitute(module("com.jiaoay.plugins:sdk-patcher-extensions")).using(project(":sdk-patcher-extensions"))
    }
}
