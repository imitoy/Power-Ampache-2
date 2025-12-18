pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Power Ampache 2"
include(":app")
include(":domain")
include(":data-ampache")
include(":MrLog")
include(":PowerAmpache2Theme")
include(":CrashReportHandling")
include(":errorlogger")
