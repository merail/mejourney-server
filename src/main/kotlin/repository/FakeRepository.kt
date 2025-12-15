package app.mejourney.repository

import app.mejourney.dto.ContentDto
import app.mejourney.dto.CoverDto

object FakeRepository {

    fun getContent(): List<ContentDto> =
        listOf(
            ContentDto(
                title = "Kazan",
                text = "Trip to Kazan was amazing"
            )
        )

    fun getCovers(): List<CoverDto> =
        listOf(
            CoverDto(
                id = "kazan-2023",
                year = 2023,
                country = "Russia",
                place = "Kazan",
                title = "Kazan Trip",
                description = "Summer trip to Kazan"
            )
        )
}