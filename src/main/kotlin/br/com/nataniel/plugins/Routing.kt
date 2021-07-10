package br.com.nataniel.plugins

import br.com.nataniel.WebSever
import br.com.nataniel.dto.SimpleFile
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File
import java.nio.file.Path

fun Application.configureRouting() {

    routing {
        /**
         * The root path is a jar folder.
         */
        get("/") {

            val jarFile = WebSever.jarFile
            var files = jarFile.listFiles()?.toList() ?: listOf()
            log.debug("Discovered ${files.size} from: ${jarFile.absolutePath}'")

            // remove this file
            files = files.filterNot { it.name == jarFile.name }

            val parent = jarFile.parentFile ?: jarFile

            val data = mapOf(
                "folder" to parent.name,
                "parent" to "",
                "files" to files.toRelativeFile(jarFile),
            )

            call.respond(FreeMarkerContent("index.ftl", data, ""))
        }

        /**
         * Any path is catched this route.
         */
        get("/{path...}") {
            val parts = call.parameters.getAll("path")

            if (parts == null) {
                call.respondRedirect("/")
                return@get
            }

            val path = parts.joinToString("/")
            log.debug("path -> $path")
            val jar = WebSever.jarFile
            // Resolve query path using jar file as root.
            val file = jar.resolve(path)

            if (file.exists()) {
                if (file.isDirectory) {
                    val files = file.listFiles()
                    if (files == null) {
                        call.respond(HttpStatusCode.BadGateway)
                        return@get
                    }

                    log.debug("Discovered ${files.size} from: ${file.absolutePath}'")

                    val data = mapOf(
                        "folder" to file.name,
                        "parent" to file.parentFile.relativeFile(jar).path,
                        "files" to files.toList().toRelativeFile(jar),
                    )

                    call.respond(FreeMarkerContent("index.ftl", data, ""))
                } else {
                    call.respondFile(file)
                }
            }else{
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }

}

/**
 * Util method to get a relative path from parent.
 */
fun File.relativeFile(parent: File): SimpleFile {
    val converted: Path = parent.toPath().relativize(toPath())

    // Files.isDirectory method not work, maybe because Path.relativize method.
    // Then i use isDirectory() from target file (the this).
    return SimpleFile(converted.fileName.toString(), converted.toString(), isDirectory)
}

/**
 * Util method to convert list of File to list of SimpleFile (The DTO object).
 */
fun List<File>.toRelativeFile(parent: File): List<SimpleFile> {
    return this.map { it.relativeFile(parent) }
}
