import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

apply(plugin = "com.github.johnrengelman.shadow")

dependencies {
    implementation(project(":discord-bucket-api"))
    implementation("com.charleskorn.kaml:kaml:0.35.2")
    implementation("ch.qos.logback:logback-classic:1.2.6")
}

tasks {
    withType<ShadowJar> {
        archiveFileName.set("Bucket-${rootProject.version}.jar")
        manifest {
            attributes("Main-Class" to "io.github.pikokr.bucket.BucketImpl")
        }
    }
}