package com.example.nasa_app.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.nasa_app.R
import com.example.nasa_app.databinding.ViewHourSelectBinding

class HourSelectView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val binding = ViewHourSelectBinding.inflate(LayoutInflater.from(context), this)

    private var onClickListener: (() -> Unit)? = null
    var hour = 0
        set(value) {
            field = value
            binding.hourTv.text = context.getString(R.string.two_places_int_format, value)
        }
    var minute = 0
        set(value) {
            field = value
            binding.minuteTv.text = context.getString(R.string.two_places_int_format, value)
        }

    init {
        binding.root.setBackgroundResource(R.drawable.ripple_rounded_white)
        binding.root.setOnClickListener {
            onClickListener?.invoke()
        }
    }

    fun setOnClickListener(listener: () -> Unit) {
        onClickListener = listener
    }
}