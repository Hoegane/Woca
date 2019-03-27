package com.pilou.woca

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    //TODO : Handle decks of cards
    //TODO : decks backup in firebase ?
    //TODO : save the current deck id
    //TODO : edit deck information (label ok, img nok)

    var dbHandler: DatabaseHandler? = null
    var current_deck_id:Int = 0
    var decks:MutableList<Deck> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        dbHandler = DatabaseHandler(this)
        decks = dbHandler!!.getAllDecks()

        tv_deck_label.text = decks[current_deck_id].label

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        bt_show_word_card.setOnClickListener(this)
        bt_add_word.setOnClickListener(this)
        bt_show_all_words.setOnClickListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START))
            super.onBackPressed()
        else
            drawer_layout.openDrawer(GravityCompat.START)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            R.id.action_edit_deck -> {
                val alert = AlertDialog.Builder(this)
                var etDeckLabel: EditText

                with (alert) {
                    //setTitle("Title of Alert")

                    etDeckLabel=EditText(context)
                    etDeckLabel.hint="Deck label"
                    //editTextAge!!.inputType = InputType.TYPE_CLASS_NUMBER

                    setPositiveButton("OK") { dialog, whichButton ->
                        val newDeckLabel = etDeckLabel.text.toString()
                        if (newDeckLabel != "") {
                            var deck = decks[current_deck_id]
                            deck.label = newDeckLabel
                            dbHandler!!.updateDeck(deck)
                            tv_deck_label.text = newDeckLabel
                            //showMessage("display the game score or anything!")
                            dialog.dismiss()
                        }
                        else
                            Toast.makeText(applicationContext, "Le nouveau label du paquet ne peut pas être vide", Toast.LENGTH_SHORT).show()
                    }

                    setNegativeButton("Cancel") { dialog, whichButton ->
                        //showMessage("Close the game or anything!")
                        dialog.dismiss()
                    }
                }

                val dialog = alert.create()
                dialog.setView(etDeckLabel)
                dialog.show()

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {         }
            R.id.nav_slideshow -> {           }
            R.id.nav_manage -> {            }
            R.id.nav_share -> {            }
            R.id.nav_send -> {            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.bt_show_word_card -> {
                if (dbHandler!!.getCardsNumber(current_deck_id) != 0)
                    startActivity(Intent(this, WordCardActivity::class.java))
                else
                    Toast.makeText(applicationContext, "Il n'y a pas de cartes dans le paquet", Toast.LENGTH_SHORT).show()
            }
            R.id.bt_add_word -> startActivity(Intent(this, EditCardActivity::class.java))
            R.id.bt_show_all_words -> startActivity(Intent(this, AllWordsActivity::class.java))
        }
    }
}
