package com.stackmobile.aplicativodeteste.dialog

import android.app.Activity
import android.app.AlertDialog
import com.stackmobile.aplicativodeteste.R

class DialogLoading(private val activity: Activity) {
    lateinit var dialog: AlertDialog

    fun onLoadAlertDialog() {
        val builder = AlertDialog.Builder(activity)
        val layoutInflater = activity.layoutInflater
        builder.setView(layoutInflater.inflate(R.layout.dialog_loading, null))
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }

    fun onCloseAlertDialog() {
        dialog.dismiss()
    }
}