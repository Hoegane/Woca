package com.pilou.woca

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_word_card.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
import android.R.attr.key
import android.content.Intent
import org.jetbrains.anko.toast


class WordCardActivity : AppCompatActivity(), View.OnClickListener {

    //TODO : modify view to allow user to swipe cards in a tinder way
    //TODO : integrate the word colors
    //TODO : finish the feature 'mark word as learned'

    private var cards:MutableList<Card> = mutableListOf()
    private var current_word_id:Int = 0
    private var showWordAndHideTranslation = true

    var dbHandler: DatabaseHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_card)

        dbHandler = DatabaseHandler(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        bt_previous_word.setOnClickListener(this)
        bt_show_word.setOnClickListener(this)
        bt_next_word.setOnClickListener(this)

        //cards = dbHandler!!.getAllCards()
        //cards.shuffle()
        //displayWord(current_word_id)
    }

    override fun onResume() {
        super.onResume()
        if (cards.isEmpty()) {
            cards = dbHandler!!.getAllCards()
            cards.shuffle()
        }
        else
            cards[current_word_id] = dbHandler!!.getCardById(cards[current_word_id].id)

        //TODO : UPDATE AND DISPLAY THE WORD ONLY IF IT WAS UPDATED FROM THE EDITCARDACTIVITY
        displayWord(current_word_id)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.word_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            R.id.menu_reverse -> {
                if (showWordAndHideTranslation) {
                    ll_card_traduction.visibility = View.VISIBLE
                    ll_card_word.visibility = View.INVISIBLE
                }else {
                    ll_card_traduction.visibility = View.INVISIBLE
                    ll_card_word.visibility = View.VISIBLE
                }
                showWordAndHideTranslation = !showWordAndHideTranslation

                current_word_id = 0
                cards.shuffle()
                displayWord(current_word_id)
            }
            R.id.menu_mark_as_learned ->  {
                val card = cards[current_word_id]
                card.is_learned = !card.is_learned
                if (card.is_learned)
                    Toast.makeText(applicationContext, "Mark as learned", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(applicationContext, "Mark as unknown", Toast.LENGTH_SHORT).show()
                dbHandler!!.updateCard(card)

            }
            R.id.menu_edit_card -> {
                val mIntent = Intent(this, EditCardActivity::class.java)
                val mBundle = Bundle()
                mBundle.putInt("cardId", cards[current_word_id].id)
                mIntent.putExtras(mBundle)
                startActivity(mIntent)
            }
            R.id.menu_delete_card -> {

                alert("Supprimer cette carte ?", "") {
                    yesButton {
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
                    noButton {}
                }.show()
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
                    reinitializeHidingStatus()
                    displayWord(current_word_id)
                }
            }
            R.id.bt_show_word -> {
                if (showWordAndHideTranslation)
                    showOrHide(ll_card_traduction)
                else
                    showOrHide(ll_card_word)
            }
            R.id.bt_next_word -> {
                if (cards.size !=0) {
                    if (current_word_id == cards.size - 1)
                        current_word_id = 0
                    else
                        current_word_id++
                    reinitializeHidingStatus()
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

    private fun showOrHide(linearLayout:LinearLayout) {
        if (linearLayout.visibility == View.INVISIBLE)
            linearLayout.visibility = View.VISIBLE
        else
            linearLayout.visibility = View.INVISIBLE
    }

    private fun reinitializeHidingStatus() {
        if (ll_card_traduction.visibility == View.VISIBLE && showWordAndHideTranslation)
            ll_card_traduction.visibility = View.INVISIBLE
        else if (ll_card_traduction.visibility == View.VISIBLE && !showWordAndHideTranslation)
            ll_card_word.visibility = View.INVISIBLE
    }

}
