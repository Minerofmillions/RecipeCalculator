import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("multiplatform")
	id("org.jetbrains.compose")
}

group = "minerofmillions"
version = "0.0.1"

repositories {
	google()
	mavenCentral()
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
	jvm {
		compilations.all {
			kotlinOptions.jvmTarget = "11"
		}
		withJava()
	}
	sourceSets {
		val jvmMain by getting {
			dependencies {
				implementation(compose.desktop.currentOs)
				implementation("com.google.code.gson:gson:2.9.0")
			}
		}
		val jvmTest by getting
	}
}

compose.desktop {
	application {
		mainClass = "minerofmillions.recipeapp.ui.MainKt"
		nativeDistributions {
			targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
			packageName = "recipeapp"
			packageVersion = "1.0.0"
		}
	}
}
