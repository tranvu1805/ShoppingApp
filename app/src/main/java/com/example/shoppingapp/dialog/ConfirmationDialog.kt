package com.example.shoppingapp.dialog

import android.app.AlertDialog
import android.content.Context

fun Context.showConfirmationDialog(
    title: String,
    message: String,
    positiveButtonText: String = "Yes",
    negativeButtonText: String = "No",
    onPositiveClick: () -> Unit,
) {
    val alert = AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton(positiveButtonText) { dialog, _ ->
            onPositiveClick()
            dialog.dismiss()
        }
        setNegativeButton(negativeButtonText) { dialog, _ ->
            dialog.dismiss()
        }
    }
    alert.create()
    alert.show()
}