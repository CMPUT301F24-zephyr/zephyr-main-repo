plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.plannet"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.plannet"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Firebase BoM and other needed dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-messaging")

    // UI
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // ZXing QR code scanner
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation(libs.legacy.support.v4)
    implementation("com.github.bumptech.glide:glide:4.11.0")
    implementation(libs.fragment.testing)
    implementation(libs.play.services.location)
    implementation(libs.rules)
    implementation(libs.play.services.maps)


    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")

    //Google Maps
    implementation("com.google.android.gms:play-services-maps:18.1.0")



    // For testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.6.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("org.mockito:mockito-android:4.6.1")
}





