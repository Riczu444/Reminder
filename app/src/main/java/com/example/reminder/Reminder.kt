package com.example.reminder

class Reminder {
    companion object Factory {
        fun create(): Reminder = Reminder()
    }

    var message: String? = null
    var reminder_time: String? = null
    var  reminder_seen: String? = null
    var  creation_time: String? = null
    var  creator_id: String? = null
    var  location_x: Float? = null
    var  location_y: Float? = null
}



//data class Reminder(val message: String, val reminder_time: String, val creator_id: String){
//    val location_x: Float = 0.0F
//    val location_y: Float = 0.0F
//}

