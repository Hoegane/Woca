package com.pilou.woca

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.ticket_all_words.view.*
import kotlinx.android.synthetic.main.ticket_deck.view.*

class AllDecksAdapter(var context: Context, var decks:MutableList<Deck>): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val deck = decks[position]
        val deckView:View
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        deckView = inflater.inflate(R.layout.ticket_deck, null)
        deckView.tv_deck.text = deck.label

        return deckView
    }

    override fun getItem(position: Int): Any {
        return decks[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return decks.size
    }

}