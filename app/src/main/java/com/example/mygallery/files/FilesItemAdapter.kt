package com.example.mygallery.files

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.mygallery.databinding.FileItemBinding

/**
created by Mehmet E. Yıldız
 **/
class FilesItemAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var _list = ArrayList<FileModel>()
    val list get() = _list.toList()

    private lateinit var context: Context

    private val selectedFileItemList = ArrayList<FileModel>()
    var selectedFileCount = MutableLiveData<Int>(0)

    fun setList(newList: ArrayList<FileModel>) {
        _list.clear()
        _list.addAll(newList)

        notifyDataSetChanged()
    }

    fun getSelectedItems(): ArrayList<FileModel> {
        return selectedFileItemList
    }

    class FileItemViewHolder(val binding: FileItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return FileItemViewHolder(
            FileItemBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bindFileItemViewHolder(holder as FileItemViewHolder, position)
    }

    private fun bindFileItemViewHolder(holder: FileItemViewHolder, position: Int) {
        holder.binding.apply {
            val item = list[position]
            imgTick.visibility = if (item.isSelected == true) View.VISIBLE else View.INVISIBLE
            item.size?.let { size ->
                processSize(size, tvSize)
            }
            tvFileName.text = item.name ?: "unknown name"
            llItem.setOnClickListener {
                onClickListenerCustom?.let { listener ->
                    listener(item)
                    item.isSelected?.let { isSelected ->
                        if (isSelected) {
                            // uncheck
                            item.isSelected = false
                            imgTick.visibility = View.INVISIBLE
                            selectedFileItemList.remove(item)
                            selectedFileCount.value = selectedFileCount.value!! - 1
                            notifyItemChanged(position)
                        } else {
                            // check
                            item.isSelected = true
                            imgTick.visibility = View.VISIBLE
                            selectedFileItemList.add(item)
                            selectedFileCount.value = selectedFileCount.value!! + 1
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

    private var onClickListenerCustom: ((item: FileModel) -> Unit)? = null
    fun setOnClickListenerCustom(f: ((item: FileModel) -> Unit)) {
        onClickListenerCustom = f
    }

}