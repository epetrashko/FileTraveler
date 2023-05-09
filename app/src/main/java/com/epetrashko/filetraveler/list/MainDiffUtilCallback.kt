package com.epetrashko.filetraveler.list

import androidx.recyclerview.widget.DiffUtil
import com.epetrashko.filetraveler.presentation.FilePresentation

class MainDiffUtilCallback(
    private val oldList: List<FilePresentation>,
    private val newList: List<FilePresentation>
) :
    DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].path == newList[newItemPosition].path

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

}