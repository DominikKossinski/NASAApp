package com.example.nasa_app.dialogs.article

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.nasa_app.architecture.BaseDialog
import com.example.nasa_app.databinding.DialogArticleFromDateBinding
import com.example.nasa_app.extensions.toDate
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ArticleFromDateDialog : BaseDialog<ArticleFromDateViewModel, DialogArticleFromDateBinding>() {

    override val viewModel: ArticleFromDateViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.datePicker.maxDate = Date().time
        binding.datePicker.minDate = "1995-06-16".toDate().time

        binding.openButton.setOnClickListener {
            val year = binding.datePicker.year
            val month = binding.datePicker.month + 1
            val day = binding.datePicker.dayOfMonth
            val date = "%04d-%02d-%02d".format(year, month, day)
            viewModel.navigateToArticle(date)
        }
        binding.cancelButton.setOnClickListener {
            dialog?.cancel()
        }
    }

}