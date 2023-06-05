package com.example.launder.data


import android.os.Parcelable
import com.example.launder.other.Constants.DEFAULT_PROFILE_PICTURE
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class User(
    val uid: String = "",
    val email: String = "",
    val username: String = "",
    val profilePictureUrl: String = DEFAULT_PROFILE_PICTURE,
    val time: String = "",
    val phone: String = ""

) : Parcelable