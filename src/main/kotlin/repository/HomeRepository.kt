package app.mejourney.repository

import app.mejourney.db.DatabaseFactory
import app.mejourney.dto.ContentDto
import app.mejourney.dto.CoverDto
import org.slf4j.LoggerFactory
import java.sql.ResultSet

internal object HomeRepository {

    private val logger = LoggerFactory.getLogger(HomeRepository::class.java)

    fun getCovers(): List<CoverDto> {
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

    fun getContentByCoverId(coverId: String) = try {
        val conn = DatabaseFactory.connection

        conn.prepareStatement(
            """
                SELECT id, cover_id, title, body
                FROM content
                WHERE cover_id = ?
                """
        ).use { stmt ->
            stmt.setString(1, coverId)

            stmt.executeQuery().use { rs ->
                if (rs.next()) {
                    rs.toContentDto()
                } else {
                    null
                }
            }
        }
    } catch (e: Exception) {
        logger.error("Database error while fetching content for coverId: $coverId", e)
        null
    }

    private fun ResultSet.toCoverDto() = CoverDto(
        id = getString("id"),
        year = getLong("year"),
        country = getString("country"),
        place = getString("place"),
        title = getString("title"),
        description = getString("description"),
        imageUrl = getString("imageUrl"),
    )

    private fun ResultSet.toContentDto() = ContentDto(
        id = getString("id"),
        coverId = getString("cover_id"),
        title = getString("title"),
        body = getString("body"),
    )
}