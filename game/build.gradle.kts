plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
 namespace = "ir.iran.shahrbanoorebuild"
 compileSdk = 35
 defaultConfig {
  applicationId = "ir.iran.shahrbanoorebuild"
  minSdk = 26
  targetSdk = 35
  versionCode = 2
  versionName = "1.1-accessible-signed"
 }
 signingConfigs {
  create("release") {
   storeFile = file("keystore/shahrbanoo-release.keystore")
   storePassword = "ShahrbanooRelease2026!"
   keyAlias = "shahrbanoo"
   keyPassword = "ShahrbanooRelease2026!"
  }
 }
 buildTypes {
  getByName("release") {
   isMinifyEnabled = false
   signingConfig = signingConfigs.getByName("release")
  }
  getByName("debug") {
   signingConfig = signingConfigs.getByName("release")
  }
 }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
 kotlinOptions { jvmTarget = "17" }
}
dependencies {
    implementation(files("libs/sherpa-onnx.aar"))
}
