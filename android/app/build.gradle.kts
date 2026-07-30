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
        versionCode = 3
        versionName = "2.1"
    }

    signingConfigs {
        create("fixed") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("fixed")
        }
        release {
            signingConfig = signingConfigs.getByName("fixed")
        }
    }
}
