package com.pilou.woca.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.support.v7.widget.RecyclerView
import com.pilou.woca.SimpleClass.Card
import com.pilou.woca.SimpleClass.CardItem
import com.pilou.woca.R

class SwipeCardAdapter (cards : MutableList<Card>) : RecyclerView.Adapter<CardItem>() {

    //private val items = ArrayList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))
    private var items : MutableList<Card> = cards
    private var cardsNumber:Int = items.size
    private var positions : MutableList<Int> = createPositionsList(cardsNumber)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardItem {
        return CardItem(
            LayoutInflater.from(parent.context).inflate(
                R.layout.content_swipeable_card,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CardItem, position: Int) {
        val numberOfCards = (positions[position]).toString() + "/" + cardsNumber
        holder.bind(items[position], numberOfCards)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getItems(): MutableList<Card>  {
        return items
    }

    fun removeTopItem() {
        items.removeAt(0)
        positions.removeAt(0)
        notifyDataSetChanged()
    }

    private fun createPositionsList(size:Int) : MutableList<Int> {
        val list : MutableList<Int> = mutableListOf()
        for (i in 1 until size + 1 )
            list.add(i)

        return list
    }
}