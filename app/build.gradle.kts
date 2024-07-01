plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}


android {
    namespace = "com.example.videopal"
    compileSdk = 34

    packagingOptions {
        exclude("androidsupportmultidexversion.txt")
    }


    buildFeatures{
        viewBinding = true
    }
    defaultConfig {
        applicationId = "com.example.videopal"
        minSdk = 19
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true


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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("com.airbnb.android:lottie:6.4.0")
    implementation ("com.mikhaellopez:circularimageview:4.3.1")

    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    implementation ("com.android.support:multidex:1.0.3")




}