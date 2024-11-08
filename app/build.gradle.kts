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
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.database)
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth)
    implementation(libs.fragment.testing)
    implementation(libs.rules)
    //implementation("androidx.core:core-ktx:1.7.0")
//    testImplementation(libs.junit)

//    androidTestImplementation(libs.ext.junit)
//    androidTestImplementation(libs.espresso.core)

    //implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    //implementation("com.google.zxing:core:3.4.1")

    // testing dependencies
    implementation("androidx.activity:activity:1.9.3")
    implementation("androidx.test.espresso:espresso-intents:3.6.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}