package com.example.reminder

interface ReminderRowListener {
    fun modifyReminder(itemObjectId: String)
    fun onItemDelete(itemObjectId: String)
}