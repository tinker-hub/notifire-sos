package com.tinker.notifire.data.model

data class FireReport(
    val userId: String? = null,
    val userName: String? = null,
    val source: String? = null,
    val location: Point? = null,
    val details: String? = null,
    val zone: Zone? = null
)