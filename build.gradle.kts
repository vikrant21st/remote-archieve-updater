import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = "1.5.10"

plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.0"
    id("org.jetbrains.compose") version "0.4.0"
}

group = "vikrant21st.remote-archive-updater"
version = "1.0.0"

repositories {
//    mavenLocal()
//    jcenter()
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
    implementation("org.zeroturnaround:zt-zip:1.14")

    implementation(project(":ssh-commands-api"))
    implementation(project(":ssh-command-jsch"))
    implementation(project(":shell-commands"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
//    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "15"
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    ignoreFailures = true
}

compose.desktop {
    application {
        mainClass = "jetbrains.compose.classfileupdator.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe) // other formats: TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb
            packageName = "Remote Archive Updater"

            windows {
                iconFile.set(project.file("icon.ico"))
            }
        }
    }
}

extensions.findByName("buildScan")?.withGroovyBuilder {
    setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
    setProperty("termsOfServiceAgree", "yes")
}