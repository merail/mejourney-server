package app.mejourney.repository

import app.mejourney.db.DatabaseFactory
import app.mejourney.dto.ContentDto

object ContentRepository {

    fun getAll(): List<ContentDto> {
        val sql = "SELECT * FROM content"
        val stmt = DatabaseFactory.connection.createStatement()
        val rs = stmt.executeQuery(sql)

        val result = mutableListOf<ContentDto>()
        while (rs.next()) {
            result += ContentDto(
                title = rs.getString("title"),
                text = rs.getString("text")
            )
        }

        rs.close()
        stmt.close()
        return result
    }
}