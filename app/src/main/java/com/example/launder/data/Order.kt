package com.example.launder.data


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.launderagent.data.entities.ShoppingItem
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
@Entity(tableName = "orders")
data class Order (
    @PrimaryKey()
    var code: String ="",
    var price: String ="",
    var orderId: String ="",
    var bookTime: String ="",
    var completeTime: String ="",
    var customerOrderid: String ="",
    var status: String ="",
    @get:Exclude
    var services:@RawValue List<ShoppingItem> = listOf()
):Parcelable