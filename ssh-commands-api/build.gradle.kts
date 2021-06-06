plugins {
    kotlin("jvm")
}
group = "vikrant21st.remote-archive-updater"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
//    implementation(kotlin("stdlib"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}
