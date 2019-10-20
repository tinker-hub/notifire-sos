package com.tinker.notifire.common.extensions

import android.app.Activity
import android.content.Context
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED

fun Context.checkPermission(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED
}

fun Activity.requestPermissions(vararg permissions: String) {
    ActivityCompat.requestPermissions(this, permissions, 200)
}
