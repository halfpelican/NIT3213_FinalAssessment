package com.vu.s8014554Assignment2.data.model

import com.squareup.moshi.Json

/** A single artwork returned by the dashboard endpoint. */
data class Entity(
    @param:Json(name = "artworkTitle") val artworkTitle: String,
    @param:Json(name = "artist") val artist: String,
    @param:Json(name = "medium") val medium: String,
    @param:Json(name = "year") val year: Int,
    @param:Json(name = "description") val description: String
)