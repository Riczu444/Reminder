package com.example.reminder.db

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface UserDao {
    @Insert
    fun addUser(user: User)
}