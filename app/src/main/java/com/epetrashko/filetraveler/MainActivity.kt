package com.epetrashko.filetraveler

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
    }

    override fun onResume() {
        super.onResume()
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
                    rvFiles.setVisibility(false)
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
            .setTitle(resources.getString(R.string.sort_title))
            .setSingleChoiceItems(
                SortDirection.getStringValues(this),
                viewModel.sortDirection.id
            ) { dialogInterface, i ->
                viewModel.updateSortDirection(i)
                dialogInterface.cancel()
            }.show()
    }

    override fun onClick(name: String, isDirectory: Boolean) {
        viewModel.onFileClick(name, isDirectory)
    }

    override fun onLongClick(name: String, isDirectory: Boolean) {
        viewModel.onFileLongClick(name, isDirectory)
    }

    companion object {
        private val EXTERNAL_PERMS = arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        private const val EXTERNAL_REQUEST = 66
    }
}