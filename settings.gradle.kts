rootProject.name = "discord-bucket"

include(
    "discord-bucket-api",
    "discord-bucket-core",
    "discord-bucket-plugins"
)

file("discord-bucket-plugins").listFiles { file ->
    file.isDirectory && file.name != "build"
}?.forEach { file ->
    include(":discord-bucket-plugins:${file.name}")
}