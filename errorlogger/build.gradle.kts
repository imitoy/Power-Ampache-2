import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "luci.sixsixsix.powerampache2.errorlogger"
    compileSdk = 36

    val properties = Properties()
    val propertiesFile = project.rootProject.file("secrets.properties")
    if (propertiesFile.exists()) {
        properties.load(propertiesFile.inputStream())
    } else {
        properties.load(project.rootProject.file("secretsnot.properties").inputStream())
    }

    val errorLogUrl = properties.getProperty("URL_ERROR_LOG")


    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "URL_ERROR_LOG", "$errorLogUrl")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
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
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.android)

    // --- Dagger Hilt --- //
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // --- Retrofit --- //
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}
