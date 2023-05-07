package com.epetrashko.filetraveler.utils

import android.content.Context
import androidx.annotation.StringRes
import com.epetrashko.filetraveler.R

enum class SortDirection(val id: Int, @StringRes val stringRes: Int) {
    BY_NAME_ASC(0, R.string.sort_by_name_asc),
    BY_NAME_DESC(1, R.string.sort_by_name_desc),
    BY_SIZE_ASC(2, R.string.sort_by_size_asc),
    BY_SIZE_DESC(3, R.string.sort_by_size_desc),
    BY_DATE_OF_CREATION_ASC(4, R.string.sort_by_date_of_creation_asc),
    BY_DATE_OF_CREATION_DESC(5, R.string.sort_by_date_of_creation_desc),
    BY_EXTENSION_ASC(6, R.string.sort_by_extension_asc),
    BY_EXTENSION_DESC(7, R.string.sort_by_extension_desc);

    companion object {
        fun getStringValues(context: Context): Array<String> =
            values().map { context.getString(it.stringRes) }.toTypedArray()
    }
}