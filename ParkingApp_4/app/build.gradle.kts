plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.parkingapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.parkingapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.opencsv:opencsv:5.8") // Use the latest version
    implementation("org.json:json:20230618")
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation ("com.intuit.sdp:sdp-android:1.1.1")
    implementation ("com.google.android.material:material:1.6.0")
    implementation ("org.apache.commons:commons-math3:3.6.1")
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-firestore:25.0.0")
    implementation ("com.google.firebase:firebase-auth:23.0.0")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("com.facebook.android:facebook-login:16.0.0")
    implementation ("com.facebook.android:facebook-android-sdk:latest.release")
    implementation ("com.intuit.sdp:sdp-android:1.1.1")
    implementation ("com.google.android.material:material:1.6.0")
    implementation ("org.apache.commons:commons-math3:3.6.1")
    implementation ("org.osmdroid:osmdroid-android:6.1.13")
    implementation ("com.google.android.gms:play-services-maps:19.0.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.maps:google-maps-services:2.2.0")
    implementation ("org.slf4j:slf4j-simple:1.7.25")
    implementation ("com.google.maps.android:android-maps-utils:2.3.0")
    implementation ("com.google.android.libraries.places:places:2.6.0")
    implementation ("androidx.core:core:1.9.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.0")
    implementation ("com.google.firebase:firebase-database:20.2.0")

}
//implementation ("com.google.gms:google-services:4.4.2") // Google Services plugin
//implementation ("com.google.firebase:firebase-core:21.3.0")  // Core library for Firebase