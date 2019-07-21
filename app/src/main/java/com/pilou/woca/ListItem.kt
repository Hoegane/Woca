package com.pilou.woca

import android.R
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.dummy_list_item.view.*


class ListItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
    internal var textView: TextView

    init {
        textView = itemView.dummy_text
    }

    fun bind(i: Int) {
        textView.text = i.toString()
    }
}