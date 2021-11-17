package com.example.nasa_app.architecture

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseRecyclerViewAdapter<T, VB : ViewBinding> :
    RecyclerView.Adapter<BaseRecyclerViewAdapter.BaseViewHolder<VB>>() {

    val items: ArrayList<T> = arrayListOf()

    protected var onClickListener: ((T) -> Unit)? = null

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {
        val vbType = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1]
        val vbClass = vbType as Class<VB>
        val method = vbClass.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        val binding = method.invoke(null, LayoutInflater.from(parent.context), parent, false) as VB
        return BaseViewHolder(binding.root, binding)
    }

    fun setOnItemClickListener(listener: ((T) -> Unit)?) {
        onClickListener = listener
    }

    class BaseViewHolder<VB>(itemView: View, val binding: VB) : RecyclerView.ViewHolder(itemView)
}