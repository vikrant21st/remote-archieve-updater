pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
    
}
rootProject.name = "remote-archive-updater"
include("ssh-command-putty", "ssh-command-jsch", "ssh-commands-api")
