pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
    
}
rootProject.name = "remote-jar-updater"
include("ssh-command-putty", "ssh-command-jsch", "ssh-commands-api")
