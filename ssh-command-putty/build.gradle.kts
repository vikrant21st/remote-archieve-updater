plugins {
    kotlin("jvm")
}
group = "me.vikrangh"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":ssh-commands-api"))
    implementation("com.lordcodes.turtle:turtle:0.5.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}
