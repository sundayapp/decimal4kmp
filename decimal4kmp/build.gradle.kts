import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

buildscript {
	dependencies {
		classpath(libs.fmpp)
	}
}

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.androidLibrary)
	alias(libs.plugins.sonarQube)
	alias(libs.plugins.kotest.multiplatform)
	alias(libs.plugins.googleKsp)
	alias(libs.plugins.mavenPublish)
}

group = "org.decimal4kmp"
//x-release-please-start-version
version="0.1.2"
//x-release-please-end

kotlin {
	jvm()
	androidTarget {
		publishLibraryVariants("release")
		@OptIn(ExperimentalKotlinGradlePluginApi::class)
		compilerOptions {
			jvmTarget.set(JvmTarget.JVM_1_8)
		}
	}
	iosX64()
	iosArm64()
	iosSimulatorArm64()

	js(IR) {
		nodejs()
		browser()
		binaries.executable()
	}

	wasmJs {
		nodejs()
		binaries.executable()
	}


	sourceSets {
		androidMain {
			kotlin.srcDir("src/commonJvmAndroid/kotlin")
			dependencies {
				kotlin.srcDir("androidMain/kotlin")
			}
		}

		androidUnitTest {
			dependencies {

			}
		}

		jvmMain {
			kotlin.srcDir("src/commonJvmAndroid/kotlin")
			kotlin.srcDir("jvmMain/kotlin")
			resources.srcDir("jvmMain/resources")
		}


		val commonMain by getting {
			kotlin.srcDir("commonMain/kotlin")
			dependencies {
				implementation(kotlin("stdlib"))
			}
		}
		val commonTest by getting {
			dependencies {
				implementation(libs.junit)
				implementation(libs.junit.params)
				implementation(libs.kotlin.reflect)
			}
		}
	}
}

dependencies {
	add("kspCommonMainMetadata", project(":processor"))
	add("kspJvm", project(":processor"))
	add("kspAndroid", project(":processor"))
}

tasks.withType<KotlinCompilationTask<*>>().all {
	if(name != "kspCommonMainKotlinMetadata") {
		dependsOn("kspCommonMainKotlinMetadata")
	}
}

tasks.named("sourcesJar") {
	dependsOn(tasks.named("kspCommonMainKotlinMetadata"))
}

tasks.named("iosX64SourcesJar") {
	dependsOn(tasks.named("kspCommonMainKotlinMetadata"))
}
tasks.named("iosArm64SourcesJar") {
	dependsOn(tasks.named("kspCommonMainKotlinMetadata"))
}
tasks.named("iosSimulatorArm64SourcesJar") {
	dependsOn(tasks.named("kspCommonMainKotlinMetadata"))
}
tasks.named("wasmJsSourcesJar") {
	dependsOn(tasks.named("kspCommonMainKotlinMetadata"))
}
tasks.named("jsSourcesJar") {
	dependsOn(tasks.named("kspCommonMainKotlinMetadata"))
}

tasks {
	afterEvaluate {
		tasks.getByName("kspKotlinJvm").dependsOn(":decimal4kmp:kspCommonMainKotlinMetadata")
		tasks.getByName("kspDebugKotlinAndroid").dependsOn(":decimal4kmp:kspCommonMainKotlinMetadata")
		tasks.getByName("kspReleaseKotlinAndroid").dependsOn(":decimal4kmp:kspCommonMainKotlinMetadata")
	}
}

kotlin.sourceSets.commonMain {
	kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
}

android {
	namespace = "org.decimal4kmp"
	compileSdk = 35
	defaultConfig {
		compileSdk = 35
		minSdk = libs.versions.android.minSdk.get().toInt()
	}
}

mavenPublishing {
    // Define coordinates for the published artifact
    coordinates(
        groupId = "com.sundayapp",
        artifactId = "decimal4kmp",
		//x-release-please-start-version
        version = "0.1.2"
		//x-release-please-end
    )

    // Configure POM metadata for the published artifact
    pom {
        name.set("KMP Library for fast fixed-point arithmetic")
        description.set("Kotlin multiplatform library for fast fixed-point arithmetic based on longs with support for up to 18 decimal places")
        inceptionYear.set("2024")
        url.set("https://github.com/sundayapp/decimal4kmp")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

		// Specify developer information
		developers {
			developer {
				id.set("yoann.meste")
				name.set("Yoann Meste")
				email.set("yoann.meste@sundayapp.com")
			}
		}

        // Specify SCM information
        scm {
            url.set("https://github.com/sundayapp/decimal4kmp")
			connection.set("scm:git:git://github.com/sundayapp/decimal4kmp.git")
			developerConnection.set("scm:git:git://github.com/sundayapp/decimal4kmp.git")
        }
    }

    // Configure publishing to Maven Central
    publishToMavenCentral()

    // Enable GPG signing for all publications
    signAllPublications()
}
