plugins {
    id("android-sdk")
    alias(libs.plugins.devtools.ksp)
}

android {
    namespace = "ch.admin.foitt.openid4vc"
}

dependencies {

    implementation(libs.androidx.core.ktx)

    // Dagger/Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // ktor
    debugImplementation(libs.slf4j.android)
    releaseImplementation(libs.slf4j.nop)
    implementation(libs.bundles.ktor)

    // JWT
    implementation(libs.nimbus.jose.jwt)

    // Error handling
    implementation(libs.kotlin.result)
    implementation(libs.kotlin.result.coroutines)

    // Logging
    implementation(libs.timber)

    // Testing
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.slf4j.nop)
    testImplementation(libs.archunit)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
