pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
    
}
rootProject.name = "remote-archive-updater"
include("ssh-commands-api")
include("ssh-command-jsch")
include("shell-commands")
