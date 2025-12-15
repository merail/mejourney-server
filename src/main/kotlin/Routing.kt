package app.mejourney

import app.mejourney.repository.ContentRepository
import app.mejourney.repository.CoverRepository
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticFiles
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.io.File

fun Application.configureRouting() {

    val imagesPath = environment.config
        .property("mejourney.images.path")
        .getString()

    routing {

        staticFiles(
            remotePath = "/images",
            dir = File(imagesPath),
        )

        get("/covers") {
            call.respond(CoverRepository.getAll())
        }

        get("/content") {
            call.respond(ContentRepository.getAll())
        }
    }
}
