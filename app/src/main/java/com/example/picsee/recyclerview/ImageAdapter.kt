package com.example.picsee.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.picsee.R
import com.example.picsee.api.Hit
import com.example.picsee.databinding.ImageRecyclerviewItemBinding
import com.squareup.picasso.Picasso

/**
 * ImageAdapter is used to create the recyclerview that will be used to display information
 */
class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    private lateinit var onItemClickListener: ((Hit) -> Unit)



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imagePosition = differ.currentList[position]
        holder.bind(imagePosition, onItemClickListener)

    }

    class ViewHolder(private val binding: ImageRecyclerviewItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(image: Hit, onItemClicked: ((Hit) -> Unit)){
            binding.image = image
            Picasso.get().load(image.webformatURL).placeholder(R.drawable.progress_bar).into(binding.imageImageview)
            binding.userNameTextview.text = image.user

            binding.root.setOnClickListener{
                onItemClicked(image)
            }
        }

        companion object{
            fun from(parent: ViewGroup): ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ImageRecyclerviewItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Hit>() {
        override fun areItemsTheSame(oldItem: Hit, newItem: Hit): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Hit, newItem: Hit): Boolean {
            return oldItem == newItem
        }
    }

    fun setOnItemClickListener(listener: (Hit) -> Unit) {
        onItemClickListener = listener
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}
