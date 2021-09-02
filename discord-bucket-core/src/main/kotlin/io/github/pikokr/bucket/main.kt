package io.github.pikokr.bucket

suspend fun main() {
    Bucket.run()
//    val pluginManager = BucketPluginManager()
//    val pluginFolder = File("plugins").apply {
//        if (!exists()) {
//            mkdirs()
//        }
//    }
//
//    pluginManager.loadPlugins(pluginFolder)
//
//    println("Start enabling plugins")
//    pluginManager.enablePlugins()
//    println("Enabled plugins")
//    println()
//
//    while (true) {
//        val x = readLine()!!
//        when (x.split(" ")[0]) {
//            "stop" -> exitProcess(0)
//            "rl", "reload" -> {
//                println("Start unloading plugins")
//                pluginManager.unloadPlugins()
//                println()
//
//                println("Start loading plugins")
//                pluginManager.loadPlugins(pluginFolder)
//                println()
//
//                println("Start enabling plugins")
//                pluginManager.enablePlugins()
//                println()
//            }
//            "pl", "plugins" -> {
//                println("Plugins (${pluginManager.plugins.size}): ${pluginManager.plugins.joinToString(", ")}")
//                println()
//            }
//        }
//    }
}