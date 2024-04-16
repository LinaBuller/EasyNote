

plugins {
    id ("com.android.application")
    id ("org.jetbrains.kotlin.android")
    id ("com.google.gms.google-services")
    id ("kotlin-parcelize")
    id ("kotlin-kapt")
    id ("androidx.navigation.safeargs")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20-Beta2"
}

android {
    compileSdk = 34

    defaultConfig {
        applicationId =  "com.buller.mysqlite"
        minSdk  = 28
        targetSdk = 34
        versionCode  = 1
        versionName  = "1.0"
        testInstrumentationRunner  = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            proguardFiles((getDefaultProguardFile("proguard-android-optimize.txt")), "proguard-rules.pro")
        }
    }
    packagingOptions {
        resources {
            excludes += arrayOf("META-INF/atomicfu.kotlin_module")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }


    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }
    namespace = "com.buller.mysqlite"
}

dependencies {
    implementation (project(":data"))
    implementation (project(":domain"))
    implementation("androidx.media3:media3-common:1.2.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0")

    val koin_version = "3.5.0"
    implementation ("io.insert-koin:koin-core:$koin_version")
    implementation ("io.insert-koin:koin-android:$koin_version")

    implementation ("androidx.preference:preference-ktx:1.2.1")
    implementation ("androidx.annotation:annotation:1.7.0")

    //Kotlin
    apply (plugin ="kotlin-kapt")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("androidx.activity:activity-ktx:1.8.1")
    implementation ("androidx.core:core-ktx:1.12.0")
    api ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    api ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")


    //UI
    implementation ("com.google.android.material:material:1.10.0")
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")

    implementation ("com.google.android.gms:play-services-auth:20.7.0")

    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
    implementation("com.google.firebase:firebase-database-ktx:20.3.0")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")

    implementation ("androidx.mediarouter:mediarouter:1.6.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")


    //Tests
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.arch.core:core-testing:2.2.0")

    //Other
    implementation ("com.larswerkman:lobsterpicker:1.0.1")
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //Lifecycle components
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-common-java8:2.6.2")

    //Room

    implementation("androidx.room:room-runtime:2.6.0")
    annotationProcessor("androidx.room:room-compiler:2.6.0")

    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:2.6.0")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.6.0")


    //ImagePicker
    implementation ("io.ak1.pix:piximagepicker:1.6.3")

    //Picasso
    implementation ("com.squareup.picasso:picasso:2.71828")

    implementation ("com.yarolegovich:discrete-scrollview:1.5.1")

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    kapt ("com.github.bumptech.glide:compiler:4.16.0")

    implementation ("codes.side:andcolorpicker:0.6.2")

    implementation ("com.google.code.gson:gson:2.10.1")

    implementation ("com.dolatkia:animated-theme-manager:1.1.4")

    implementation ("androidx.biometric:biometric:1.2.0-alpha05")

    implementation ("androidx.core:core-splashscreen:1.0.1")

    implementation ("com.airbnb.android:lottie:6.1.0")
    implementation ("androidx.preference:preference-ktx:1.2.1")

    implementation ("com.afollestad.material-dialogs:core:3.3.0")
    implementation ("com.afollestad.material-dialogs:input:3.3.0")
    implementation ("com.afollestad.material-dialogs:bottomsheets:3.3.0")
    implementation ("com.firebaseui:firebase-ui-auth:7.2.0")

}