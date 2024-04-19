plugins {
    id("android-common")
    id("jacoco-android-sdk")
    id("com.android.library")
}

android {
    compileSdk = 34

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    kotlinOptions {
        allWarningsAsErrors = true
    }

    lint {
        lintConfig = file("$rootDir/config/lint/custom-lint.xml")
        quiet = false
        abortOnError = properties.getOrDefault("abortOnLintError", "true") == "true"

        // Warnings
        ignoreWarnings = false
        checkAllWarnings = true
        warningsAsErrors = false

        // If true, running lint on the app module will also run it on all the
        // dependent modules the app depends on. This way, lint has only be
        // invocated once and not again for each module.
        checkDependencies = true

        // Report formats and output paths.
        absolutePaths = false
        xmlReport = true
        htmlReport = true
    }

    testOptions {
        animationsDisabled = true
    }
}
