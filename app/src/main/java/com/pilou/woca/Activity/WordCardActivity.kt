package com.pilou.woca.Activity

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
import android.content.Intent
import android.util.Log
import com.pilou.woca.SimpleClass.Card
import com.pilou.woca.Database.DatabaseHandler
import com.pilou.woca.R


class WordCardActivity : AppCompatActivity(), View.OnClickListener {

    //TODO : modify view to allow user to swipe cards in a tinder way
    //TODO : integrate the word colors

    private var cards:MutableList<Card> = mutableListOf()
    private var deckId:Int = -1
    private var currentWordId:Int = 0
    private var showWordAndHideTranslation = true
    private var displayUnknownCards=true
    private lateinit var menu:Menu

    private var dbHandler: DatabaseHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_card)

        dbHandler = DatabaseHandler(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        bt_previous_word.setOnClickListener(this)
        bt_show_word.setOnClickListener(this)
        bt_next_word.setOnClickListener(this)

        deckId = intent.getIntExtra("deckId",-1)

        //cards = dbHandler!!.getAllCards()
        //cards.shuffle()
        //displayWord(current_word_id)
    }

    override fun onResume() {
        super.onResume()
        if (cards.isEmpty()) {
            //cards = dbHandler!!.getCardsFromDeck(deckId)
            if (displayUnknownCards)
                cards = dbHandler!!.getUnknownCards(deckId)
            else
                cards = dbHandler!!.getKnownCards(deckId)
            cards.shuffle()
        }
        else
            cards[currentWordId] = dbHandler!!.getCardById(cards[currentWordId].id)

        //TODO : UPDATE AND DISPLAY THE WORD ONLY IF IT WAS UPDATED FROM THE EDITCARDACTIVITY
        displayWord(currentWordId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.word_card, menu)
        this.menu = menu!!
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

                currentWordId = 0
                cards.shuffle()
                displayWord(currentWordId)
            }
            R.id.menu_mark_as_learned ->  {
                val card = cards[currentWordId]
                card.is_learned = !card.is_learned
                if (card.is_learned)
                    Toast.makeText(applicationContext, "Mark as learned", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(applicationContext, "Mark as unknown", Toast.LENGTH_SHORT).show()

                if (dbHandler!!.updateCard(card)) {
                    cards.removeAt(currentWordId)
                    if (currentWordId<cards.size)
                        displayWord(currentWordId)
                    else if (currentWordId == cards.size && currentWordId > 0)
                        displayWord(--currentWordId)
                    else{
                        Toast.makeText(applicationContext, "Le paquet ne contient plus de cartes", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                else
                    Toast.makeText(applicationContext, "Echec", Toast.LENGTH_SHORT).show()

            }
            R.id.menu_known_unknown_cards -> {
                var tmpCards = mutableListOf<Card>()
                if (!displayUnknownCards) {
                    tmpCards = dbHandler!!.getUnknownCards(deckId)
                    if (tmpCards.size == 0) {
                        Toast.makeText(applicationContext, "There is no 'Unknown cards'", Toast.LENGTH_SHORT).show()
                        return super.onOptionsItemSelected(item)
                    }
                    item.title = "Known cards"
                }
                else {
                    tmpCards = dbHandler!!.getKnownCards(deckId)
                    if (tmpCards.size == 0) {
                        Toast.makeText(applicationContext, "There is no 'Known cards'", Toast.LENGTH_SHORT).show()
                        return super.onOptionsItemSelected(item)
                    }
                    item.title = "Unknown cards"
                }
                cards = tmpCards
                cards.shuffle()
                currentWordId = 0
                displayWord(currentWordId)
                displayUnknownCards = !displayUnknownCards
            }
            R.id.menu_edit_card -> {
                val mIntent = Intent(this, EditCardActivity::class.java)
                val mBundle = Bundle()
                mBundle.putInt("cardId", cards[currentWordId].id)
                mBundle.putInt("deckId", deckId)
                mIntent.putExtras(mBundle)
                startActivity(mIntent)
            }
            R.id.menu_delete_card -> {

                alert("Supprimer cette carte ?", "") {
                    yesButton {
                        if (dbHandler!!.deleteCard(cards[currentWordId])) {
                            cards.removeAt(currentWordId)
                            if (currentWordId>0) {
                                currentWordId--
                                displayWord(currentWordId)
                                Toast.makeText(applicationContext, "delete", Toast.LENGTH_SHORT).show()
                            }
                            else if (cards.size > 0) {
                                displayWord(currentWordId)
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
                    if (currentWordId == 0)
                        currentWordId = cards.size - 1
                    else
                        currentWordId--
                    reinitializeHidingStatus()
                    displayWord(currentWordId)
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
                    if (currentWordId == cards.size - 1)
                        currentWordId = 0
                    else
                        currentWordId++
                    reinitializeHidingStatus()
                    displayWord(currentWordId)
                }
            }
        }
    }

    private fun displayWord(id:Int){
        val numberOfCards = (id+1).toString() + "/" + cards.size
        tv_word_number.text = numberOfCards

        tv_word.text = cards[id].word
        tv_word_example.text = getString(R.string.word_act_example,cards[id].word_example)

        tv_transl_1.text = getString(R.string.word_act_translation_1, cards[id].translation_1)
        tv_transl_1_ex.text = getString(R.string.word_act_example,cards[id].translation_1_example)

        tv_transl_2.text = getString(R.string.word_act_translation_2, cards[id].translation_2)
        tv_transl_2_ex.text = getString(R.string.word_act_example,cards[id].translation_2_example)

        tv_transl_3.text = getString(R.string.word_act_translation_3, cards[id].translation_3)
        tv_transl_3_ex.text = getString(R.string.word_act_example,cards[id].translation_3_example)
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
