plugins {
    kotlin("jvm") version "1.5.30"
}

group = "io.github.pikokr"
version = "1.0-SNAPSHOT"
//
//repositories {
//    mavenCentral()
//}
//
//dependencies {
//    implementation(kotlin("stdlib"))
//}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
//        # For Snapshot Only
//        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation("dev.kord:kord-core:0.8.0-M5")
    }
}