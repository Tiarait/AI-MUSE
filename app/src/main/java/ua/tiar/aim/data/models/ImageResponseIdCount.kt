package ua.tiar.aim.data.models

import androidx.room.Entity

@Entity
data class ImageResponseIdCount(
    val imageId: String,
    val count: Int,
    val id: Int
)