package com.epetrashko.filetraveler

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.epetrashko.filetraveler.databinding.ActivityMainBinding
import com.epetrashko.filetraveler.utils.SortDirection
import com.epetrashko.filetraveler.utils.checkRequiredPermissions
import com.epetrashko.filetraveler.utils.launchWhenStarted
import com.epetrashko.filetraveler.utils.requestToOpenFile
import com.epetrashko.filetraveler.utils.requestToShareFile
import com.epetrashko.filetraveler.utils.setVisibility
import com.epetrashko.filetraveler.viewModel.MainNews
import com.epetrashko.filetraveler.viewModel.MainState
import com.epetrashko.filetraveler.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), FileItemCallback {
    private val viewModel by viewModels<MainViewModel>()

    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: MainAdapter

    private lateinit var service: MainService
    private val serviceConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder = p1 as MainService.MainBinder
            service = binder.getService()
            viewModel.startService()
        }

        override fun onServiceDisconnected(p0: ComponentName?) = Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        adapter = MainAdapter(listOf(), this)
        binding.rvFiles.adapter = adapter
        binding.rvFiles.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        setContentView(binding.root)
        handleFlows()
        addOnBackListener()
        proceedIfHasPermissions {
            startFileService()
            viewModel.navigateTo()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_main_sort -> {
                showSortDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onDestroy() {
        stopService(Intent(this, MainService::class.java))
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == EXTERNAL_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.navigateTo()
            } else {
                viewModel.showError()
            }
        }
    }

    override fun onClick(file: FilePresentation) {
        proceedIfHasPermissions {
            viewModel.onFileClick(file)
        }
    }

    override fun onLongClick(file: FilePresentation) {
        viewModel.onFileLongClick(file)
    }

    private fun handleState(state: MainState) {
        with(binding) {
            tvFullRoute.text = state.currentRoute
            when (state) {
                is MainState.Data -> {
                    adapter.updateList(state.files)
                    rvFiles.setVisibility(true)
                    cpiFiles.setVisibility(false)
                }
                is MainState.Loading -> {
                    cpiFiles.setVisibility(true)
                }
                is MainState.Error -> {
                    rvFiles.setVisibility(false)
                    cpiFiles.setVisibility(false)
                }
            }

        }
    }

    private fun handleFlows() {
        launchWhenStarted {
            viewModel.state.collectLatest { state ->
                handleState(state)
            }
        }
        launchWhenStarted {
            viewModel.news.collect { news ->
                when (news) {
                    MainNews.Finish -> finish()
                    MainNews.StartService ->
                        kotlin.runCatching { service.updateHashJob() }
                    is MainNews.OpenFile -> requestToOpenFile(
                        mimeType = news.mimeType,
                        data = news.data
                    )
                    is MainNews.ShareFile -> requestToShareFile(
                        mimeType = news.mimeType,
                        data = news.data
                    )
                }
            }
        }
    }

    private fun addOnBackListener() {
        onBackPressedDispatcher.addCallback {
            viewModel.goBack()
        }
    }

    private fun startFileService() {
        val i = Intent(this, MainService::class.java)
        bindService(i, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun proceedIfHasPermissions(
        action: () -> Unit
    ) {
        if (checkRequiredPermissions(permissionCode = EXTERNAL_REQUEST, perms = EXTERNAL_PERMS))
            action()
        else
            viewModel.showError()
    }

    private fun showSortDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.sort_title))
            .setSingleChoiceItems(
                SortDirection.getStringValues(this),
                viewModel.sortDirection.id
            ) { dialogInterface, i ->
                viewModel.updateSortDirection(i)
                dialogInterface.cancel()
            }.show()
    }

    companion object {
        private val EXTERNAL_PERMS = arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        private const val EXTERNAL_REQUEST = 66
    }
}