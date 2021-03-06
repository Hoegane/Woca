package com.pilou.woca.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.Toast
import com.pilou.woca.adapter.AllWordsAdapter
import com.pilou.woca.simpleClass.Card
import com.pilou.woca.database.DatabaseHandler
import com.pilou.woca.R
import kotlinx.android.synthetic.main.activity_all_words.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

class AllWordsActivity : AppCompatActivity() {

    //TODO : add a 'search' feature

    private lateinit var adapter: AllWordsAdapter
    private var deckId:Int = -1
    private var cards:MutableList<Card> = mutableListOf()
    private var lastUpdatedCardPos:Int = -1
    private var dbHandler: DatabaseHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_all_words)
        dbHandler = DatabaseHandler(this)

        deckId = intent.getIntExtra("deckId",-1)
        cards = when (deckId) {
            -1 -> dbHandler!!.getAllCards()
            else -> dbHandler!!.getCardsFromDeck(deckId)
        }

        adapter = AllWordsAdapter(this, cards)
        lv_all_words.adapter = adapter
        lv_all_words.onItemClickListener = AdapterView.OnItemClickListener{ parent, view, position, id ->

            lastUpdatedCardPos = position
            val mIntent = Intent(this, EditCardActivity::class.java)
            val mBundle = Bundle()
            mBundle.putInt("cardId", cards[position].id)
            mBundle.putInt("deckId", deckId)
            mIntent.putExtras(mBundle)
            startActivity(mIntent)
        }

        lv_all_words.onItemLongClickListener = AdapterView.OnItemLongClickListener{ parent, view, position, id ->
            Log.e(">> Allword - onlongite", cards[position].word + ", Card DeckId : " + cards[position].deck_id)
            alert("Supprimer cette carte ?", "") {
                yesButton {
                    if (dbHandler!!.deleteCard(cards[position])) {
                        cards.removeAt(position)
                        adapter.notifyDataSetChanged()

                        tv_word_count.text = getString(R.string.all_cards_act_words_count, cards.size)
                    }
                    else
                        Toast.makeText(applicationContext, "Echec", Toast.LENGTH_SHORT).show()
                }
                noButton {}
            }.show()
            true
        }

        tv_word_count.text = getString(R.string.all_cards_act_words_count, cards.size)
    }

    override fun onResume() {
        super.onResume()
        if (lastUpdatedCardPos != -1) {
            cards[lastUpdatedCardPos] = dbHandler!!.getCardById(cards[lastUpdatedCardPos].id)
            adapter.notifyDataSetChanged()
            lastUpdatedCardPos = -1
        }
    }
}
