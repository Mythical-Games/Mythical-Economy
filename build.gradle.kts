import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.2.2"
}

// TODO: Update the group to yours
group = "com.mythicalgames.economy"
// TODO: Update the description to yours
description = "Rollout to AllayMC - The greatest MCBE server software OAT"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://storehouse.okaeri.eu/repository/maven-public")
}

dependencies {
    // TODO: Update the version of api to the latest
    compileOnly(group = "org.allaymc.allay", name = "api", version = "0.15.0")
    compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.34")
    implementation("eu.okaeri:okaeri-configs-yaml-snakeyaml:6.0.0-beta.1")
    implementation("org.mongodb:mongodb-driver-sync:4.7.1")
    implementation("org.xerial:sqlite-jdbc:3.45.1.0") 

    annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.34")
}

tasks.shadowJar {
    archiveFileName = "${project.name}-${version}-shaded.jar"
}
