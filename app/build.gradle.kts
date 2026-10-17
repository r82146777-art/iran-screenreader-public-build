plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }

android {
    namespace = "ir.iran.screenreader"
    compileSdk = 35
    defaultConfig {
        applicationId = "ir.iran.screenreader"
        minSdk = 26
        targetSdk = 35
        versionCode = 6
        versionName = "4.0.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.mlkit:text-recognition:16.0.1")
    implementation("com.google.mlkit:image-labeling:17.0.9")
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    implementation("com.google.mlkit:language-id:17.0.6")
}
