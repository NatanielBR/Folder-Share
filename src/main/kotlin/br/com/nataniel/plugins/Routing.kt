package br.com.nataniel.plugins

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
            val jarFile = getJarFile()

            var files = jarFile.listFiles()?.toList() ?: listOf()

            // remove this file
            files = files.filterNot { it.name == jarFile.name }
            val data = mapOf(
                "folder" to jarFile.parentFile.name,
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
            val jar = getJarFile()
            // Resolve query path using jar file as root.
            val file = jar.resolve(path)

            if (file.exists()) {
                if (file.isDirectory) {
                    val list = file.listFiles()
                    if (list == null) {
                        call.respond(HttpStatusCode.BadGateway)
                        return@get
                    }
                    val data = mapOf(
                        "folder" to file.name,
                        "parent" to file.parentFile.relativeFile(jar).path,
                        "files" to list.toList().toRelativeFile(jar),
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
 * Get jar location as File using java class methods.
 */
private fun getJarFile(): File {
    return File(SimpleFile::class.java.protectionDomain.codeSource.location.toURI())
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
