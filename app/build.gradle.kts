
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.dummy_database"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.dummy_database"
        minSdk = 24 // was 23 for my dummy_database
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.navigation.compose)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // The BOM (Bill of Materials) aligns all Firebase library versions automatically
    implementation(platform(libs.firebase.bom))
    // Firestore
    // implementation(libs.firebase.firestore)
    // (Optional) Firebase Auth if you plan to do sign in
    // implementation("com.google.firebase:firebase-auth")
    // (Optional) If you use Realtime Database
    // implementation(libs.firebase.database.ktx)
    // Import the BoM for the Firebase platform

    // Declare the dependencies for Firebase products
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    implementation(libs.kotlinx.coroutines.play.services)


    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies



    implementation(libs.zxing.core)     //generate qr code

    implementation(libs.zxing.android.embedded) {
        // If you only want Barcode scanning and not the full capture activity UI:
        // exclude group: "com.google.zxing"
        // exclude module: "core"
    }

// ARCore (Google Play Services for AR)
    implementation("com.google.ar:core:1.47.0")

// AppCompat library (needed for Theme.AppCompat, etc.)
    implementation("androidx.appcompat:appcompat:1.1.0")

// Lifecycle library that might be used by AR helpers
    implementation("androidx.lifecycle:lifecycle-common-java8:2.2.0")

// Material components (if your AR code references any Material widgets)
    implementation("com.google.android.material:material:1.1.0")

    implementation("de.javagl:obj:0.4.0")

    // Existing Compose and Android dependencies...
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
}

