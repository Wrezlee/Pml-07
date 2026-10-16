plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "putra.yanuar.pml06"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "putra.yanuar.pml06"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    val supabaseVersion = "2.3.1"
    val ktorVersion = "2.3.11"
    //menyesuaikan versi modul Supabase & Ktor agar konsisten
    implementation(platform("io.github.jan-tennert.supabase:bom:$supabaseVersion"))
    implementation(platform("io.ktor:ktor-bom:$ktorVersion"))
    //SDK Supabase Storage & Engine jaringan Ktor
    implementation("io.github.jan-tennert.supabase:storage-kt")
    implementation("io.ktor:ktor-client-android")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    //penampil gambar UI
    implementation("io.coil-kt:coil:2.6.0")
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}