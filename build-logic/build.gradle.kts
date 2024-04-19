plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.3.1")
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.51.1")

    val kotlinVersion = "1.9.23"  // keep in sync with devtools-ksp in [libs.versions.toml]
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")

    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.6")
    implementation("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:11.1.1")
    implementation("com.dynatrace.tools.android:gradle-plugin:8.287.1.1006")
}
