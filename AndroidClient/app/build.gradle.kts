import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import ru.vladislavsumin.build.Dependencies

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

androidExtensions {
    isExperimental = true
}

android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "ru.vladislavsumin.cams"
        minSdkVersion(24)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

//    dataBinding { isEnabled = true }

    signingConfigs {
        create("shared") {
            storeFile = file("../keystore/shared.keystore")
            storePassword = "Qwerty!@"
            keyAlias = "shared"
            keyPassword = "Qwerty!@"
        }
    }

    buildTypes {
        getByName("release") {
            //TODO rename to release pure
            isMinifyEnabled = true
            isDebuggable = true
            signingConfig = signingConfigs.getByName("shared")
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }

        getByName("debug") {
            signingConfig = signingConfigs.getByName("shared")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    (kotlinOptions as KotlinJvmOptions).apply {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    Dependencies.apply {
        implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
        implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))

        implementation(project(":DTO"))

        // design
        implementation(appCompat)
        implementation(constraintLayout)
        implementation(recyclerView)
        implementation(swipeRefreshLayout)
        implementation(material)
        implementation(cardView)
        implementation(swipeLayout)
        implementation(loadingButton)
        implementation("com.github.prolificinteractive:material-calendarview:2.0.1")


        implementation(dagger)
        kapt(daggerCompiler)
//        implementation("javax.annotation:javax.annotation-api:1.2")

        implementation(moxy)
        implementation(moxyAndroid)
        kapt(moxyCompiler)

        implementation(rxJava)
        implementation(rxKotlin)
        implementation(rxAndroid)

        implementation(retrofit)
        implementation(retrofitAdapterRxJava)
        implementation(retrofitConverterGson)

        //leakcanary
//        debugImplementation("com.squareup.leakcanary:leakcanary-android:2.0-alpha-3")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
        testImplementation("org.mockito:mockito-core:3.1.0")

        androidTestImplementation("androidx.test:runner:1.2.0")
        androidTestImplementation("androidx.test:rules:1.2.0")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")


//    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:+")

        // lifecycle
//    implementation("androidx.lifecycle:lifecycle-extensions:2.0.0")
//    implementation("androidx.lifecycle:lifecycle-runtime:2.0.0")
//    implementation("androidx.lifecycle:lifecycle-reactivestreams:2.0.0")
//    kapt("androidx.lifecycle:lifecycle-compiler:2.0.0")

        // paging
//    implementation("androidx.paging:paging-runtime-ktx:2.1.0")
//    testImplementation("androidx.paging:paging-common-ktx:2.1.0")
//    implementation("androidx.paging:paging-rxjava2-ktx:2.1.0")

        // room
    implementation("androidx.room:room-runtime:2.1.0")
    kapt("androidx.room:room-compiler:2.1.0")
    implementation("androidx.room:room-rxjava2:2.1.0")
//    testImplementation("androidx.room:room-testing:2.1.0-alpha04")
    }
}
