# plugin-extensions-demo

## 接入方式

找到你喜欢的位置

```bash
git submodule add git@github.com:shijiayao-main/plugin-extensions.git
```

settings.gradle.kts中添加, project("xxx")中xxx需要改为你刚刚喜欢的位置

```kotlin
includeBuild("plugin-extensions") {
    dependencySubstitution {
        substitute(module("com.jiaoay.plugins:core")).using(project(":core"))
        substitute(module("com.jiaoay.plugins:plugin-demo")).using(project(":plugin-demo"))
        substitute(module("com.jiaoay.plugins:core-extensions")).using(project(":core-extensions"))
    }
}
enableFeaturePreview("VERSION_CATALOGS")
```

根目录下build.gradle.kts中添加

```kotlin
buildscript {
    dependencies {
        ...
        classpath("com.jiaoay.plugins:core")
        classpath("com.jiaoay.plugins:plugin-demo")
    }
}
```

项目下build.gradle.kts中添加

```kotlin
plugins {
    ...
    id("com.jiaoay.plugins")
}
```

具体怎么用看代码

# plugin-extensions
