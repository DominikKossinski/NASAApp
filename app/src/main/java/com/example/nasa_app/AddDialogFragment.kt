package com.example.nasa_app

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.widget.DatePicker
import com.example.nasa_app.activities.MainActivity
import java.util.*

class AddDialogFragment : DialogFragment() {

    companion object {
        fun newInstance(activity: MainActivity): AddDialogFragment {
            val alertDialog = AddDialogFragment()
            alertDialog.activity = activity
            return alertDialog
        }
    }

    var activity: MainActivity? = null
    var alertDialog: AlertDialog? = null
    var datePicker: DatePicker? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val layoutInflater = activity!!.layoutInflater
        val dialogView = layoutInflater.inflate(R.layout.add_dialog_layout, null)

        datePicker = dialogView.findViewById(R.id.datePicker)
        datePicker!!.maxDate = Date().time
        datePicker!!.minDate = DBHelper.simpleDateFormat.parse("1995-06-16").time

        builder.setView(dialogView)
        builder.setPositiveButton(
            getString(android.R.string.ok)
        ) { dialog, which ->
            val year = datePicker!!.year
            val month = datePicker!!.month + 1
            val day = datePicker!!.dayOfMonth
            if (BuildConfig.DEBUG) {
                Log.d("AddDialogFragment", "Date: $year-$month-$day")
            }
            activity!!.getArticleByDate("$year-$month-$day")
        }

        builder.setNegativeButton(getString(android.R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }
        alertDialog = builder.create()
        return alertDialog!!

    }

}