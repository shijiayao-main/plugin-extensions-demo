plugins {
    id("com.android.application")
    id("com.jiaoay.plugins")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.jiaoay.plugin_extensions_demo"
    compileSdk = 32

    defaultConfig {
        applicationId = "com.jiaoay.plugin_extensions_demo"
        minSdk = 21
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    viewBinding {
        isEnabled = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

extensionsPlugin {
    isEnableSdkPatcher = true
}

pluginDemo {
    isEnable = false
    tag = "这是一个Demo"
}

dependencies {

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation("com.github.bumptech.glide:glide:4.13.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")

    implementation("com.jiaoay.plugins:core-extensions")
}