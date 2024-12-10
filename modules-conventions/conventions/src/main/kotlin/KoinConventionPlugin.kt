import extensions.getBundle
import extensions.getLibrary
import extensions.implementation
import extensions.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class KoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target){

            with(pluginManager) {
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                val bom = libs.findLibrary("koin-bom").get()
                implementation(platform(bom))
                implementation(libs.getBundle("koin"))
                testImplementation(libs.getLibrary("koin-test"))
                testImplementation(libs.getLibrary("koin-test-junit4"))
            }

        }
    }
}