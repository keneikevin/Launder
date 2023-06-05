package com.example.launderagent.adapterpackage

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.launder.data.Service
import com.example.launder.databinding.ServiceBinding
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class ServiceAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<ServiceAdapter.PostViewHolder>() {

    class PostViewHolder(val binding: ServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivPostImage: ImageView = binding.img
        val tvPostAuthor: TextView = binding.textName
        val tvPostText: TextView = binding.textPrice
        val cad = binding.cad
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Service>() {
        override fun areContentsTheSame(oldItem: Service, newItem: Service): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Service, newItem: Service): Boolean {
            return oldItem.mediaId == newItem.mediaId
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var posts: List<Service>
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
            glide.load(post.img).into(ivPostImage)
            tvPostAuthor.text = post.title
            tvPostText.text = post.price
            cad.setOnClickListener {
                Snackbar.make(this.itemView, "Swipe to delete", Snackbar.LENGTH_SHORT).show()
            }

        }
    }

    private var onDeletePostClickListener: ((Service) -> Unit)? = null



    fun setOnDeletePostClickListener(listener: (Service) -> Unit) {
        onDeletePostClickListener = listener
    }


}
























































