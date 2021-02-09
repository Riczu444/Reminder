package com.example.reminder

interface ReminderRowListener {
    //fun modifyItemState(itemCreatorId: String)
    fun onItemDelete(itemObjectId: String)
}