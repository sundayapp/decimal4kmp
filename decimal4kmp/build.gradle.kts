import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

buildscript {
	dependencies {
		classpath(libs.fmpp)
	}
}

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.androidLibrary)
	`maven-publish`
	alias(libs.plugins.sonarQube)
	alias(libs.plugins.kotest.multiplatform)
	alias(libs.plugins.googleKsp)
}

group = "org.decimal4kmp"
//x-release-please-start-version
version="0.0.1"
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

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().all {
	if(name != "kspCommonMainKotlinMetadata") {
		dependsOn("kspCommonMainKotlinMetadata")
	}
}

kotlin.sourceSets.commonMain {
	kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
}

android {
	namespace = "org.decimal4kmp"
	compileSdk = 33
	defaultConfig {
		compileSdk = 33
		minSdk = libs.versions.android.minSdk.get().toInt()
	}
}
