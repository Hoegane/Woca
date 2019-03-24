package com.pilou.woca

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_all_words.*

class AllWordsActivity : AppCompatActivity() {

    private lateinit var adapter: AllWordsAdapter
    private var cards:MutableList<Card> = mutableListOf()

    var dbHandler: DatabaseHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_all_words)
        dbHandler = DatabaseHandler(this)

        cards = dbHandler!!.getAllCards()

        adapter = AllWordsAdapter(this, cards)
        lv_all_words.adapter = adapter
        lv_all_words.onItemClickListener = AdapterView.OnItemClickListener{ parent, view, position, id ->
            Toast.makeText(applicationContext, cards[position].word, Toast.LENGTH_SHORT).show()
        }

        lv_all_words.onItemLongClickListener = AdapterView.OnItemLongClickListener{ parent, view, position, id ->
            Toast.makeText(applicationContext, "Long : " + cards[position].word, Toast.LENGTH_SHORT).show()
            true
        }

        val wordCount = cards.size.toString() + " mots"
        tv_word_count.text = wordCount
    }


}