// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
// alias(libs.plugins.kotlin.compose) apply false // removed: plugin not available for Kotlin 1.9.24
    alias(libs.plugins.kotlin.compose) apply false // re-enabled for Kotlin 2.0.20
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.android) apply false
}
