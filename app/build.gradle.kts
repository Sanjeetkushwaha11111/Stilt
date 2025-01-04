plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.gsm)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-android")
    id("kotlin-parcelize")
}
val bType = ""
android {
    namespace = "com.ourstilt"
    compileSdk = 35

    defaultConfig {
        buildConfigField("boolean", "lazyLoad", "${"lazyLoad" == bType}")
        applicationId = "com.ourstilt"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.incremental" to "true")
            }
        }
        sourceSets {
            getByName("main") {
                jniLibs.srcDirs("libs")
            }
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(fileTree("libs") { include("*.jar", "*.aar") })

    //Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.multidex)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.play.services.auth.api.phone)
    implementation(libs.activity.ktx)
    implementation(libs.androidx.core.animation)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.lifecycle.extensions)
    implementation (libs.androidx.navigation.ui.ktx)
    implementation (libs.flexbox)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.dynamic.links.ktx)
    implementation(libs.firebase.analytics)


    // Dagger Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.work)

    //ImageLoading
    implementation(libs.glide)

    //LocationService
    implementation(libs.play.services.location)

    //PixelIndependent
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)

    //Retrofit
    implementation(libs.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)
    implementation(libs.retrofit2.kotlin.coroutines.adapter)

    //Essentials
    implementation(libs.timber)
    implementation(libs.truecaller.sdk)
    implementation(libs.lottie)
    implementation(libs.jsoup)

    implementation (libs.androidx.work.runtime.ktx)
    implementation (libs.kotlinx.coroutines.android)
    //Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}