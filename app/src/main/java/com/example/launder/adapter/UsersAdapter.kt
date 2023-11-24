package com.example.launderagent.adapterpackage
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.launder.data.Service
import com.example.launder.databinding.ServiceBinding
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

import com.example.launder.data.User
import com.example.launder.ui.home.customer.ServiceFragmentDirections
import com.example.launder.ui.home.customer.UsersFragmentDirections

interface UserItemClickListener {
    fun onUserItemClicked(user: User)
}
class UsersAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<UsersAdapter.PostViewHolder>() {

    class PostViewHolder(val binding: ServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivPostImage: ImageView = binding.img
        val tvPostAuthor: TextView = binding.textName
       // val tvPostText: TextView = binding.textPrice
        val cad = binding.cad
    }

    private val diffCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var posts: List<User>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            ServiceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.apply {
            glide.load(post.profilePictureUrl).into(ivPostImage)
            tvPostAuthor.text = post.username
            //tvPostText.text = post.phone
            cad.setOnClickListener {
                val directions= UsersFragmentDirections.actionUsersFragmentToServiceFragment(post)
                it.findNavController().navigate(directions)
               // Snackbar.make(this.itemView, "Swipe", Snackbar.LENGTH_SHORT).show()
            }

        }

    }

    private var onDeletePostClickListener: ((Service) -> Unit)? = null



    fun setOnDeletePostClickListener(listener: (Service) -> Unit) {
        onDeletePostClickListener = listener
    }


}
























































