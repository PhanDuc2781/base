import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.base_project"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.base_project"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions.add("type")

    productFlavors {
        create("MOKE") {
            dimension = "type"
        }

        create("API") {
            dimension = "type"
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".dev"
            isDebuggable = true
            buildConfigField("String", "API_ENDPOINT", "\"https://54.169.102.50/\"")
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "API_ENDPOINT", "\"https://54.169.102.50/\"")

            signingConfig = signingConfigs.getByName("debug")
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.kotlin.reflect)

    /*Navigation*/
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    /*Lifecycle aware*/
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)

    /*Room Database*/
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.room.compiler)
    annotationProcessor(libs.androidx.room.room.compiler)
    implementation(libs.androidx.room.ktx)

    /*Hilt*/
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    /*Retrofit*/
    implementation(libs.retrofit)

    /*OkHttp */
    implementation(platform(libs.okhttp.bom))
    // define any required OkHttp artifacts without version
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    /*Gson*/
    implementation(libs.converter.gson)

    /*Coil*/
    implementation(libs.coil)
    implementation(libs.coil.network.okhttp)

    /*Firebase*/
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)

    /*RxFFmpeg*/
    //noinspection Aligned16KB
    implementation("com.github.microshow:RxFFmpeg:4.9.0")

    /*EvenBus*/
    implementation("org.greenrobot:eventbus:3.3.1")
    /*WorkManager*/
    /*WorkManager*/
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)

    /*Rounder ImageView*/
    implementation("com.makeramen:roundedimageview:2.3.0")

    /*Swiperefreshlayout*/
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    /*Wave form*/
    implementation("com.github.lincollincol:amplituda:2.2.2")
    implementation("com.github.massoudss:waveformSeekBar:5.0.2")

    //Glide
    implementation("com.github.bumptech.glide:glide:5.0.5")

    /*Lottie*/
    implementation("com.airbnb.android:lottie:6.6.9")

    implementation("com.pnikosis:materialish-progress:1.7")

    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    implementation("io.coil-kt:coil:2.7.0")
    implementation("io.coil-kt:coil-gif:2.7.0")
    implementation("io.coil-kt:coil-video:2.7.0")


}