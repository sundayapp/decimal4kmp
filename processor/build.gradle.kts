val kspVersion: String by project

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

group = "com.example"
version = "1.0-SNAPSHOT"

kotlin {
    jvm()
    sourceSets {
        val jvmMain by getting {
            dependencies {

                implementation("org.freemarker:freemarker:2.3.33")

                implementation(libs.ksp.api)
            }
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.junit)
                implementation(libs.mockk)
            }
            kotlin.srcDir("src/test/kotlin")
        }
    }
}