package com.pilou.woca

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_all_decks.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

class AllDecksActivity : AppCompatActivity() {

    private lateinit var adapter: AllDecksAdapter
    private var decks:MutableList<Deck> = mutableListOf()
    private var dbHandler: DatabaseHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_all_decks)
        dbHandler = DatabaseHandler(this)

        decks = dbHandler!!.getAllDecks()

        if (decks.size == 0)
            Log.e("AllDecksActiv - OnCreat", "Pas de decks")

        val array:MutableList<String> = mutableListOf()
        for (deck in decks)
            array.add(deck.label)

        //adapter = ArrayAdapter(this, R.layout.ticket_deck, array)
        adapter = AllDecksAdapter(this, decks)

        lv_all_decks.adapter = adapter

        lv_all_decks.onItemLongClickListener = AdapterView.OnItemLongClickListener{ _, _, position, _ ->
            Log.e(">> Allword - longitem", decks[position].label + ", DeckId : " + decks[position].id)
            alert("Supprimer ce paquet ?", "") {
                yesButton {
                    if (dbHandler!!.deleteDeck(decks[position])) {
                        decks.removeAt(position)
                        adapter.notifyDataSetChanged()

                        tv_decks_count.text = getString(R.string.all_decks_act_decks_count, decks.size)
                    }
                    else
                        Toast.makeText(applicationContext, "Echec", Toast.LENGTH_SHORT).show()
                }
                noButton {}
            }.show()
            true
        }

        tv_decks_count.text = getString(R.string.all_decks_act_decks_count, decks.size)
    }
}