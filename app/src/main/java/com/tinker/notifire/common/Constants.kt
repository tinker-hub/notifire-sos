package com.tinker.notifire.common

object Constants {

    object LOG {
        const val FIREBASE = "FIREBASE"
        const val EMERGENCY_BUBBLE = "EMERGENCY BUBBLE"
        const val TRACKING = "TRACKING"
    }

    object SHARED_PREFS {
        const val NOTIFIRE_PREFS = "NOTIFIRE_PREFS"
        const val FIREBASE_TOKEN = "FIREBASE_TOKEN"
        const val USER_FIREBASE_ID = "USER_FIREBASE_ID"
        const val USER_FIREBASE_NAME = "USER_FIREBASE_NAME"
    }

    object FIREBASE {
        const val USERS_COLLECTION = "users"
        const val ZONES_COLLECTION = "zones"
        const val FIRE_REPORTS_COLLECTION = "fire_reports"
    }
}