package com.epetrashko.filetraveler

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.epetrashko.filetraveler.databinding.ActivityMainBinding
import com.epetrashko.filetraveler.utils.SortDirection
import com.epetrashko.filetraveler.utils.launchWhenStarted
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        adapter = MainAdapter(listOf(), this)
        binding.rvFiles.adapter = adapter
        binding.rvFiles.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        setContentView(binding.root)
        handleFlows()
        onBackPressedDispatcher.addCallback {
            viewModel.goBack()
        }
        if (checkPermissions())
            viewModel.navigateTo()
        else
            viewModel.showError()
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

    private fun checkPermissions(): Boolean {
        var isPermissionOn = true
        val version = Build.VERSION.SDK_INT
        if (version >= Build.VERSION_CODES.M) {
            if (!hasAllPermissions()) {
                ActivityCompat.requestPermissions(this, EXTERNAL_PERMS, EXTERNAL_REQUEST)
                isPermissionOn = false
            }
        }
        return isPermissionOn
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

    private fun hasAllPermissions(): Boolean =
        EXTERNAL_PERMS.all { perm ->
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm)
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

    private fun requestToOpenFile(mimeType: String?, data: Uri) {
        try {
            startActivity(
                Intent(Intent.ACTION_VIEW)
                    .apply {
                        setDataAndType(data, mimeType)
                        flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
            )
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                getString(R.string.no_application_found_for_opening),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun requestToShareFile(mimeType: String?, data: Uri) {
        try {
            startActivity(
                Intent(Intent.ACTION_SEND)
                    .apply {
                        type = mimeType
                        putExtra(Intent.EXTRA_STREAM, data)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
            )
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                getString(R.string.no_application_found_for_opening),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onClick(file: FilePresentation) {
        viewModel.onFileClick(file)
    }

    override fun onLongClick(file: FilePresentation) {
        viewModel.onFileLongClick(file)
    }

    companion object {
        private val EXTERNAL_PERMS = arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        private const val EXTERNAL_REQUEST = 66
    }
}