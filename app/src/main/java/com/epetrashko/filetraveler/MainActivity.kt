package com.epetrashko.filetraveler

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.epetrashko.filetraveler.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val viewModel by viewModels<MainViewModel>()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        if (checkPermissions())
            viewModel.checkIsWorking()
        else
            Log.d("ksdjf", "No permissions")
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
                viewModel.checkIsWorking()
            } else {
                // TODO
            }
        }
    }

    private fun hasAllPermissions(): Boolean =
        EXTERNAL_PERMS.all { perm ->
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm)
        }

    companion object {
        private val EXTERNAL_PERMS = arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        private const val EXTERNAL_REQUEST = 66
    }
}