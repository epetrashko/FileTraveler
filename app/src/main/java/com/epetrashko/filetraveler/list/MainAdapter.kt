package com.epetrashko.filetraveler.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.epetrashko.filetraveler.databinding.ItemFileBinding
import com.epetrashko.filetraveler.listener.FileItemActionListener
import com.epetrashko.filetraveler.presentation.FilePresentation
import com.epetrashko.filetraveler.utils.setVisibility


class MainAdapter(
    var files: ArrayList<FilePresentation>,
    private val callback: FileItemActionListener
) :
    RecyclerView.Adapter<MainAdapter.MainViewHolder>() {


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
                ivFileChanged.setVisibility(isChanged)
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
        val diffResult = DiffUtil.calculateDiff(MainDiffUtilCallback(files, newFiles))
        files.clear()
        files.addAll(newFiles)
        diffResult.dispatchUpdatesTo(this)
    }

}