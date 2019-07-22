package com.pilou.woca

import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.content_swipeable_card.view.*


class CardItem(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val tv_word_number : TextView
    private val tv_word : TextView
    private val tv_word_example : TextView

    private val tv_transl_1 : TextView
    private val tv_transl_1_ex : TextView
    private val tv_transl_2 : TextView
    private val tv_transl_2_ex : TextView
    private val tv_transl_3 : TextView
    private val tv_transl_3_ex : TextView

    init {
        tv_word_number = itemView.tv_word_number
        tv_word = itemView.tv_word
        tv_word_example = itemView.tv_word_example

        tv_transl_1 = itemView.tv_transl_1
        tv_transl_1_ex = itemView.tv_transl_1_ex
        tv_transl_2 = itemView.tv_transl_2
        tv_transl_2_ex = itemView.tv_transl_2_ex
        tv_transl_3 = itemView.tv_transl_3
        tv_transl_3_ex = itemView.tv_transl_3_ex
    }

    fun bind(card: Card, wordNumber : String) {
        tv_word_number.text = wordNumber
        tv_word.text = card.word
        tv_word_example.text = card.word_example

        tv_transl_1.text = card.translation_1
        tv_transl_1_ex.text = card.translation_1_example
        tv_transl_2.text = card.translation_2
        tv_transl_2_ex.text = card.translation_2_example
        tv_transl_3.text = card.translation_3
        tv_transl_3_ex.text = card.translation_3_example
    }
}