package app.mejourney

import io.ktor.http.ContentType
import io.ktor.http.withCharset
import io.ktor.server.application.Application
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import kotlinx.serialization.json.Json

internal fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            json = Json { prettyPrint = true },
            contentType = ContentType.Application.Json.withCharset(Charsets.UTF_8),
        )
    }
}