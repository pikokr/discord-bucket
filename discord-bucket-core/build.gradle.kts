plugins {
    kotlin("plugin.serialization") version "1.5.30"
}

//repositories {
//    mavenCentral()
//}

dependencies {
    implementation(project(":discord-bucket-api"))
    implementation("com.charleskorn.kaml:kaml:0.35.2")
}