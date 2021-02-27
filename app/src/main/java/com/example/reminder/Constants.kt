package com.example.reminder

// Constant value for Firebase reminder item
object Constants {
    const val FIREBASE_ITEM: String = "reminder_item"
    const val GEOFENCE_RADIUS = 200
    const val GEOFENCE_ID = "REMINDER_GEOFENCE_ID"
    const val GEOFENCE_EXPIRATION = 10 * 24 * 60 * 60 * 1000 // 10 days
    const val GEOFENCE_DWELL_DELAY =  10 * 1000 // 10 secs // 2 minutes
    const val GEOFENCE_LOCATION_REQUEST_CODE = 12345
    const val CAMERA_ZOOM_LEVEL = 13f
    const val LOCATION_REQUEST_CODE = 123
}