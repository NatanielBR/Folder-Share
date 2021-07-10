package br.com.nataniel

import br.com.nataniel.argsP.ArgsProcessor
import br.com.nataniel.dto.SimpleFile
import br.com.nataniel.plugins.configureRouting
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File

class WebSever {

    companion object {
        /**
         * Get jar location as File using java class methods.
         */
        lateinit var jarFile: File

        /**
         *
         */
        lateinit var args: ArgsProcessor
    }

}

fun main(args: Array<String>) {
    WebSever.args = ArgsProcessor(args)

    val argsProcessor = WebSever.args

    val path = if (argsProcessor.containsVariants("--folder", "-f")){
        argsProcessor.getVariantsOrDefault(
            "--folder", "-f",
            default = ""
        )
    }else{
        File(SimpleFile::class.java.protectionDomain.codeSource.location.toURI()).parentFile.absolutePath
    }

    embeddedServer(Netty,
        port = argsProcessor.getVariantsOrDefault("--port", "-p", default = "8080").toInt(),
        host = argsProcessor.getVariantsOrDefault("--bind", "-b", default = "127.0.0.1"),
        watchPaths = listOf("classes", "resources")) {
        WebSever.jarFile = File(path)
        log.info("Using '$path' as root folder.")
        // Install template engine
        install(FreeMarker) {
            // Configure template folder
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }
        configureRouting()
    }.start(wait = true)
}

