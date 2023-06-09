package com.epetrashko.filetraveler.utils

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.epetrashko.filetraveler.R
import kotlinx.coroutines.launch


fun View.setVisibility(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun LifecycleOwner.launchWhenStarted(block: suspend () -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            block()
        }
    }
}

fun String.getExtensionOrNull(): String? = substringAfterLast(".").ifEmpty { null }

fun AppCompatActivity.checkRequiredPermissions(permissionCode: Int): Boolean {
    var isPermissionOn = true
    val version = Build.VERSION.SDK_INT
    if (version >= Build.VERSION_CODES.R) {
        if (Environment.isExternalStorageManager()) return true
        startActivity(
            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                .apply {
                    addCategory("android.intent.category.DEFAULT")
                    data = getPackageUri()
                }
        )
        isPermissionOn = false
    } else if (version >= Build.VERSION_CODES.M) {
        val perms = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        if (!hasAllPermissions(perms)) {
            ActivityCompat.requestPermissions(
                this,
                perms,
                permissionCode
            )
        }
    }
    return isPermissionOn
}

private fun AppCompatActivity.getPackageUri(): Uri =
    Uri.parse("package:$packageName")

fun AppCompatActivity.hasAllPermissions(perms: Array<String>): Boolean =
    perms.all { perm ->
        PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm)
    }

fun AppCompatActivity.requestToOpenFile(mimeType: String?, data: Uri) {
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
    } catch (e: Exception) {
        showUnexpectedError()
    }
}

fun AppCompatActivity.requestToShareFile(mimeType: String?, data: Uri) {
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
    } catch (e: Exception) {
        showUnexpectedError()
    }
}

private fun AppCompatActivity.showUnexpectedError() {
    Toast.makeText(
        this,
        getString(R.string.unexpected_error),
        Toast.LENGTH_LONG
    ).show()
}

