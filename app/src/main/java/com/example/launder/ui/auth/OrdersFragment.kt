package com.example.launder.ui.auth


import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.example.launder.R
import com.example.launder.databinding.FragmentOrderBinding
import com.example.launder.domain.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class OrdersFragment :Fragment(R.layout.fragment_order){

    private lateinit var binding: FragmentOrderBinding
    lateinit var viewModel: MainViewModel
    private lateinit var navController: NavHostController
    @Inject
    lateinit var glide: RequestManager
    protected open val uid:String
        get() = FirebaseAuth.getInstance().uid!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.cart_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {
               // findNavController().navigate(R.id.action_ordersFragment_to_shoppingFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        binding = FragmentOrderBinding.bind(view)
        // Get the NavHostController from the Composable function
        // Create the NavHostController
        navController = NavHostController(this.requireActivity())
        viewModel.loadOrder(uid)


    }



}
































