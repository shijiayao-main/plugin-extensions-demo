pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("pluginLibs") {
            from(files("${rootDir}/plugin-extensions/gradle/plugin.libs.versions.toml"))
        }
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "plugin-extensions-demo"
include(":app")

includeBuild("plugin-extensions") {
    dependencySubstitution {
        substitute(module("com.jiaoay.plugins:core")).using(project(":core"))
        substitute(module("com.jiaoay.plugins:plugin-demo")).using(project(":plugin-demo"))
        substitute(module("com.jiaoay.plugins:core-extensions")).using(project(":core-extensions"))
        substitute(module("com.jiaoay.plugins:plugin-trace")).using(project(":plugin-trace"))
    }
}