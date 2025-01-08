plugins {
    id("bizilabs.convention.application")
    id("bizilabs.convention.koin")
    id("bizilabs.convention.compose.application")
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {

    namespace = "com.bizilabs.app.streeek.mobile"

    defaultConfig {
        applicationId = "com.bizilabs.app.streeek.mobile"
        versionCode = 2
        versionName = "0.0.2"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            applicationVariants.all {
                val variant = this
                variant.outputs
                    .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
                    .filter {
                        val names = it.name.split("-")
                        it.name.lowercase().contains(names[0], true) && it.name.lowercase().contains(names[1], true)
                    }
                    .forEach { output ->
                        val outputFileName = "streeek-release.apk"
                        output.outputFileName = outputFileName
                    }
            }

//            val variant =
//                    this.outputs as com.android.build.gradle.internal.api.BaseVariantOutputImpl
//                variant.outputFileName = "streeek-release.apk"
//            }
        }
//        val beta by creating {
//            applicationIdSuffix = ".beta"
//            versionNameSuffix = "-beta"
//        }
    }
}

dependencies {
    // work-manager
    implementation(libs.androidx.work.runtime)
}
