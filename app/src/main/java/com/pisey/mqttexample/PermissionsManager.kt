package com.pisey.mqttexample

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.pisey.mqttexample.PermissionCheckerFragment


/**
 * Injects code to ask for permissions before executing any code that requires permissions
 * defined in the annotation
 */
fun Context.runWithPermissions(
    vararg permissions: String,
    callback: () -> Unit
) {
    return runWithPermissionsHandler(this, permissions = permissions, callback)
}

/**
 * Injects code to ask for permissions before executing any code that requires permissions
 * defined in the annotation
 */
fun Fragment.runWithPermissions(
    vararg permissions: String,
    callback: () -> Unit
) {
    runWithPermissionsHandler(this, permissions = permissions, callback)
}

fun Context.runWithGPSPermissions(callback: () -> Unit) {
    runWithPermissionGPSHandler(this, callback)
}

fun Fragment.runWithGPSPermissions(callback: () -> Unit) {
    runWithPermissionGPSHandler(this, callback)
}

fun Context.runWithLocationPermission(callback: () -> Unit) {
    runWithGPSPermissions { runWithPermissionsHandler(this, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, callback = callback) }
}

fun Context.runWithBackgroundLocationPermission(callback: () -> Unit) {
    runWithGPSPermissions {
        runWithPermissionsHandler(this, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) runWithPermissionsHandler(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION, callback = callback)
            else callback()
        }
    }
}

fun Fragment.runWithBackgroundLocationPermission(callback: () -> Unit) {
    runWithGPSPermissions {
        runWithPermissionsHandler(this, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) runWithPermissionsHandler(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION, callback = callback)
            else callback()
        }
    }
}

private fun runWithPermissionGPSHandler(target: Any, callback: () -> Unit) {

    // support for AppCompatActivity and Activity
    var permissionCheckerFragment = when (target) {
        // for app compat activity
        is AppCompatActivity -> {
            target.supportFragmentManager.findFragmentByTag(
                PermissionCheckerFragment::class.java.canonicalName
            ) as PermissionCheckerFragment?
        }
        // for support fragment
        is FragmentActivity -> {
            target.supportFragmentManager.findFragmentByTag(
                PermissionCheckerFragment::class.java.canonicalName
            ) as PermissionCheckerFragment?
        }

        is Fragment -> {
            target.childFragmentManager.findFragmentByTag(
                PermissionCheckerFragment::class.java.canonicalName
            ) as PermissionCheckerFragment?
        }

        else -> {
            // cannot handle the permission checking from the any class other than AppCompatActivity/Fragment
            // crash the app RIGHT NOW!
            throw IllegalStateException("Found " + target::class.java.canonicalName + " : No support from any classes other than AppCompatActivity/Fragment")
        }
    }

    // check if permission check fragment is added or not
    // if not, add that fragment
    if (permissionCheckerFragment == null) {
        permissionCheckerFragment = PermissionCheckerFragment()
        when (target) {
            is AppCompatActivity -> {
                target.supportFragmentManager.beginTransaction().apply {
                    add(permissionCheckerFragment, PermissionCheckerFragment::class.java.canonicalName)
                    commit()
                }
                // make sure fragment is added before we do any context based operations
                target.supportFragmentManager.executePendingTransactions()
            }

            is FragmentActivity -> {
                // this does not work at the moment
                target.supportFragmentManager.beginTransaction().apply {
                    add(permissionCheckerFragment, PermissionCheckerFragment::class.java.canonicalName)
                    commit()
                }
                // make sure fragment is added before we do any context based operations
                target.supportFragmentManager.executePendingTransactions()
            }

            is Fragment -> {
                // this does not work at the moment
                target.childFragmentManager.beginTransaction().apply {
                    add(permissionCheckerFragment, PermissionCheckerFragment::class.java.canonicalName)
                    commit()
                }
                // make sure fragment is added before we do any context based operations
                target.childFragmentManager.executePendingTransactions()
            }
        }
    }
    // start requesting permissions for the first time
    permissionCheckerFragment.requestPermissionGPSFromUser(callback)
}


private fun runWithPermissionsHandler(target: Any, vararg permissions: String, callback: () -> Unit) {

    val context = when (target) {
        is AppCompatActivity -> target.applicationContext
        is FragmentActivity -> target.applicationContext
        is DialogFragment -> target.requireContext()
        is Fragment -> target.requireContext()
        else -> {
            // cannot handle the permission checking from the any class other than AppCompatActivity/Fragment
            // crash the app RIGHT NOW!
            throw IllegalStateException("Found " + target::class.java.canonicalName + " : No support from any classes other than AppCompatActivity/Fragment")
        }
    }

    // check if we have the permissions
    if (context?.hasSelfPermission(permissions = permissions) == true) {
        callback()
    } else {
        // we don't have required permissions
        // begin the permission request flow

        // check if we have permission checker fragment already attached

        // support for AppCompatActivity and Activity
        var permissionCheckerFragment = when (target) {
            // for app compat activity
            is AppCompatActivity -> {
                target.supportFragmentManager.findFragmentByTag(
                    PermissionCheckerFragment::class.java.canonicalName
                ) as PermissionCheckerFragment?
            }
            // for support fragment
            is FragmentActivity -> {
                target.supportFragmentManager.findFragmentByTag(
                    PermissionCheckerFragment::class.java.canonicalName
                ) as PermissionCheckerFragment?
            }

            is DialogFragment -> {
                target.childFragmentManager.findFragmentByTag(
                    PermissionCheckerFragment::class.java.canonicalName
                ) as PermissionCheckerFragment?
            }
            // else return null
            else -> null
        }

        // check if permission check fragment is added or not
        // if not, add that fragment
        if (permissionCheckerFragment == null) {
            permissionCheckerFragment = PermissionCheckerFragment()
            when (target) {
                is AppCompatActivity -> {
                    target.supportFragmentManager.beginTransaction().apply {
                        add(permissionCheckerFragment, PermissionCheckerFragment::class.java.canonicalName)
                        commit()
                    }
                    // make sure fragment is added before we do any context based operations
                    target.supportFragmentManager.executePendingTransactions()
                }

                is FragmentActivity -> {
                    // this does not work at the moment
                    target.supportFragmentManager.beginTransaction().apply {
                        add(permissionCheckerFragment, PermissionCheckerFragment::class.java.canonicalName)
                        commit()
                    }
                    // make sure fragment is added before we do any context based operations
                    target.supportFragmentManager.executePendingTransactions()
                }

                is DialogFragment -> {
                    // this does not work at the moment
                    target.childFragmentManager.beginTransaction().apply {
                        add(permissionCheckerFragment, PermissionCheckerFragment::class.java.canonicalName)
                        commit()
                    }
                    // make sure fragment is added before we do any context based operations
                    target.childFragmentManager.executePendingTransactions()
                }
            }
        }
        // start requesting permissions for the first time
        permissionCheckerFragment.requestPermissionsFromUser(permissions = permissions) { callback() }
    }
}
