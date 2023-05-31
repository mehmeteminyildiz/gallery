package com.example.mygallery.myApp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mygallery.databinding.GalleryItemBinding

class GalleryItemAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var _list = ArrayList<ImageModel>()
    val list get() = _list.toList()

    private lateinit var context: Context

    private val selectedItemList = ArrayList<ImageModel>()

    fun setList(newList: ArrayList<ImageModel>) {
        _list.clear()
        _list.addAll(newList)

        notifyDataSetChanged()
    }

    fun getSelectedItems(): ArrayList<ImageModel> {
        return selectedItemList
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
        holder: GalleryItemViewHolder, position: Int
    ) {
        holder.binding.apply {
            val item = list[position]

            Glide.with(context).load(item.path).into(img)

            imgTick.visibility = if (item.isSelected == true) View.VISIBLE else View.INVISIBLE

            item.size?.let { size ->
                processSize(size, tvSize)
            }

            llItem.setOnClickListener {
                onClickListenerCustom?.let { listener ->
                    listener(item)
                    item.isSelected?.let { isSelected ->
                        if (isSelected) {
                            // uncheck
                            item.isSelected = false
                            imgTick.visibility = View.INVISIBLE
                            selectedItemList.remove(item)
                            notifyItemChanged(position)
                        } else {
                            // check
                            item.isSelected = true
                            imgTick.visibility = View.VISIBLE
                            selectedItemList.add(item)
                            notifyItemChanged(position)
                        }
                    }
                }
            }
        }
    }

    private fun processSize(size: Long, tvSize: TextView) {
        val fileSizeInKB = size / 1024.0
        val fileSizeInMB = fileSizeInKB / 1024.0
        val fileSizeInGB = fileSizeInMB / 1024.0

        if (fileSizeInMB < 1) {
            tvSize.text = "${fileSizeInKB.toInt()} kb"
        } else if (fileSizeInMB < 1024) {
            tvSize.text = "${fileSizeInMB.toInt()} mb"
        } else {
            tvSize.text = "${fileSizeInGB.toInt()} gb"
        }
    }

    private var onClickListenerCustom: ((item: ImageModel) -> Unit)? = null
    fun setOnClickListenerCustom(f: ((item: ImageModel) -> Unit)) {
        onClickListenerCustom = f
    }
}