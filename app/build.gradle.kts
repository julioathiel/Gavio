plugins {
    alias(libs.plugins.android.application)//ORIGINAL
    alias(libs.plugins.jetbrains.kotlin.android)//ORIGINAL
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
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
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
       // jvmTarget = "1.8"
        jvmTarget = "11"
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
    implementation (libs.accompanist.systemuicontroller)//agregado 20/12/2024
    //splash screen
    implementation(libs.androidx.core.splashscreen)
    //hilt navigation compose
    implementation(libs.androidx.hilt.navigation.compose)//agregado 18/12/2024
    //autenticacion biometrica
    implementation(libs.androidx.biometric)//agregado 16/12/2024

    //refrescar pantalla
    implementation(libs.androidx.swiperefreshlayout)

    //credential manager
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    implementation(libs.coil.compose)
    implementation(libs.lottie.compose)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.runtime.livedata)
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

    implementation(libs.kotlinx.serialization.json)
}