package com.example.launder.ui.home.customer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.launder.R
import com.example.launder.data.Service
import com.example.launder.databinding.FragmentCustomersServiceBinding
import com.example.launder.domain.MainViewModel
import com.example.launder.other.Status
import com.example.launder.other.snackbar
import com.example.launderagent.adapterpackage.ServiceCustomerAdapter
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ServiceFragment : Fragment(R.layout.fragment_customers_service) {

    @Inject
    lateinit var glide:RequestManager
    @Inject
    lateinit var serviveAdapter: ServiceCustomerAdapter
    private lateinit var binding: FragmentCustomersServiceBinding
    private lateinit var sss: List<Service>
    private val viewModel: MainViewModel by viewModels()
//    private val args:CustomersServiceFragmentArgs by navArgs()

//    protected open val uid:String
//        get() = FirebaseAuth.getInstance().uid!!
    private val args:ServiceFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCustomersServiceBinding.bind(view)

            viewModel.getService(args.currentUser.uid)
            subscribeToObservers()

        subscribeToObservers()
        setUpRecylerView()
        viewModel.loadOrder(args.currentUser.uid)
        setHasOptionsMenu(true)


        serviveAdapter.notifyDataSetChanged()

    }


    private fun setUpRecylerView() = binding.rvCakes.apply{

        serviveAdapter= ServiceCustomerAdapter(glide)
        binding.rvCakes.adapter = serviveAdapter
        binding.rvCakes.layoutManager = LinearLayoutManager(requireContext())

        adapter=serviveAdapter
        layoutManager = GridLayoutManager(requireContext(),2)
        itemAnimator = null


        serviveAdapter.notifyDataSetChanged()
    }


        private fun subscribeToObservers(){

            viewModel.deleteServiceStatus.observe(viewLifecycleOwner, Observer { result ->
                result?.let {
                    when (result.status) {
                        Status.SUCCESS ->{}
                        Status.LOADING ->{}
                        Status.ERROR ->{  snackbar(it.message.toString())}

                    }
                }
            })
            viewModel.bookServiceStatus.observe(viewLifecycleOwner, Observer { result ->
                result?.let {
                    when (result.status) {
                        Status.SUCCESS ->{
//                            binding.createPostProgressBar.visibility =  View.GONE
//                            binding.btnPost.isClickable = true
                            snackbar("Service created Successfully")

                  //          findNavController().navigate(R.id.action_createServiceFragment_to_serviceFragment)
                        }
                        Status.ERROR ->{
//                            binding.createPostProgressBar.visibility = View.GONE
//                            binding.btnPost.isClickable = true
                            snackbar(it.message.toString())
                        }
                        Status.LOADING ->{
//                            binding.createPostProgressBar.visibility = View.VISIBLE
//                            binding.btnPost.isClickable = false
                        }
                    }
                }

            })
            viewModel.service.observe(viewLifecycleOwner, Observer { result ->
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
















