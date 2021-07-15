package com.sreshtha.conversionbuddy.ui.dialog

import android.app.Activity
import android.app.AlertDialog
import com.sreshtha.conversionbuddy.R


class CustomLoadingDialog(private val activity: Activity) {
    private var dialog: AlertDialog? = null

    fun startLoadingDialog() {
        val builder = AlertDialog.Builder(activity)
        val infl = activity.layoutInflater
        builder.setView(infl.inflate(R.layout.custom_loading_dialog, null))
        builder.setCancelable(false)

        dialog = builder.create()
        dialog?.show()
    }

    fun dismissDialog() {
        dialog?.dismiss()
    }

}