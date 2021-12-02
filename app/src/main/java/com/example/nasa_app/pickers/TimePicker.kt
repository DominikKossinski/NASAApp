package com.example.nasa_app.pickers

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePicker(
    private val hourAndMinute: Pair<Int, Int>?,
    private val onTimeSet: (hourOfDay: Int, minute: Int) -> Unit
) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val hour = hourAndMinute?.first ?: c.get(Calendar.HOUR_OF_DAY)
        val minute = hourAndMinute?.second ?: c.get(Calendar.MINUTE)
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        onTimeSet.invoke(hourOfDay, minute)
    }

    companion object {
        const val TAG = "TIME_PICKER"
    }
}