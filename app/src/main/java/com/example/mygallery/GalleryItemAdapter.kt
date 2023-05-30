package com.example.mygallery

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mygallery.databinding.GalleryItemBinding

class GalleryItemAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var _list = ArrayList<ImageModel>()
    val list get() = _list.toList()

    private lateinit var context: Context

    fun setList(newList: ArrayList<ImageModel>) {
        _list.clear()
        _list.addAll(newList)

        notifyDataSetChanged()
    }

    class GalleryItemViewHolder(val binding: GalleryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return GalleryItemViewHolder(
            GalleryItemBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bindGalleryItemViewHolder(holder as GalleryItemViewHolder, position)
    }

    private fun bindGalleryItemViewHolder(
        holder: GalleryItemViewHolder,
        position: Int
    ) {
        holder.binding.apply {
            val item = list[position]

            Glide.with(context).load(item.path).into(image)


            image.setOnClickListener {
                onClickListenerCustom?.let { listener ->
                    listener(item.path ?: "")
                }
            }

        }
    }

    private var onClickListenerCustom: ((path: String) -> Unit)? = null
    fun setOnClickListenerCustom(f: ((path: String) -> Unit)) {
        onClickListenerCustom = f
    }
}