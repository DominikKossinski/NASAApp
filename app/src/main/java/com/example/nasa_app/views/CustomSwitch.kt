package com.example.nasa_app.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.nasa_app.R
import com.example.nasa_app.databinding.ViewCustomSwitchBinding

class CustomSwitch(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val binding = ViewCustomSwitchBinding.inflate(LayoutInflater.from(context), this)

    private var onChangeClickListener: ((checked: Boolean) -> Unit)? = null

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.CustomSwitch, 0, 0).apply {
            binding.switchTv.text = getString(R.styleable.CustomSwitch_title)
        }
        binding.root.setBackgroundResource(R.drawable.ripple_white)
//        binding.root.setOnClickListener {
//            binding.switchCompat.isChecked = !binding.switchCompat.isChecked
//        }
    }

    fun setOnChangeClickListener(listener: (Boolean) -> Unit) {
        onChangeClickListener = listener
    }

    fun setChecked(isChecked: Boolean) {
        binding.switchCompat.setOnCheckedChangeListener(null)
        binding.switchCompat.isChecked = isChecked
        binding.switchCompat.setOnCheckedChangeListener { _, checked ->
            onChangeClickListener?.invoke(checked)
        }
    }
}