package com.example.myapplication.model

import kotlinx.serialization.*

@Serializable
data class UserResponse(val user: User)

@Serializable
data class User(val id: Int, val login: String, val firstname: String, val lastname: String, val mail: String)
