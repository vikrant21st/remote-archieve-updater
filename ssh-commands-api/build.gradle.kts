plugins {
    kotlin("jvm")
}
group = "me.vikrangh"
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
