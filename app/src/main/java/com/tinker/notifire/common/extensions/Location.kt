package com.tinker.notifire.common.extensions

import android.location.Location
import com.tinker.notifire.data.model.Point

fun Point.getDistance(
    targetPoint: Point
): Float {

    val locationA = Location("First Location")
    locationA.latitude = lat ?: 0.0
    locationA.longitude = long ?: 0.0

    val locationB = Location("Second Location")
    locationB.latitude = targetPoint.lat ?: 0.0
    locationB.longitude = targetPoint.long ?: 0.0

    val distance = locationA.distanceTo(locationB)

    return distance
}