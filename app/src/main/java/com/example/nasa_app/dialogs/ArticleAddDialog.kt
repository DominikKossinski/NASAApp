package com.example.nasa_app.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import androidx.fragment.app.viewModels
import com.example.nasa_app.BuildConfig
import com.example.nasa_app.DBHelper
import com.example.nasa_app.R
import com.example.nasa_app.activities.MainActivity
import com.example.nasa_app.architecture.BaseDialog
import com.example.nasa_app.databinding.DialogArticleAddBinding
import com.example.nasa_app.extensions.toDate
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ArticleAddDialog : BaseDialog<ArticleAddViewModel, DialogArticleAddBinding>() {

    override val viewModel: ArticleAddViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.datePicker.maxDate = Date().time
        binding.datePicker.minDate = "1995-06-16".toDate().time

        binding.addButton.setOnClickListener {
            val year = binding.datePicker.year
            val month = binding.datePicker.month + 1
            val day = binding.datePicker.dayOfMonth
            if (BuildConfig.DEBUG) {
                Log.d("AddDialogFragment", "Date: $year-$month-$day")
            }
            //TODO add selected article
        }
        binding.cancelButton.setOnClickListener {
            dialog?.cancel()
        }
    }

}