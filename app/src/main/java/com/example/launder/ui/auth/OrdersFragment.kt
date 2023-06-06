package com.example.launder.ui.auth

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.launder.R
import com.example.launder.databinding.FragmentCustomersServiceBinding
import com.example.launder.databinding.FragmentOrderBinding
import com.example.launder.domain.MainViewModel
import com.example.launder.other.Status
import com.example.launder.other.snackbar
import com.example.launderagent.adapterpackage.OrdersAdapter
import com.example.launderagent.adapterpackage.UsersAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrdersFragment : Fragment(R.layout.fragment_order) {

    @Inject
    lateinit var glide:RequestManager
    @Inject
    lateinit var serviveAdapter: OrdersAdapter
    private lateinit var binding: FragmentOrderBinding
    private val viewModel: MainViewModel by viewModels()



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
                findNavController().navigate(R.id.action_ordersFragment_to_shoppingFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOrderBinding.bind(view)
        subscribeToObservers()
        setUpRecylerView()

        serviveAdapter.notifyDataSetChanged()

    }

    private fun setUpRecylerView() = binding.rvCakes.apply{
        viewModel.getOrders()
        serviveAdapter= OrdersAdapter(glide)
        binding.rvCakes.adapter = serviveAdapter
        binding.rvCakes.layoutManager = LinearLayoutManager(requireContext())

        adapter=serviveAdapter
        layoutManager = LinearLayoutManager(requireContext())
        itemAnimator = null

        serviveAdapter.notifyDataSetChanged()
    }


    private fun subscribeToObservers(){

        viewModel.orders.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS ->{
                        serviveAdapter.posts = it.data!!
                        binding.progressBar.visibility =  View.GONE
                    }
                    Status.ERROR ->{
                        binding.progressBar.visibility = View.GONE
                        snackbar(it.message.toString())
                    }
                    Status.LOADING ->{binding.progressBar.visibility = View.VISIBLE}
                }
            }

        })

    }
}


























