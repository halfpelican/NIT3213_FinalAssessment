package com.vu.s8014554Assignment2.data.model

import com.squareup.moshi.Json

/** Response body of GET /dashboard/{keypass}. */
data class DashboardResponse(
    @param:Json(name = "entities") val entities: List<Entity>,
    @param:Json(name = "entityTotal") val entityTotal: Int
)