package com.example.reminder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView


class ReminderAdapter(context: Context, reminderList: MutableList<Reminder>) : BaseAdapter() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var itemList = reminderList
    private var reminderRowListener:  ReminderRowListener = context as ReminderRowListener

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val message: String = itemList[position].message as String
        val reminder_time: String = itemList[position].reminder_time as String
        val object_id: String = itemList[position].object_id as String
        //val creation_time: String = itemList[position].creation_time as String
        //val creation_id: String = itemList[position].creator_id as String
        //val location_x: String = itemList[position].location_x as String
        //val location_y: String = itemList[position].location_y as String
        //val reminder_seen: String = itemList[position].reminder_seen as String

        val view: View
        val listRowHolder: ListRowHolder
        if (convertView == null) {
            view = mInflater.inflate(R.layout.row_items, parent, false)
            listRowHolder = ListRowHolder(view)
            view.tag = listRowHolder
        } else {
            view = convertView
            listRowHolder = view.tag as ListRowHolder
        }
        listRowHolder.reminder_name.text= message
        listRowHolder.reminder_time.text= reminder_time

        listRowHolder.ibDeleteObject.setOnClickListener{
            this.reminderRowListener.onItemDelete(object_id)
        }

        listRowHolder.ibEditObject.setOnClickListener{
            this.reminderRowListener.modifyReminder(object_id)
        }

        return view
    }

    override fun getItem(index: Int): Any {
        return this.itemList[index]
    }

    override fun getItemId(index: Int): Long {
        return index.toLong()
    }

    override fun getCount(): Int {
        return this.itemList.size
    }

    private class ListRowHolder(row: View?) {
        val reminder_name: TextView = row!!.findViewById<TextView>(R.id.tv_reminder_name) as TextView
        val reminder_time: TextView  = row!!.findViewById<TextView>(R.id.tv_reminder_date) as TextView
        val ibEditObject: ImageButton = row!!.findViewById<ImageButton>(R.id.ib_edit_reminder) as ImageButton
        val ibDeleteObject: ImageButton = row!!.findViewById<ImageButton>(R.id.ib_remove_reminder) as ImageButton
    }
}