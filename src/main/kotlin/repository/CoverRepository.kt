package app.mejourney.repository

import app.mejourney.db.DatabaseFactory
import app.mejourney.dto.CoverDto
import java.sql.ResultSet

object CoverRepository {

    fun getAll(): List<CoverDto> {
        val sql = "SELECT * FROM cover"
        val stmt = DatabaseFactory.connection.createStatement()
        val rs = stmt.executeQuery(sql)

        val result = mutableListOf<CoverDto>()
        while (rs.next()) {
            result += rs.toCoverDto()
        }

        rs.close()
        stmt.close()
        return result
    }

    private fun ResultSet.toCoverDto() = CoverDto(
        id = getString("id"),
        year = getLong("year"),
        country = getString("country"),
        place = getString("place"),
        title = getString("title"),
        description = getString("description")
    )
}