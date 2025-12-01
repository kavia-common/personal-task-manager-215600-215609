androidApplication {
    namespace = "org.example.app"

    dependencies {
        // Compose and Material3
        // Rely on Compose BOM for aligned versions; Material3 uses explicit stable version.
        implementation(platform("androidx.compose:compose-bom:2024.10.01"))
        implementation("androidx.activity:activity-compose:1.9.3")
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.material3:material3:1.3.0")

        // Lifecycle/ViewModel
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

        // Coroutines
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    }
}
