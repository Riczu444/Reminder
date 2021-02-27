package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class GeofenceReceiver : BroadcastReceiver() {
    lateinit var key: String
    lateinit var text: String
    private lateinit var databaseReference: DatabaseReference

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            val geofencingTransition = geofencingEvent.geofenceTransition

            if (geofencingTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofencingTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                // Retrieve data from intent
                if (intent != null) {
                    key = intent.getStringExtra("key")!!
                    text = intent.getStringExtra("message")!!
                }

                val firebase = Firebase.database
                val reference = firebase.getReference(Constants.FIREBASE_ITEM)
                val reminderListener = object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val reminder = snapshot.getValue<Reminder>()
                        updateReminderSeenValue(key)
                        if (reminder != null) {
                            MapsActivity
                                .showNotification(
                                    context.applicationContext,
                                    text
                                )
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println("reminder:onCancelled: ${error.details}")
                    }

                }

                val child = reference.child(key)
                child.addValueEventListener(reminderListener)

                // remove geofence
                val triggeringGeofences = geofencingEvent.triggeringGeofences
                MapsActivity.removeGeofences(context, triggeringGeofences)
            }
        }
    }

    private fun updateReminderSeenValue(itemObjectId: String){
        val itemReference = this.getDatabaseReference(itemObjectId)
        itemReference.child("reminder_seen").setValue(true)
    }

    private fun getDatabaseReference(itemObjectId: String): DatabaseReference {
        this.databaseReference = FirebaseDatabase.getInstance().reference
        return this.databaseReference.child(Constants.FIREBASE_ITEM).child(itemObjectId)
    }
}