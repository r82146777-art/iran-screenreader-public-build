plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }

android {
    namespace = "ir.iran.screenreader"
    compileSdk = 35
    defaultConfig {
        applicationId = "ir.iran.screenreader"
        minSdk = 26
        targetSdk = 35
        versionCode = 7
        versionName = "5.0.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
}
