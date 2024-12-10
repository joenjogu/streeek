plugins {
    id("bizilabs.convention.application")
    id("bizilabs.convention.compose.application")
}

android {

    namespace = "com.bizilabs.app.streeek.mobile"

    defaultConfig {
        applicationId = "com.bizilabs.app.streeek.mobile"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {

}
