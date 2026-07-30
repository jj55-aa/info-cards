plugins {
    id("com.android.application")
}

android {
    namespace = "io.github.jj55_aa.info_cards"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.jj55_aa.info_cards.widget"
        minSdk = 23
        targetSdk = 34
        versionCode = 2
        versionName = "2.0"
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("org.json:json:20240303")
}
