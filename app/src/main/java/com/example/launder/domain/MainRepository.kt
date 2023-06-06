package com.example.launder.domain

import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.launder.data.Order
import com.example.launder.data.ProfileUpdate
import com.example.launder.data.Service
import com.example.launder.data.User
import com.example.launder.other.Resouce
import com.example.launder.other.Resource
import com.example.launderagent.data.entities.ShoppingItem
import com.google.firebase.auth.FirebaseUser

interface mainRepository {
    val currentUser: FirebaseUser?
    suspend fun getUser(uid: String): Resouce<User>

    suspend fun updateProfilePicture(uid:String, imageUri: Uri):Uri?
    suspend fun login(email: String, password: String): Resource<FirebaseUser>

    suspend fun updateProfile(profileUpdate: ProfileUpdate): Resouce<Any>

    suspend fun deleteService(post: Service): Resouce<Service>

    //  suspend fun createPost(imageUri: Uri, name: String, prise:String,per:String): Resouce<Any>



    suspend fun getServices(uid:String): Resouce<List<Service>>
    suspend fun getUsers(): Resouce<List<User>>
    suspend fun signup(name: String, email: String, password: String,phone:String): Resource<FirebaseUser>
    fun logout()
    suspend fun bookServices(code: String,status:String,bookTime: String,completeTime: String, prise:String,services:List<ShoppingItem>):Resouce<Any>

    suspend fun getOrder(uid: String): Resouce<Order>
    suspend fun insertShoppingItem(shoppingItem: ShoppingItem)

    suspend fun deleteShoppingItem(shoppingItem: ShoppingItem)

    fun observeAllShoppingItems(): LiveData<List<ShoppingItem>>

    fun observeTotalPrice(): LiveData<Float>
}