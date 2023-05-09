package com.epetrashko.filetraveler.manager

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceManager @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    fun getString(
        @StringRes resId: Int
    ): String = context.getString(resId)

    fun getString(
        @StringRes resId: Int,
        vararg formatArgs: Any
    ): String = context.getString(resId, *formatArgs)

}