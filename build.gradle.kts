import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
	kotlin("multiplatform")
	id("org.jetbrains.compose")
}

group = "minerofmillions"
version = "1.0-SNAPSHOT"

repositories {
	google()
	mavenCentral()
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
	jvm {
		compilations.all {
			kotlinOptions.jvmTarget = "16"
		}
		withJava()
	}
	sourceSets {
		val jvmMain by getting {
			repositories {
				maven("https://jitpack.io")
			}
			dependencies {
				implementation(compose.desktop.currentOs)
				implementation("com.google.code.gson:gson:2.8.5")
				implementation("com.google.guava:guava:31.1-jre")
				implementation("com.arkivanov.decompose:decompose:0.8.0")
				implementation("com.arkivanov.decompose:extensions-compose-jetbrains:0.8.0")
				implementation("org.ojalgo:ojalgo:51.4.0")
			}
		}
		val jvmTest by getting
	}
}

compose.desktop {
	application {
		mainClass = "minerofmillions.recipeapp.MainKt"
		nativeDistributions {
			targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
			packageName = "recipeapp"
			packageVersion = "1.0.0"
		}
	}
}

tasks.register<Zip>("buildDistributable") {
	dependsOn("createDistributable")
	from(layout.buildDirectory.dir("compose/binaries/main/app"))
	archiveFileName.set("calculator.zip")
}
