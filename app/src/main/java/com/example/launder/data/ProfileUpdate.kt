package com.example.launder.data

import android.net.Uri

data class ProfileUpdate(
    val uidToUpdate:String = "",
    val username:String =  "",
    val email: String = "",
    val phone:String =  "",
    var time: String ,
    val profilePictureUri: Uri? =  null
)