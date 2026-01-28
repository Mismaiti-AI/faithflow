package com.faithflow.domain.model

data class ChurchProfile(
    val name: String,
    val logoURL: String,
    val welcomeMessage: String,
    val address: String,
    val phone: String,
    val website: String,
    val email: String,
    val mission: String,
    val serviceTimes: String,
    val socialFacebook: String
)
