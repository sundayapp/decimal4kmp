plugins {
	alias(libs.plugins.kotlin.multiplatform.android.library).apply(false)
	alias(libs.plugins.kotlin.multiplatform).apply(false)
	alias(libs.plugins.sonarQube).apply(false)
	alias(libs.plugins.googleKsp).apply(false)
	alias(libs.plugins.mavenPublish).apply(false)
}