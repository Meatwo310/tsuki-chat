plugins {
    id("lexforge-legacy-mod-conventions")
}

val configuredVersion = project.property("configuredVersion").toString()

// Mod Dependencies
dependencies {
    modImplementation("maven.modrinth:craterlib:1.20-3.1.2+hotfix.1")
    modImplementation("maven.modrinth:sdlink:3.4.3")

    modRuntimeOnly(libs.configured) { version { require(configuredVersion) } }
    modRuntimeOnly("curse.maven:modern-ui-352491:6956345")
}
