plugins {
    `java-library`
    `maven-publish`
    idea
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenLocal()
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://jitpack.io")
    maven("https://repo.opencollab.dev/maven-releases/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
}

group = "cn.powernukkitx"
version = "1.0.0-SNAPSHOT"
description = "powernukkitx example plugin"
java.sourceCompatibility = JavaVersion.VERSION_21

dependencies {
    //Check it out https://jitpack.io/#PowerNukkitX/PowerNukkitX
    compileOnly("com.github.PowerNukkitX:PowerNukkitX:master-SNAPSHOT")
    //local dependency
    //compileOnly(files("D:\\IdeaProjects\\PowerNukkitX\\build\\powernukkitx-2.0.0-SNAPSHOT-all.jar"))
}

//Automatically download dependencies source code
idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = false
    }
}

java {
    withSourcesJar()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

sourceSets {
    main {
        resources {
            srcDirs("src/main/resources")
        }
    }
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xpkginfo:always")
    java.sourceCompatibility = JavaVersion.VERSION_21
    java.targetCompatibility = JavaVersion.VERSION_21
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(listOf("--add-opens", "java.base/java.lang=ALL-UNNAMED"))
    jvmArgs(listOf("--add-opens", "java.base/java.io=ALL-UNNAMED"))
}

tasks.withType<AbstractCopyTask>() {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
