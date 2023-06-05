package com.example.launder.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.launder.data.Service
import com.example.launderagent.data.entities.ShoppingItem

@Database(
    entities = [ShoppingItem::class, Service::class],
    version = 5
)
abstract class ShoppingItemDatabase: RoomDatabase() {
    abstract fun shoppingDao(): ShoppingDao
}