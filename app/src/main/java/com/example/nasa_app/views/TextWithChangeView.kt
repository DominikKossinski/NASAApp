package com.example.nasa_app.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.nasa_app.databinding.ViewCustomSwitchBinding
import com.example.nasa_app.databinding.ViewTextWithChangeBinding

class TextWithChangeView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val binding = ViewTextWithChangeBinding.inflate(LayoutInflater.from(context), this)

    private var onChangeClickListener: (() -> Unit)? = null

    var text: String = ""
        set(value) {
            field = value
            binding.textView.text = value
        }


    init {
        binding.changeTv.setOnClickListener {
            onChangeClickListener?.invoke()
        }
    }

    fun setOnChangeClickListener(listener: () -> Unit) {
        onChangeClickListener = listener
    }
}