package app.mejourney.db

import io.ktor.server.application.ApplicationEnvironment
import java.sql.Connection
import java.sql.DriverManager

object DatabaseFactory {

    lateinit var connection: Connection
        private set

    fun init(environment: ApplicationEnvironment) {
        val dbPath = environment.config
            .property("mejourney.db.path")
            .getString()

        val jdbcUrl = "jdbc:sqlite:$dbPath"

        Class.forName("org.sqlite.JDBC")
        connection = DriverManager.getConnection(jdbcUrl)
    }
}