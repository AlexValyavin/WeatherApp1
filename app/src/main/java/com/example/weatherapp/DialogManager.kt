package com.example.weatherapp

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText

object DialogManager {
    fun search(context: Context,listener: Listener){
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        val edName = EditText(context)
        builder.setView(edName)
        val dialog = builder.create()
        dialog.setTitle("Enter city name:")
        dialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE,"Ok"){ _, _ ->
            listener.onClick(edName.text.toString())
            dialog.dismiss()
        }
        dialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE,"Cancel"){ _, _->
            dialog.dismiss()
        }
        dialog.show()
    }
    interface Listener{
        fun onClick(name: String)
    }
}

