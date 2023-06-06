package com.example.launder.other

import androidx.room.TypeConverter
import com.example.launderagent.data.entities.ShoppingItem
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class ShoppingItemConverter {
    @TypeConverter
    fun fromJson(json: String): List<ShoppingItem> {
        val type = object : TypeToken<List<ShoppingItem>>() {}.type
        return Gson().fromJson(json, type)
    }

    @TypeConverter
    fun toJson(list: List<ShoppingItem>): String {
        return Gson().toJson(list)
    }
}
