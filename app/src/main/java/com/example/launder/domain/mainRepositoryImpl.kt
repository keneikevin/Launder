package com.example.launder.domain

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.launder.data.Order
import com.example.launder.data.ProfileUpdate
import com.example.launder.data.Service
import com.example.launder.data.User
import com.example.launder.data.local.ShoppingDao
import com.example.launder.other.Constants.DEFAULT_PROFILE_PICTURE
import com.example.launder.other.Constants.SERVICE_COLLECTION
import com.example.launder.other.Resouce
import com.example.launder.other.Resource
import com.example.launder.other.safeCall
import com.example.launderagent.data.entities.ShoppingItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class mainRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val shoppingDao: ShoppingDao
) : mainRepository {
    var db = FirebaseFirestore.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage
    private val cakes = firestore.collection(SERVICE_COLLECTION)
    private val orders = firestore.collection("orders")
    private val users = firestore.collection("users")

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.localizedMessage)
        }
    }


    override suspend fun bookServices(code: String,status:String,bookTime: String,completeTime: String, prise:String,services:List<ShoppingItem>) = withContext(Dispatchers.IO) {
        safeCall {
            val uid = firebaseAuth.uid!!
            val oderId = UUID.randomUUID().toString()
//            val imageUploadResult = storage.getReference(postId).putFile(imageUri).await()
//            val imageUrl = imageUploadResult?.metadata?.reference?.downloadUrl?.await().toString()
            val post = Order(
                code = code,
                price = prise,
                orderUid = uid,
                bookTime = bookTime,
                completeTime = completeTime,
                status = status,
                services = services
            )
            orders.document(oderId).set(post).await()
            Resouce.success(Any())
        }
    }


    override suspend fun updateProfilePicture(uid: String, imageUri: Uri) = withContext(Dispatchers.IO) {

        val storageRef = storage.getReference(uid)
        val user = getUser(uid).data!!
        if (user.profilePictureUrl != DEFAULT_PROFILE_PICTURE) {
            storage.getReferenceFromUrl(user.profilePictureUrl).delete().await()
        }

        storageRef.putFile(imageUri).await().metadata?.reference?.downloadUrl?.await()
    }
    override suspend fun deleteService(post: Service) = withContext(Dispatchers.IO) {
        safeCall {
            cakes.document(post.mediaId).delete().await()
            storage.getReferenceFromUrl(post.img).delete().await()
            Resouce.success(post)
        }
    }


    override suspend fun updateProfile(profileUpdate: ProfileUpdate) = withContext(Dispatchers.IO) {
        safeCall {
//            val imageUrl = profileUpdate.profilePictureUri?.let { uri ->
//                updateProfilePicture(profileUpdate.uidToUpdate, uri).toString()
//            }

            val uid = firebaseAuth.uid!!
            val postId = UUID.randomUUID().toString()
            val imageUploadResult =
                profileUpdate.profilePictureUri?.let { storage.getReference(postId).putFile(it).await() }
            val imageUrl = imageUploadResult?.metadata?.reference?.downloadUrl?.await().toString()


            val map = mutableMapOf(
                "username" to profileUpdate.username,
                "phone" to profileUpdate.phone,
                "email" to profileUpdate.email,
                "time" to profileUpdate.time,
            )
            imageUrl.let { url ->
                map["profilePictureUrl"] = url
            }

            users.document(profileUpdate.uidToUpdate).update(map.toMap()).await().also {
                FirebaseAuth.getInstance().currentUser?.updateEmail(profileUpdate.email)
            }
            Resouce.success(Any())
        }

    }
    override suspend fun getUser(uid: String) = withContext(Dispatchers.IO) {
        safeCall {
            val user = users.document(uid).get().await().toObject(User::class.java)
                ?: throw IllegalStateException()
            val currentUid = FirebaseAuth.getInstance().uid!!


            Resouce.success(user)
        }
    }

    override suspend fun insertShoppingItem(shoppingItem: ShoppingItem) {
        shoppingDao.insertShoppingItem(shoppingItem)
    }

    override suspend fun deleteShoppingItem(shoppingItem: ShoppingItem) {
        shoppingDao.deleteShoppingItem(shoppingItem)
    }

    override fun observeAllShoppingItems(): LiveData<List<ShoppingItem>> {
        val ua = shoppingDao.observeAllShoppingItems()
        return shoppingDao.observeAllShoppingItems()


    }

    override fun observeTotalPrice(): LiveData<Float> {
        return shoppingDao.observeTotalPrice()
    }

    override suspend fun signup(name: String, email: String, password: String,phone: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await().also {

                val uid = it.user?.uid!!
                val user = User(uid,email,name,"https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png","phone",phone)
                users.document(uid).set(user).await()

            }

            Resource.Success(result.user!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.localizedMessage)
        }
    }
    override suspend fun getServices() = withContext(Dispatchers.IO) {
        safeCall {
            val uid = FirebaseAuth.getInstance().currentUser?.uid


            val allPosts = cakes
                //  .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

                .toObjects(Service::class.java)


            Resouce.success(allPosts)
        }
    }

    override suspend fun getUsers()= withContext(Dispatchers.IO) {
        safeCall {
            val uid = FirebaseAuth.getInstance().currentUser?.uid


            val allPosts = users
                //  .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

                .toObjects(User::class.java)

            Resouce.success(allPosts)
        }
    }
    override suspend fun getOrders()= withContext(Dispatchers.IO) {
        safeCall {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val allPosts = orders
                .get()
                .await()
                .toObjects(Order::class.java)

            Resouce.success(allPosts)
        }
    }
    override suspend fun getOrder() = withContext(Dispatchers.IO) {
        safeCall {

            val allPosts = orders.whereEqualTo("orderUid", firebaseAuth.uid!!)
                .get()
                .await()

                .toObjects(Order::class.java)

            Log.d("dadada", allPosts.toString())
            Resouce.success(allPosts)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }


}