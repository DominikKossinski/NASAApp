package com.example.nasa_app.architecture

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import com.example.nasa_app.fragments.articles.ArticlesItemCallback
import java.lang.reflect.ParameterizedType

abstract class BasePagingDataAdapter<T : Any, VB : ViewBinding>(
    itemCallback: DiffUtil.ItemCallback<T>
) : PagingDataAdapter<T, BaseRecyclerViewAdapter.BaseViewHolder<VB>>(itemCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseRecyclerViewAdapter.BaseViewHolder<VB> {
        val vbType = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1]
        val vbClass = vbType as Class<VB>
        val method = vbClass.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        val binding = method.invoke(null, LayoutInflater.from(parent.context), parent, false) as VB
        return BaseRecyclerViewAdapter.BaseViewHolder(binding.root, binding)
    }
}