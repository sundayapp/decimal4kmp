plugins {
	alias(libs.plugins.androidLibrary).apply(false)
	alias(libs.plugins.kotlinMultiplatform).apply(false)
	alias(libs.plugins.sonarQube).apply(false)
	alias(libs.plugins.kotest.multiplatform).apply(false)
	alias(libs.plugins.googleKsp).apply(false)
	alias(libs.plugins.mavenPublish).apply(false)
}