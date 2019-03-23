package com.pilou.woca

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_word_card.*
class WordCardActivity : AppCompatActivity(), View.OnClickListener {

    //TODO : modify view to allow user to swipe cards in a tinder way
    //TODO : integrate the word colors
    //TODO : allow users to edit cards

    private var cards:MutableList<Card> = mutableListOf()
    private var current_word_id:Int = 0

    var dbHandler: DatabaseHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_card)

        dbHandler = DatabaseHandler(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        bt_previous_word.setOnClickListener(this)
        bt_show_word.setOnClickListener(this)
        bt_next_word.setOnClickListener(this)

        cards = dbHandler!!.getAllCards()
        displayWord(current_word_id)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.word_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            R.id.menu_edit_card -> Toast.makeText(applicationContext, "edit", Toast.LENGTH_SHORT).show()
            R.id.menu_delete_card -> {
                if (dbHandler!!.deleteCard(cards[current_word_id])) {
                    cards.removeAt(current_word_id)
                    if (current_word_id>0) {
                        current_word_id--
                        displayWord(current_word_id)
                        Toast.makeText(applicationContext, "delete", Toast.LENGTH_SHORT).show()
                    }
                    else if (cards.size > 0) {
                        displayWord(current_word_id)
                        Toast.makeText(applicationContext, "delete", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(applicationContext, "Le paquet ne contient plus de cartes", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                else
                    Toast.makeText(applicationContext, "Echec", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.bt_previous_word -> {
                if (cards.size !=0) {
                    if (current_word_id == 0)
                        current_word_id = cards.size - 1
                    else
                        current_word_id--
                    if (ll_card_traduction.visibility == View.VISIBLE)
                        ll_card_traduction.visibility = View.INVISIBLE
                    displayWord(current_word_id)
                }
            }
            R.id.bt_show_word -> {
                if (ll_card_traduction.visibility == View.INVISIBLE)
                    ll_card_traduction.visibility = View.VISIBLE
                else
                    ll_card_traduction.visibility = View.INVISIBLE
            }
            R.id.bt_next_word -> {
                if (cards.size !=0) {
                    if (current_word_id == cards.size - 1)
                        current_word_id = 0
                    else
                        current_word_id++
                    if (ll_card_traduction.visibility == View.VISIBLE)
                        ll_card_traduction.visibility = View.INVISIBLE
                    displayWord(current_word_id)
                }
            }
        }
    }

    private fun displayWord(id:Int){
        val numberOfCards = (id+1).toString() + "/" + cards.size
        tv_word_number.text = numberOfCards

        tv_word.text = cards[id].word
        tv_word_example.text = "Ex : " + cards[id].word_example

        tv_transl_1.text = "1. " + cards[id].translation_1
        tv_transl_1_ex.text = "Ex : " + cards[id].translation_1_example

        tv_transl_2.text = "2. " + cards[id].translation_2
        tv_transl_2_ex.text = "Ex : " + cards[id].translation_2_example

        tv_transl_3.text = "3. " + cards[id].translation_3
        tv_transl_3_ex.text = "Ex : " + cards[id].translation_3_example
    }

}
