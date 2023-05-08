package com.epetrashko.filetraveler

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.epetrashko.domain.usecase.ObserveChangedFilesUseCase
import com.epetrashko.domain.usecase.TraverseAndCalculateHashUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainService : Service() {

    @Inject
    lateinit var traverseUseCase: TraverseAndCalculateHashUseCase
    @Inject
    lateinit var observeChangedFilesUseCase: ObserveChangedFilesUseCase

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val binder: MainBinder = MainBinder()

    override fun onBind(p0: Intent?): IBinder = binder

    override fun onDestroy() {
        Log.d("jfdhdhfuyfdh", "Stop service")
        job.cancelChildren()
        super.onDestroy()
    }

    fun updateHashJob() {
        scope.launch {
            traverseUseCase()
        }
    }

    inner class MainBinder : Binder() {
        fun getService(): MainService = this@MainService
    }

}