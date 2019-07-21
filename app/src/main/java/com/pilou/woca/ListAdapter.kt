package com.pilou.woca

import android.support.annotation.NonNull
import android.view.LayoutInflater
import android.view.ViewGroup
import java.util.Arrays.asList
import android.support.v7.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class ListAdapter : RecyclerView.Adapter<ListItem>() {
    private val items = ArrayList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItem {
        return ListItem(
            LayoutInflater.from(parent.context).inflate(R.layout.dummy_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListItem, position: Int) {
        holder.bind(items.get(position).toInt())
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getItems(): List<Int> {
        return items
    }

    fun removeTopItem() {
        items.removeAt(0)
        notifyDataSetChanged()
    }
}