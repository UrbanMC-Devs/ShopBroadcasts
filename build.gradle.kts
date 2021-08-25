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

dependencies {
    compileOnly ("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly ("com.github.BenCodez:VotingPlugin:6.4.1")
    compileOnly ("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly ("com.github.TownyAdvanced:Towny:0.97.0.11")
    implementation("net.kyori:adventure-text-minimessage:4.2.0-SNAPSHOT")
    implementation("com.github.silverwolfg11:CommentConfig:v1.0.0")
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

    relocate("me.Silverwolfg11.CommentConfig", "net.lithosmc.shopbroadcast.commentconfig")
    relocate("net.kyori.adventure.text.minimessage", "net.lithosmc.shopbroadcast.minimessage")

    dependencies {
        include(dependency("me.Silverwolfg11:CommentConfig:0.92"))
        include(dependency("net.kyori:adventure-text-minimessage:4.2.0-SNAPSHOT"))
    }
}