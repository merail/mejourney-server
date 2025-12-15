package app.mejourney.dto

import kotlinx.serialization.Serializable

@Serializable
data class ContentDto(
    val title: String,
    val text: String,
)

@Serializable
data class CoverDto(
    val id: String,
    val year: Long,
    val country: String,
    val place: String,
    val title: String,
    val description: String,
)

@Serializable
data class ImageDto(
    val reference: String,
)