// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    id("com.android.library") version "8.4.1" apply false

    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.dagger.hilt.android") version "2.56.1" apply false
    alias(libs.plugins.kotlin.compose) apply false

}


buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")// последняя на сегодня
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")

    }
}

