package com.julyapp.dentalapp.models

import java.util.*

data class Users(
    var id: String,
    var uid: String,
    var displayName: String,
    var email: String,
    var providerId: String,
    var photoUrl: String,
    var lastLogin: Date

)