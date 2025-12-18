package app.mejourney.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class ContentDto(
    val id: String,
    val coverId: String,
    val title: String,
    val body: String,
    val imagesUrls: List<String> = emptyList(),
)

@Serializable
internal data class CoverDto(
    val id: String,
    val year: Long,
    val country: String,
    val place: String,
    val title: String,
    val description: String,
    val imageUrl: String,
)