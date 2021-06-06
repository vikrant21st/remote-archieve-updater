plugins {
    kotlin("jvm")
}
group = "vikrant21st.remote-archive-updater"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":ssh-commands-api"))
//    implementation("com.jcraft:jsch:0.1.55")
    api("com.jcraft:jsch:0.1.55")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}
