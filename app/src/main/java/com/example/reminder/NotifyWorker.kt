package com.example.reminder

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


// Notification worker to schedule the reminders and execute notifications when time is due
class NotifyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private var auth = FirebaseAuth.getInstance().currentUser
    private lateinit var databaseReference: DatabaseReference

    override fun doWork(): Result {
        val reminderTitleInput =
            inputData.getString("title") ?: return Result.failure()
        val reminderDescriptionInput =
            inputData.getString("description") ?: return Result.failure()
        val reminderObjectIDInput =
                inputData.getString("reminder_object_id") ?: return Result.failure()

        updateReminderSeenValue(reminderObjectIDInput)
        createNotification(reminderTitleInput, reminderDescriptionInput)
        return Result.success()
    }

    // Create notification for users and show the application name and reminder name. Direct user to
    // main pages after user push notification
    private fun createNotification(title: String, description: String) {

        var notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(applicationContext, MainPage::class.java)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel("101", "channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(applicationContext, "101")
            .setContentTitle(title)
            .setContentText(description)
                .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_background)


        notificationManager.notify(1, notificationBuilder.build())

    }

    // Update reminder seen value to true that main page will show the due reminder
    private fun updateReminderSeenValue(itemObjectId: String){
        val itemReference = this.getDatabaseReference(itemObjectId)
        itemReference.child("reminder_seen").setValue(true)
    }

    private fun getDatabaseReference(itemObjectId: String): DatabaseReference {
        this.databaseReference = FirebaseDatabase.getInstance().reference
        return this.databaseReference.child(Constants.FIREBASE_ITEM).child(itemObjectId)
    }
}