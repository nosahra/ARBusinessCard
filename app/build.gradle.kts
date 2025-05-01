
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
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // The BOM (Bill of Materials) aligns all Firebase library versions automatically
    implementation(platform(libs.firebase.bom))
    // Firestore
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

    testImplementation(libs.junit)

    // Mockito Core (Works with JUnit 4)
    testImplementation("org.mockito:mockito-core:5.11.0") // Use desired version

    // Mockito-Kotlin (Works with JUnit 4)
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1") // Use desired version

    // Coroutines Testing (Works with JUnit 4)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3") // Adjust version if needed

    // Robolectric gives you a fake Android runtime so Uri.parse() works in unit tests.


    testImplementation("org.robolectric:robolectric:4.11.1")



    // Optional: Mockito-inline (usually needed only for mocking final classes/methods if not using Mockito 5+)
    // testImplementation("org.mockito:mockito-inline:5.11.0")

    // --- END: Unit Test Dependencies ---


    // --- Android Test Dependencies (Remain unchanged) ---
    androidTestImplementation(libs.androidx.junit) // For instrumented tests
    androidTestImplementation(libs.androidx.espresso.core) // For instrumented tests
    androidTestImplementation(platform(libs.androidx.compose.bom)) // For instrumented tests
    androidTestImplementation(libs.androidx.ui.test.junit4) // For instrumented tests
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // --- End Android Test Dependencies ---

}

