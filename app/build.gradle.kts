
plugins {
    alias(libs.plugins.android.application)//ORIGINAL
    alias(libs.plugins.jetbrains.kotlin.android)//ORIGINAL
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.gastosdiarios.gavio"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gastosdiarios.gavio"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
    }
    composeOptions {
       kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    //google play services  dependencia base

    //credential manager
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    implementation(libs.coil.compose)
    implementation(libs.lottie.compose)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.appcompat)
    //Dagger Hilt
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)
    //firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.installations.ktx)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)//ORIGINAL
    implementation(libs.androidx.lifecycle.runtime.ktx)//ORIGINAL
    implementation(libs.androidx.activity.compose)//ORIGINAL
    implementation(platform(libs.androidx.compose.bom))//ORIGINAL
    implementation(libs.androidx.ui)//ORIGINAL
    implementation(libs.androidx.ui.graphics)//ORIGINAL
    implementation(libs.androidx.ui.tooling.preview)//ORIGINAL
    implementation(libs.androidx.material3)
    implementation(libs.google.firebase.auth)//ORIGINAL
    testImplementation(libs.junit)//ORIGINAL
    androidTestImplementation(libs.androidx.junit)//ORIGINAL
    androidTestImplementation(libs.androidx.espresso.core)//ORIGINAL
    androidTestImplementation(platform(libs.androidx.compose.bom))//ORIGINAL
    androidTestImplementation(libs.androidx.ui.test.junit4)//ORIGINAL
    debugImplementation(libs.androidx.ui.tooling)//ORIGINAL
    debugImplementation(libs.androidx.ui.test.manifest)//ORIGINAL
}