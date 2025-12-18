package app.mejourney

import app.mejourney.repository.HomeRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticFiles
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.io.File

internal fun Application.configureRouting() {

    val imagesPath = environment.config
        .property("mejourney.images.path")
        .getString()

    routing {

        staticFiles(
            remotePath = "/images",
            dir = File(imagesPath),
        )

        get("/covers") {
            call.respond(HomeRepository.getCovers())
        }

        get("/content") {
            val coverId = call.request.queryParameters["coverId"]
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Missing coverId",
                )

            val content = HomeRepository.getContentByCoverId(coverId)
                ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    "Content not found",
                )

            val contentPath = "contents/${content.id}"

            val contentImagesDir = File(imagesPath, contentPath)

            val supportedExtensions = listOf("jpg", "jpeg", "png", "gif", "webp", "heic")

            val fileNames = contentImagesDir.listFiles()
                ?.filter { file ->
                    file.isFile && supportedExtensions.contains(file.extension.lowercase())
                }
                ?.map { "/images/$contentPath/${it.name}" }
                ?: emptyList()

            call.respond(content.copy(imagesUrls = fileNames))
        }
    }
}
