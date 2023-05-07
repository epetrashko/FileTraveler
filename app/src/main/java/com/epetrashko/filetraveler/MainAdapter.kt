package com.epetrashko.filetraveler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.epetrashko.filetraveler.databinding.ItemFileBinding

class DiffCallback : DiffUtil.ItemCallback<FilePresentation>() {
    override fun areItemsTheSame(
        oldItem: FilePresentation,
        newItem: FilePresentation
    ): Boolean = oldItem.path == newItem.path

    override fun areContentsTheSame(
        oldItem: FilePresentation,
        newItem: FilePresentation
    ): Boolean = oldItem == newItem

}

class MainAdapter(var files: List<FilePresentation>, private val callback: FileItemCallback) :
    ListAdapter<FilePresentation, MainAdapter.MainViewHolder>(DiffCallback()) {


    class MainViewHolder(val binding: ItemFileBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder =
        MainViewHolder(ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = files.size

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        with(holder.binding) {
            with(files[position]) {
                fileName.text = name
                fileDescription.text = description
                fileImg.setImageResource(icon)
                root.setOnClickListener {
                    callback.onClick(this)
                }
                root.setOnLongClickListener {
                    callback.onLongClick(this)
                    true
                }
            }
        }
    }

    fun updateList(newFiles: List<FilePresentation>) {
        files = newFiles
        submitList(newFiles)
    }

}