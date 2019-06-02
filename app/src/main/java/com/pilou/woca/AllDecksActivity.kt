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

    //TODO : Update dynamically the screen when deleting a deck

    private lateinit var adapter: ArrayAdapter<String>
    private var decks:MutableList<Deck> = mutableListOf()
    private var dbHandler: DatabaseHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_all_decks)
        dbHandler = DatabaseHandler(this)

        decks = dbHandler!!.getAllDecks()

        if (decks.size == 0)
            Log.e("AllDecksActiv - OnCreat", "Pas de decks")

        var array:MutableList<String> = mutableListOf()
        for (deck in decks)
            array.add(deck.label)

        adapter = ArrayAdapter(this, R.layout.ticket_deck, array)

        lv_all_decks.adapter = adapter

        lv_all_decks.onItemLongClickListener = AdapterView.OnItemLongClickListener{ parent, view, position, id ->
            alert("Supprimer ce paquet ?", "") {
                yesButton {
                    if (dbHandler!!.deleteDeck(decks[position])) {
                        decks.removeAt(position)
                        adapter.notifyDataSetChanged()

                        tv_decks_count.text = decks.size.toString() + " mots"
                    }
                    else
                        Toast.makeText(applicationContext, "Echec", Toast.LENGTH_SHORT).show()
                }
                noButton {}
            }.show()
            true
        }

        tv_decks_count.text = decks.size.toString() + " mots"
    }
}