package com.pilou.woca

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.ticket_all_words.view.*

class AllWordsAdapter(var context: Context, var cards:MutableList<Card>): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val card = cards[position]
        var cardView:View
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        cardView = inflater.inflate(R.layout.ticket_all_words, null)
        cardView.tv_word.text = card.word
        cardView.tv_translation.text = card.translation_1

        return cardView
    }

    override fun getItem(position: Int): Any {
        return cards[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return cards.size
    }

}