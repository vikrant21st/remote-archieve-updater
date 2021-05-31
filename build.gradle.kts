import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = "1.4.32"

plugins {
    kotlin("jvm") version "1.4.32"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.4.32"
    id("org.jetbrains.compose") version "0.4.0-build188" // stable version is "0.3.0"
}

group = "me.vikrangh"
version = "1.0.0"

repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation( "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1") // use "1.1.0" to remove warning
    implementation(project(":ssh-commands-api"))
    implementation(project(":ssh-command-putty"))
    implementation(project(":ssh-command-jsch"))

    implementation("org.junit.jupiter:junit-jupiter:5.7.0")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions { 
		jvmTarget = "11"
	}
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

compose.desktop {
    application {
        mainClass = "jetbrains.compose.classfileupdator.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe) // other formats: TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb
            packageName = "Class File Uploader"
        }
    }
}
