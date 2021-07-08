package br.com.nataniel

import br.com.nataniel.plugins.configureRouting
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", watchPaths = listOf("classes", "resources")) {
        // Install template engine
        install(FreeMarker) {
            // Configure template folder
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }
        configureRouting()
    }.start(wait = true)
}
