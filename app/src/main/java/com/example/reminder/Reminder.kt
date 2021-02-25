package com.example.reminder


// Class for Reminder, which is used to storage several information for user, notification etc.
class Reminder {
    companion object Factory {
        fun create(): Reminder = Reminder()
    }

    var object_id: String? = null
    var message: String? = null
    var reminder_time: String? = null
    var  reminder_seen: Boolean? = false
    var  creation_time: String? = "None"
    var  creator_id: String? = "None"
    var  location_x: Double = 0.0
    var  location_y: Double = 0.0
}


