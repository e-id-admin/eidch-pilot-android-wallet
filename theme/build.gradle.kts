plugins {
    id("android-sdk")
    alias(libs.plugins.devtools.ksp)
}

android {
    namespace = "ch.admin.foitt.pilotwallet.theme"

    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    // Dagger/Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Compose and material
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
}
