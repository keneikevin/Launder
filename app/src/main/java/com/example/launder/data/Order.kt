package com.example.launder.data


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.launderagent.data.entities.ShoppingItem
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
@Entity(tableName = "orders")
data class Order (
    @PrimaryKey()
    val code: String ="",
    val price: String ="",
    val orderUid: String ="",
    val bookTime: String ="",
    val completeTime: String ="",
    val status: String ="",
    val services:@RawValue List<ShoppingItem> = listOf()
):Parcelable