package com.example.launder.domain

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.launder.R
import com.example.launder.data.Order
import com.example.launder.data.ProfileUpdate
import com.example.launder.data.Service
import com.example.launder.data.User
import com.example.launder.other.Constants.MIN_USER_NAME
import com.example.launder.other.Resouce
import com.example.launder.other.Resource
import com.example.launderagent.data.entities.ShoppingItem
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: mainRepository,
    private val applicationContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _loginFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val loginFlow: StateFlow<Resource<FirebaseUser>?> = _loginFlow

    private val _signupFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val signupFlow: StateFlow<Resource<FirebaseUser>?> = _signupFlow

    private val _getUserStatus = MutableLiveData<Resouce<User>>()
    val getUserStatus: LiveData<Resouce<User>> = _getUserStatus

    private val _updateProfileStatus = MutableLiveData<Resouce<Any>>()
    val updateProfileStatus: LiveData<Resouce<Any>> = _updateProfileStatus

    private val _profileMeta = MutableLiveData<Resouce<User>>()
    val profileMeta: LiveData<Resouce<User>> = _profileMeta

    private val _order = MutableLiveData<Resouce<List<Order>>>()
    val order: LiveData<Resouce<List<Order>>> = _order


    private val _createServiceStatus = MutableLiveData<Resouce<Any>>()
    val createServiceStatus: LiveData<Resouce<Any>> = _createServiceStatus

    private val _bookServiceStatus = MutableLiveData<Resouce<Any>>()
    val bookServiceStatus: LiveData<Resouce<Any>> = _bookServiceStatus




    private val _curImageUri = MutableLiveData<Uri>()
    val curImageUri: LiveData<Uri> = _curImageUri


    private val _deleteServiceStatus = MutableLiveData<Resouce<Service>>()
    val deleteServiceStatus: LiveData<Resouce<Service>> = _deleteServiceStatus

    private val _services = MutableLiveData<List<Service>>()
    val services: LiveData<List<Service>> = _services

    private val _service = MutableLiveData<Resouce<List<Service>>>()
    val service: LiveData<Resouce<List<Service>>> = _service

    private val _users = MutableLiveData<Resouce<List<User>>>()
    val users: LiveData<Resouce<List<User>>> = _users
  private val _orders = MutableLiveData<Resouce<List<Order>>>()
    val orders: LiveData<Resouce<List<Order>>> = _orders

    val currentUser: FirebaseUser?
        get() = repository.currentUser


    var serviceList: MutableList<Service> = mutableListOf()

    private val KEY = "Saved_Shopping_List"


    private val _cake = MutableLiveData<Resouce<Service>>()
    val cake: LiveData<Resouce<Service>> = _cake

    private val _curPrice = MutableLiveData<String>()
    val curPrice: LiveData<String> get() = _curPrice
    // The current cost



    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score
    init {
        if(repository.currentUser != null){
            _loginFlow.value = Resource.Success(repository.currentUser!!)
        }
        _score.value = 1500
    }

    fun getOrders() {
        _orders.postValue((Resouce.loading(null)))

        viewModelScope.launch(dispatcher){
            val result = repository.getOrder()
            Log.d("tttttuu", result.toString())
            _orders.postValue((result))
        }
    }
    fun loadOrder(uid: String) {
//        _order.postValue((Resouce.loading(null)))
//        viewModelScope.launch(dispatcher) {
//            val result = repository.getOrder(uid)
//            _order.postValue((result))
//            Log.d("uwdguwdg", result.toString())
//        }
//        //getPosts(uid)
    }
    fun setCur(){
        _bookServiceStatus.postValue(Resouce.loading(null))
    }
    fun getUsers() {
        _users.postValue((Resouce.loading(null)))

        viewModelScope.launch(dispatcher){
            val result = repository.getUsers()
            Log.d("tttttuu", _users.value?.data.toString())
            _users.postValue((result))
        }
    }
    fun getService() {
        _service.postValue((Resouce.loading(null)))

        viewModelScope.launch(dispatcher){
            val result = repository.getServices()
            _service.postValue((result))
            _services.postValue((result.data))
        }
    }
    fun deleteService(post: Service) {
        _deleteServiceStatus.postValue((Resouce.loading(null)))

        viewModelScope.launch(dispatcher) {
            val result = repository.deleteService(post)
            _deleteServiceStatus.postValue((result))
        }
    }
    fun bookServices(price:String){
        viewModelScope.launch(dispatcher) {
            _bookServiceStatus.postValue(((Resouce.loading(null))))
            viewModelScope.launch(dispatcher) {
                val result = shoppingItems.value?.let {
                    val currentDate = Date()
                    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                    val readableDate = dateFormat.format(currentDate)
                    val random = Random().nextInt(9000) + 1000
                    repository.bookServices(
                        code = "#${random}",
                        status = "Pending",
                        bookTime = readableDate,
                        completeTime ="Pending",
                        prise = price,
                        services = it
                    )
                }
                _bookServiceStatus.postValue((result))
            Log.d("resyys",result.toString())
            }


        }
    }
    fun login(email: String, password: String) = viewModelScope.launch {
        _loginFlow.value = Resource.Loading()
        val result = repository.login(email, password)
        _loginFlow.value = result
    }

    fun signup(name: String, email: String, password: String,phone:String) = viewModelScope.launch {
        _signupFlow.value = Resource.Loading()
        val result = repository.signup(name, email, password, phone)
        _signupFlow.value = result
    }

    fun logout() {
        repository.logout()
        _loginFlow.value = null
        _signupFlow.value = null
    }
    fun getUser(uid:String){
        _getUserStatus.postValue((Resouce.loading(null)))
        viewModelScope.launch(dispatcher) {
            val result = repository.getUser(uid)
            _getUserStatus.postValue((result))
        }
    }

    fun loadProfile(uid: String) {
        _profileMeta.postValue((Resouce.loading(null)))
        viewModelScope.launch(dispatcher) {
            val result = repository.getUser(uid)
            _profileMeta.postValue((result))
        }
        //getPosts(uid)
    }

    fun updateProfile(profileUpdate: ProfileUpdate){
        if (profileUpdate.username.length < MIN_USER_NAME){
            val error = applicationContext.getString(R.string.error_username_too_short)
            _updateProfileStatus.postValue(((Resouce.error(error,null))))
        } else{
            _updateProfileStatus.postValue((Resouce.loading(null)))
            viewModelScope.launch(dispatcher){
                val result = repository.updateProfile(profileUpdate)

                _updateProfileStatus.postValue((result))

            }
        }
    }

    fun setCurImageUri(uri: Uri) {
        _curImageUri.postValue(uri)
    }



    fun setCurPrice(pr:String){
        _curPrice.value = pr
        _insertShoppingItemStatus.postValue(Resouce.loading(null))
    }

    fun calculate(sz: String){
        val prc = _curPrice.value?.toInt()
        var p = sz.toInt()
        var s= p * prc!!
        _score.value = s
    }


    var shoppingItems = repository.observeAllShoppingItems()

    private val _insertShoppingItemStatus = MutableLiveData<Resouce<ShoppingItem>>()
    val insertShoppingItemStatus: LiveData<Resouce<ShoppingItem>> = _insertShoppingItemStatus



    val totalPrice = repository.observeTotalPrice()
    fun deleteShoppingItem(shoppingItem: ShoppingItem) = viewModelScope.launch {
        repository.deleteShoppingItem(shoppingItem)
    }


    fun insertShoppingItemIntoDb(shoppingItem: ShoppingItem) = viewModelScope.launch {
        repository.insertShoppingItem(shoppingItem)
    }
    fun items() = viewModelScope.launch {
        repository.observeAllShoppingItems()

    }

    fun insertShoppingItem(name: String, size: String, price: String,img:String) {
        if (name.isEmpty() || size.isEmpty() || price.isEmpty()){
            _insertShoppingItemStatus.postValue((Resouce.error("The fields must not be empty", null)))
            return
        }
        val shoppingItem = ShoppingItem(name, size.toInt(), price.toFloat(),img ?:"" )
        insertShoppingItemIntoDb(shoppingItem)
        _insertShoppingItemStatus.postValue((Resouce.success(shoppingItem)))
    }



    fun getServiceById(cakeId: String) = viewModelScope.launch {
        _cake.postValue((Resouce.loading(null)))
        // val cake = repository.getCakeById(cakeId)
        cake.let {
            //   _cake.postValue((Resouce.success(it)))
        }}
}