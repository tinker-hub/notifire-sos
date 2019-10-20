package com.tinker.notifire.data.model

data class User(
    val name: String? = null,
    val phoneNumber: String? = null,
    val pushToken: String? = null,
    val subscribedZones: MutableList<Zone>? = null
)