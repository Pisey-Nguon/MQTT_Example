package com.pisey.mqttexample

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.GET_META_DATA
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment


class PermissionMultiHandler(private val fragment: Fragment) {

    private val context: Context by lazy { fragment.requireContext() }

    private var multiplePermissionExecute: (() -> Unit)? = null
    private var permissions = ArrayList<String>()

    private val requestPermissionSettings = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (permissions.isEmpty()) return@registerForActivityResult
        if (!context.hasSelfPermission(permissions = permissions)) {
            showDialogRequestPermission(permissions)
        } else {
            multiplePermissionExecute?.invoke()
        }

    }

    private val requestMultiplePermissionNoCallbackLauncher = fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { maps ->
        val permissionDenied = maps.filter { !it.value }.map {
            val info = context.packageManager.getPermissionInfo(it.key, GET_META_DATA)
            val label = context.getString(info.labelRes)
            label
        }
        val labels = permissionDenied.joinToString(separator = ", ")
        when {
            context.hasSelfPermission(maps.map { it.key }) -> {
                multiplePermissionExecute?.let { it() }
            }

            permissions.map { fragment.shouldShowRequestPermissionRationale(it) }.contains(false) -> {
                val fullDescription = "This lets you $labels. \n" +
                        "To enable this, click App Settings below and activate these permissions."
                showDialogAllowInSettings(fullDescription)
            }

            else -> {
                val fullDescription = "This lets you $labels. \n" +
                        "Please allow these permission to use this feature."
                showDialogMultiplePermissionDenied(fullDescription)
            }
        }
    }

    fun runMultiplePermission(vararg permissions: String, execute: () -> Unit) {
        multiplePermissionExecute = execute
        this.permissions.clear()
        this.permissions.addAll(arrayOf(*permissions))
        when {
            context.hasSelfPermission(arrayListOf(*permissions)) -> {
                execute()
            }

            else -> {
                requestMultiplePermissionNoCallbackLauncher.launch(arrayOf(*permissions))
            }
        }
    }


    private fun showDialogMultiplePermissionDenied(fullDescription: String) {
        AlertDialog.Builder(context)
            .setMessage(fullDescription)
            .setPositiveButton("Allow") { dialog, _ ->
                dialog.dismiss()
                requestMultiplePermissionNoCallbackLauncher.launch(permissions.toTypedArray())
            }
            .setNegativeButton("Don't Allow") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun showDialogRequestPermission(permissions: ArrayList<String>) {
        if (permissions.isEmpty()) return
        val permissionDenied = permissions.filter { !context.hasSelfPermission(permissions) }.map {
            val info = context.packageManager.getPermissionInfo(it, GET_META_DATA)
            val label = context.getString(info.labelRes)
            label
        }
        val labels = permissionDenied.joinToString(separator = ", ", truncated = "")
        val fullDescription = "This lets you $labels. \n" +
                "To enable this, click App Settings below and activate these permissions."
        showDialogAllowInSettings(fullDescription)
    }

    private fun showDialogAllowInSettings(fullDescription: String) {
        AlertDialog.Builder(context)
            .setMessage(fullDescription)
            .setPositiveButton("App Settings") { dialog, _ ->
                dialog.dismiss()
                startApplicationDetailsActivity()
            }
            .setNegativeButton("Not Now") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun startApplicationDetailsActivity() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri: Uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        requestPermissionSettings.launch(intent)
    }


}