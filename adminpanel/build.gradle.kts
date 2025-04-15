plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("org.jetbrains.kotlin.kapt") // ✅ ADD THIS LINE to fix kapt issue
}

android {
    namespace = "com.example.adminpanel"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.adminpanel"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // AndroidX and Material
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Firebase
    implementation(libs.firebase.database)

    // Volley
    implementation(libs.volley)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // CircleImageView for profile image
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // OkHttp (Already added for Cloudinary Upload)
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    // Glide (image preview)
    implementation("com.github.bumptech.glide:glide:4.15.1")
    kapt("com.github.bumptech.glide:compiler:4.15.1")

    // JSON handling
    implementation("org.json:json:20210307")

    // Retrofit for Cloudinary API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Optional: Logging Interceptor for debugging network calls (very useful during testing)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Image Picker with Activity Result API
    implementation("androidx.activity:activity-ktx:1.7.0")
}
