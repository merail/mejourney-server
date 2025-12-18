package app.mejourney

import app.mejourney.db.DatabaseFactory
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

internal fun main(args: Array<String>) {
    EngineMain.main(args)
}

internal fun Application.module() {
    DatabaseFactory.init(environment)
    configureRouting()
    configureSerialization()
}
