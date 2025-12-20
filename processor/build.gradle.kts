import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val kspVersion: String by project

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "com.example"
version = "1.0-SNAPSHOT"

kotlin {
    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    sourceSets {
        jvmMain {
            dependencies {

                implementation(libs.freemarker)

                implementation(libs.ksp.api)
            }
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
        }

        jvmTest {
            dependencies {
                implementation(libs.junit)
                implementation(libs.mockk)
            }
            kotlin.srcDir("src/test/kotlin")
        }
    }
}