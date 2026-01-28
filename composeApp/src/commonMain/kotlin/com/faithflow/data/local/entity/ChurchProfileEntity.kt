package com.faithflow.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.faithflow.domain.model.ChurchProfile

@Entity(tableName = "church_profile")
data class ChurchProfileEntity(
    @PrimaryKey
    val id: String = "default", // Single row table

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "logo_url")
    val logoURL: String,

    @ColumnInfo(name = "welcome_message")
    val welcomeMessage: String,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "phone")
    val phone: String,

    @ColumnInfo(name = "website")
    val website: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "mission")
    val mission: String,

    @ColumnInfo(name = "service_times")
    val serviceTimes: String,

    @ColumnInfo(name = "social_facebook")
    val socialFacebook: String
)

fun ChurchProfileEntity.toDomain(): ChurchProfile {
    return ChurchProfile(
        name = name,
        logoURL = logoURL,
        welcomeMessage = welcomeMessage,
        address = address,
        phone = phone,
        website = website,
        email = email,
        mission = mission,
        serviceTimes = serviceTimes,
        socialFacebook = socialFacebook
    )
}

fun ChurchProfile.toEntity(): ChurchProfileEntity {
    return ChurchProfileEntity(
        name = name,
        logoURL = logoURL,
        welcomeMessage = welcomeMessage,
        address = address,
        phone = phone,
        website = website,
        email = email,
        mission = mission,
        serviceTimes = serviceTimes,
        socialFacebook = socialFacebook
    )
}
