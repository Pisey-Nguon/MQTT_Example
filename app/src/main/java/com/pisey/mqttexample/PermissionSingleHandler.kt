package com.pisey.mqttexample

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.GET_META_DATA
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment


class PermissionSingleHandler(private val fragment: Fragment) {

    private val context: Context by lazy { fragment.requireContext() }
    private var singlePermissionExecute: (() -> Unit)? = null
    private var permission: String? = null

    private val requestPermissionSettings = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (permission == null) return@registerForActivityResult
        if (!context.hasSelfPermission(permission!!)) {
            showDialogRequestPermission(permission)
        } else {
            singlePermissionExecute?.invoke()
        }

    }


    private val requestSinglePermissionNoCallbackLauncher = fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        val info = context.packageManager.getPermissionInfo(permission!!, GET_META_DATA)
        val label = context.getString(info.labelRes)
        when {
            isGranted -> {
                singlePermissionExecute?.let { it() }
            }

            !fragment.shouldShowRequestPermissionRationale(permission!!) -> {
                val fullDescription = "This lets you $label. \n" +
                        "To enable this, click App Settings below and activate this permissions"
                showDialogAllowInSettings(fullDescription)
            }

            else -> {
                val fullDescription = "This lets you $label. \n" +
                        "Please allow this permission to use this feature."
                showDialogSinglePermissionDenied(fullDescription)
            }
        }

    }

    fun runSinglePermission(permission: String, execute: () -> Unit) {
        singlePermissionExecute = execute
        this.permission = permission
        when {
            context.hasSelfPermission(permission) -> {
                execute()
            }

            else -> {
                requestSinglePermissionNoCallbackLauncher.launch(permission)
            }
        }
    }

    private fun showDialogSinglePermissionDenied(fullDescription: String) {
        AlertDialog.Builder(context)
            .setMessage(fullDescription)
            .setPositiveButton("Allow") { dialog, _ ->
                dialog.dismiss()
                requestSinglePermissionNoCallbackLauncher.launch(permission)
            }
            .setNegativeButton("Don't Allow") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun showDialogRequestPermission(permission: String?) {
        if (permission == null) return
        val info = context.packageManager.getPermissionInfo(permission, GET_META_DATA)
        val label = context.getString(info.labelRes)
        val fullDescription = "This lets you $label. \n" +
                "To enable this, click App Settings below and activate this permissions"
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