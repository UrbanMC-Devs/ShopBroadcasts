plugins {
    id("java")
    id ("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "net.lithosmc"
version = "1.0"

repositories {
    mavenCentral()
    // Paper
    maven (
        url= "https://papermc.io/repo/repository/maven-public/"
    )
    // Towny, Vault, VotingPlugin, CommentConfig
    maven (
        url = "https://jitpack.io"
    )
    // MiniMessage
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots"
    }
}

val commentConfigDep = "com.github.silverwolfg11:CommentConfig:v1.0.0"
val miniMessageDep = "net.kyori:adventure-text-minimessage:4.2.0-SNAPSHOT"

dependencies {
    compileOnly ("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly ("com.github.BenCodez:VotingPlugin:6.4.1")
    compileOnly ("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly ("com.github.TownyAdvanced:Towny:0.97.0.11")
    implementation(commentConfigDep)
    implementation(miniMessageDep)
}

// Configure plugins

// Java
java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

// Shadow Jar
tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveBaseName.set("ShopBroadcast")

    val pathPrefix = "net.lithosmc.shopbroadcast"

    relocate("me.Silverwolfg11.CommentConfig", "$pathPrefix.commentconfig")
    relocate("net.kyori.adventure.text.minimessage", "$pathPrefix.minimessage")

    dependencies {
        include(dependency(commentConfigDep))
        include(dependency(miniMessageDep))
    }
}