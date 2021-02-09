package com.example.reminder

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
    var  location_x: String? = "None"
    var  location_y: String? = "None"
}


