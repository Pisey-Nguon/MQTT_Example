package com.pisey.mqttexample

import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class PermissionGPSHandler(private val fragment: Fragment) {

    private var permissionGPSExecutor: (() -> Unit)? = null

    private val resolutionForResult = fragment.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) permissionGPSExecutor?.invoke()
    }

    fun runGPSPermission(execute: () -> Unit) {
        permissionGPSExecutor = execute
        LocationUtils.enableLoc(fragment.requireActivity(),
            allowRequest = { permissionGPSExecutor?.invoke() },
            launchRequest = { resolutionForResult.launch(it) }
        )
    }
}