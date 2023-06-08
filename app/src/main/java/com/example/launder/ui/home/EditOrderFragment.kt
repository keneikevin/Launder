package com.example.launder.ui.home.customer

//import kotlinx.android.synthetic.main.activity_main.*
import android.app.AlertDialog
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.example.launder.R
import com.example.launder.data.OrderUpdate
import com.example.launder.databinding.FragmentOrdereditBinding
import com.example.launder.domain.MainViewModel
import com.example.launder.other.Status
import com.example.launder.other.snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class EditOrderFragment : Fragment(R.layout.fragment_orderedit) {
    private lateinit var binding: FragmentOrdereditBinding
    private val viewModel: MainViewModel by viewModels()
    @Inject
    lateinit var glide: RequestManager
    lateinit var auth: FirebaseAuth

    private val args: EditOrderFragmentArgs by navArgs()

    private var cuImageUri: Uri? = null
    lateinit var stats: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOrdereditBinding.bind(view)
        if (args.currentOrder.oderUid.isNotEmpty()){
            //   viewModel.getOrders(args.currentOrder.orderUid)
            viewModel.getUser(args.currentOrder.oderUid)
            //   binding.loc
            subscribeToObservers()
        }
        auth = FirebaseAuth.getInstance()
        val uid = auth.uid!!
        viewModel.getUser(uid)
        binding.orderid.text = args.currentOrder.code
        binding.book.text = args.currentOrder.bookTime
        binding.complete.text = args.currentOrder.completeTime
        binding.pricee.text = args.currentOrder.price
        binding.status.text = args.currentOrder.status
        stats = args.currentOrder.status
        when (args.currentOrder.status) {
            "Pending" -> {
                binding.status.setBackgroundColor(Color.parseColor("#0000FF"))
                stats = "Canceled"
                binding.button.text = "Cancel Order"
            }

            "Accepted" -> {
                binding.status.setBackgroundColor(Color.parseColor("#800000"))
                stats = "Canceled"
                binding.button.text = "Cancel Order"
            }
            "Processing" -> {
                binding.button.text = "Cancel Order"
                binding.status.setBackgroundColor(Color.parseColor("#808080"))
                stats = "Canceled"
            }
            "Canceled" -> {
                binding.button.text = "Order Canceled"
                binding.button.isClickable = false
                binding.status.setBackgroundColor(Color.RED)
                binding.button.setBackgroundColor(Color.RED)
                stats = "Canceled"
                binding.dele.visibility = View.VISIBLE
            }
            "Complete" -> {
                binding.button.text = "Order Complete"
                binding.button.isClickable = false
                binding.button.setBackgroundColor(Color.parseColor("#006400"))
                binding.status.setBackgroundColor(Color.parseColor("#006400"))
                binding.dele.visibility = View.VISIBLE
                stats = "Complete"}
            else ->  {
                binding.status.setBackgroundColor(Color.RED)
            }
        }
        if (args.currentOrder.status.equals("Processing") ||args.currentOrder.status.equals("Pending") || args.currentOrder.status.equals("Accepted")) {
        binding.button.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Launder")
            val sizePice = "Confirm Cancel oder no: ${args.currentOrder.code} ?"
            builder.setMessage(sizePice)
            builder.setIcon(R.drawable.warning)
            //performing positive action
            builder.setPositiveButton("Yes"){dialogInterface, which ->


                val currentDate = Date()
                val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                val readableDate = dateFormat.format(currentDate)
                val profileUpdate = OrderUpdate(status = stats, orderId = args.currentOrder.orderId, completeTime = readableDate)

                viewModel.updateOrder(profileUpdate)
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
        binding.dele.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Launder")
            val sizePice = "Proceed to delete oder: ${args.currentOrder.code} ?"
            builder.setMessage(sizePice)
            builder.setIcon(R.drawable.a)
            //performing positive action
            builder.setPositiveButton("Yes"){dialogInterface, which ->


                viewModel.deleteOrder(args.currentOrder)

                // findNavController().navigate(R.id.action_shoppingFragment_to_ordersFragment)
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
    private fun subscribeToObservers() {
        viewModel.deleteOrderStatus.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS ->{
                        findNavController().popBackStack()
                    }
                    Status.LOADING ->{}
                    Status.ERROR ->{  snackbar(it.message.toString())}

                }
            }
        })
        viewModel.updateOrderStatus.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS ->{

                        binding.progressBar.visibility =  View.GONE
                        binding.button.isClickable = true
                        snackbar("Order Cancelled ")
                        findNavController().popBackStack()



                    }
                    Status.ERROR ->{
                        binding.progressBar.visibility = View.GONE
                        binding.button.isClickable = true
                        snackbar(it.message.toString())
                    }
                    Status.LOADING ->{binding.progressBar.visibility = View.VISIBLE
                        binding.button.isClickable = false
                    }
                }
            }

        })
        viewModel.curImageUri.observe(viewLifecycleOwner){uri->
            uri?.let {
                cuImageUri=it
                Log.d("raetat",it.toString())
            }
        }
        viewModel.getUserStatus.observe(viewLifecycleOwner, Observer {result->
            result?.let {
                when (result.status) {
                    Status.SUCCESS ->{

                        binding.progressBar.visibility = View.GONE
                        glide.load(it.data?.profilePictureUrl).into(binding.bigMage)
                        binding.etShoppingItemName.text = it.data?.username
                        binding.eEmail.text = it.data?.email
                        binding.ePhone.text = it.data?.phone
                        binding.eTime.text = it.data?.time


                    }
                    Status.ERROR ->{
                        binding.progressBar.visibility = View.GONE
                        snackbar(it.message.toString())
                    }
                    Status.LOADING ->{ binding.progressBar.visibility = View.VISIBLE}
                }
            }

        })
    }


}





































