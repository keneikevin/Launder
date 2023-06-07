package com.example.launder.ui.auth


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.example.launder.R
import com.example.launder.activity.MainActivity
import com.example.launder.databinding.FragmentProfileBinding
import com.example.launder.domain.MainViewModel
import com.example.launder.other.Status
import com.example.launder.other.snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment :Fragment(R.layout.fragment_profile){

    private lateinit var binding: FragmentProfileBinding
    lateinit var viewModel: MainViewModel
    private lateinit var navController: NavHostController
    @Inject
    lateinit var glide: RequestManager
    protected open val uid:String
        get() = FirebaseAuth.getInstance().uid!!






    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        binding = FragmentProfileBinding.bind(view)
        // Get the NavHostController from the Composable function
        // Create the NavHostController
        navController = NavHostController(this.requireActivity())
        viewModel.loadProfile(uid)
        subscribeToObservers()
    binding.btnAddShoppingItem.setOnClickListener {
        viewModel.logout()
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
    }
        binding.logout.setOnClickListener {
        viewModel.logout()
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
    }
        binding.etItemPrice.setOnClickListener {
         findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

    }

    override fun onPause() {
        super.onPause()
        // Restore the previous title when the fragment is destroyed
        requireActivity().title = "Launder"
    }

    private fun subscribeToObservers(){
        viewModel.profileMeta.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS ->{
                        requireActivity().title = "Welcome: ${it.data?.username.toString()}"
                        binding.etShoppingItemName.text= it.data?.username
                        binding.eEmail.text= it.data?.email
                        binding.ePhone.text= it.data?.phone
                        binding.eTime.text= it.data?.time
                        glide.load(it.data?.profilePictureUrl).into(binding.bigImage)
                        binding.progressBar.visibility =  View.GONE
                        binding.vviee.visibility =  View.VISIBLE
                        binding.vvie.visibility =  View.VISIBLE
                    }
                    Status.ERROR ->{
                        binding.progressBar.visibility = View.GONE
                        binding.vvie.visibility =  View.VISIBLE
                        binding.vviee.visibility =  View.VISIBLE
                        snackbar(it.message.toString())
                    }
                    Status.LOADING ->{
                        binding.progressBar.visibility = View.VISIBLE
                        binding.vvie.visibility =  View.GONE
                        binding.vviee.visibility =  View.GONE
                    }
                }
            }

        })



    }


}
































