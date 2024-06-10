plugins {
    id("android-application")
    id("jacoco-android-app")
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.junit5)
}

android {
    namespace = "ch.admin.foitt.pilotwallet"

    val schemeCredentialOffer = "openid-credential-offer"
    val schemePresentationRequest = "https"

    defaultConfig {
        applicationId = "ch.admin.foitt.pilotwallet"
        testApplicationId = "ch.admin.foitt.pilotwallet.test"
        versionCode = Integer.parseInt(properties.getOrDefault("APP_VERSION_CODE", "1") as String)
        versionName = properties.getOrDefault("APP_VERSION_NAME", "1.0.0") as String
        manifestPlaceholders["appLabel"] = "pilotWallet"
        manifestPlaceholders["deepLinkCredentialOfferScheme"] = schemeCredentialOffer
        manifestPlaceholders["deepLinkPresentationRequestScheme"] = schemePresentationRequest

        buildConfigField(
            type = "String", name = "SCHEME_CREDENTIAL_OFFER", value = "\"$schemeCredentialOffer\""
        )

        buildConfigField(
            type = "String", name = "SCHEME_PRESENTATION_REQUEST", value = "\"$schemePresentationRequest\""
        )
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    signingConfigs {
        create("release") {
            storeFile = properties["RELEASE_STORE_FILE"]?.let { file(it) }
            storePassword = properties["RELEASE_STORE_PASSWORD"] as String?
            keyAlias = properties["RELEASE_KEY_ALIAS"] as String?
            keyPassword = properties["RELEASE_KEY_PASSWORD"] as String?
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            manifestPlaceholders["appLabel"] = "(DEV) pilotWallet"
        }

        create("ref") {
            dimension = "environment"
            applicationIdSuffix = ".ref"
            manifestPlaceholders["appLabel"] = "(REF) pilotWallet"
        }

        create("abn") {
            dimension = "environment"
            manifestPlaceholders["appLabel"] = "(ABN) pilotWallet"
        }

        create("prod") {
            dimension = "environment"
        }
    }

    applicationVariants.all {
        addJavaSourceFoldersToModel(
            layout.buildDirectory.dir("generated/ksp/$name/kotlin").get().asFile
        )
    }
    ksp {
        arg(
            "compose-destinations.codeGenPackageName", "ch.admin.foitt.pilotwalletcomposedestinations"
        )
        arg("room.generateKotlin", "true")
    }
}

dependencies {
    implementation(project(":theme"))
    implementation(project(":openid4vc"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.process)

    // Compose BOM
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.constraintlayout.compose)

    implementation(libs.androidx.core.splashscreen)

    // navigation
    implementation(libs.compose.destinations)
    ksp(libs.compose.destinations.ksp)

    // biometrics
    implementation(libs.androidx.biometric)

    // scanner
    implementation(libs.bundles.androidx.camera)
    implementation(libs.zxing.cpp)

    // security
    implementation(libs.androidx.security.crypto)

    // Dagger/Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)

    // Room / Sqlcipher
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.sqlcipher.android)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // JSON / JWT
    implementation(libs.nimbus.jose.jwt)
    implementation(libs.json.path)

    // Logging
    implementation(libs.timber)

    // Error handling
    implementation(libs.kotlin.result)
    implementation(libs.kotlin.result.coroutines)

    // Images
    implementation(libs.coil)
    implementation(libs.coil.compose)

    // AboutLibraries
    implementation(libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Testing
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.archunit)
    testImplementation(libs.mockk)
    testImplementation(libs.androidx.room.testing)

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter.params)

    androidTestImplementation(libs.junit.jupiter.api)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.android.test.compose)
}
