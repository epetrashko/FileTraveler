package com.epetrashko.filetraveler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.epetrashko.filetraveler.databinding.ItemFileBinding

class MainAdapter(var files: List<FilePresentation>, val callback: FileItemCallback) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {


    class MainViewHolder(val binding: ItemFileBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder =
        MainViewHolder(ItemFileBinding.inflate(LayoutInflater.from(parent.context)))

    override fun getItemCount(): Int = files.size

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        with(holder.binding) {
            with(files[position]) {
                fileName.text = name
                fileDescription.text = description
                fileImg.setImageResource(icon)
                root.setOnClickListener {
                    callback.onClick(
                        path = path,
                        isDirectory = isDirectory
                    )
                }
                root.setOnLongClickListener {
                    callback.onLongClick(
                        path = path,
                        isDirectory = isDirectory
                    )
                    true
                }
            }
        }
    }

    fun updateList(newFiles: List<FilePresentation>) {
        files = newFiles
        notifyDataSetChanged()
    }

}