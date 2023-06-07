package com.example.launder.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.launder.R
import com.example.launder.databinding.FragmentOrderBinding
import com.example.launder.domain.MainViewModel
import com.example.launder.other.Status
import com.example.launder.other.snackbar
import com.example.launderagent.adapterpackage.OrdersAdapter
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





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOrderBinding.bind(view)
        subscribeToObservers()
        setUpRecylerView()
        requireActivity().title = "My Orders"
        serviveAdapter.notifyDataSetChanged()
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.actiontoShoppingFragment)
        }
    }

    override fun onPause() {
        super.onPause()
        // Restore the previous title when the fragment is destroyed
        requireActivity().title = "Launder"
        viewModel.setCur()
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


























