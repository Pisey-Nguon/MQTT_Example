package com.pisey.mqttexample


import androidx.fragment.app.Fragment
import com.pisey.mqttexample.PermissionGPSHandler
import com.pisey.mqttexample.PermissionMultiHandler
import com.pisey.mqttexample.PermissionSingleHandler

/**
 * This fragment holds the single permission request and holds it until the flow is completed
 */
class PermissionCheckerFragment : Fragment() {

    private val permissionSingleHandler = PermissionSingleHandler(this)
    private val permissionMultiHandler = PermissionMultiHandler(this)
    private val permissionGPSHandler = PermissionGPSHandler(this)


    fun requestPermissionsFromUser(vararg permissions: String, onGranted: () -> Unit) {
        if (permissions.size == 1) permissionSingleHandler.runSinglePermission(permission = permissions[0], execute = onGranted)
        else permissionMultiHandler.runMultiplePermission(permissions = permissions, execute = onGranted)
    }

    fun requestPermissionGPSFromUser(onGranted: () -> Unit) {
        permissionGPSHandler.runGPSPermission(onGranted)
    }


}
