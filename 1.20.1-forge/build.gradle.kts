plugins {
    id("legacyforge-mod-conventions")
}

val configuredVersion: String by project

// Mod Dependencies
dependencies {
    modImplementation("curse.maven:craterlib-867099:5574012")
    modImplementation("curse.maven:simple-discord-link-bot-forge-fabric-spigot-541320:5574099")

    modRuntimeOnly(libs.configured) { version { require(configuredVersion) } }
    modRuntimeOnly("curse.maven:modern-ui-352491:6199942")
}
