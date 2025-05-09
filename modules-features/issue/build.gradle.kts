plugins {
    id("bizilabs.convention.feature")
}

android {
    namespace = "com.bizilabs.streeek.feature.issue"
}

dependencies {
    // paging
    implementation(libs.bundles.paging)
    // coil
    implementation(libs.bundles.coil)
    // richtexteditor
    implementation(libs.richtexteditor)
    // compose markdown
    implementation(libs.compose.markdown)
}
