plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
 namespace = "ir.iran.shahrbanoorebuild"
 compileSdk = 35
 defaultConfig { applicationId = "ir.iran.shahrbanoorebuild"; minSdk = 26; targetSdk = 35; versionCode = 1; versionName = "1.0-accessible" }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
 kotlinOptions { jvmTarget = "17" }
}
dependencies { }
