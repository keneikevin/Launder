package com.example.launder.ui.home.customer

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.launder.R
import com.example.launder.databinding.FragmentCartBinding
import com.example.launder.domain.MainViewModel
import com.example.launder.other.Status
import com.example.launder.other.snackbar
import com.example.launderagent.adapter.ShoppingAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShoppingFragment :Fragment(R.layout.fragment_cart) {

    private lateinit var binding: FragmentCartBinding
    private val viewModel: MainViewModel by viewModels()

    private lateinit var shoppingAdapter: ShoppingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCartBinding.bind(view)
        subscribeToObservers()
        setupRecyclerView()

        requireActivity().title = "My Cart"

        binding.fab.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Cake City")
            val sizePice = "Proceed to checkout?    subtotal ${binding.total.text} "
            builder.setMessage(sizePice)
            builder.setIcon(R.drawable.a)
            //performing positive action
            builder.setPositiveButton("Yes"){dialogInterface, which ->

                viewModel.bookServices(binding.total.text.toString())

            }
            builder.setNeutralButton("Cancel"){dialogInterface , which ->
                /*NO-Op*/
            }

            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()

        }
    }


    private val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
        0, LEFT or RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = true

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = viewHolder.layoutPosition
            val item = shoppingAdapter.shoppingItems[pos]
            viewModel?.deleteShoppingItem(item)
            Snackbar.make(requireView(), "Successfully deleted item", Snackbar.LENGTH_LONG).apply {
                setAction("Undo") {
                    viewModel?.insertShoppingItemIntoDb(item)
                }
                show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setCur()
    }
    override fun onPause() {
        super.onPause()
        // Restore the previous title when the fragment is destroyed
        requireActivity().title = "Launder"
        viewModel.setCur()
    }


    private fun subscribeToObservers() {
        viewModel.totalPrice.observe(viewLifecycleOwner, Observer {
            val price = it ?: 0f
            val priceText = "$price ksh"
            binding.total.text = priceText
        })
        viewModel.shoppingItems.observe(viewLifecycleOwner, Observer {
            shoppingAdapter.shoppingItems = it
        })
        viewModel.bookServiceStatus.observe(viewLifecycleOwner,Observer{ result->
            result?.let {
                when (result.status) {
                    Status.SUCCESS ->{
                        binding.progressBar.visibility =  View.GONE
                                  findNavController().navigate(R.id.action_shoppingFragment_to_ordersFragment2)
                                       }
                    Status.ERROR ->{
                        binding.progressBar.visibility = View.GONE
                        snackbar(it.message.toString())
                    }
                    Status.LOADING ->{binding.progressBar.visibility = View.GONE}
                }
            }
        })

    }
    private fun setupRecyclerView() {
        binding.rvCakes.apply {
            shoppingAdapter = ShoppingAdapter()
            adapter = shoppingAdapter
            layoutManager = LinearLayoutManager(requireContext())
            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
        }
    }
}
