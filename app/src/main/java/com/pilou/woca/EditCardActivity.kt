package com.pilou.woca

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_edit_card.*
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import android.support.v4.app.NavUtils
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_word_card.*


class EditCardActivity : AppCompatActivity(), View.OnClickListener {

    //TODO : add color picker
    //TODO : Modify the sub part of the card to make it prettier

    private var bouboucolor:Int = 0
    private var cardId:Int = -1
    private var deckId:Int = -1
    private var dbHandler: DatabaseHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_card)

        dbHandler = DatabaseHandler(this)

        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        bt_save_card.setOnClickListener(this)
        bt_translation_3_color.setOnClickListener(this)

        deckId = intent.getIntExtra("deckId",-1)
        cardId = intent.getIntExtra("cardId",-1)

        if (cardId != -1) {
            val card = dbHandler!!.getCardById(cardId)
            cardId = card.id
            et_word.setText(card.word)
            et_word_example.setText(card.word_example)
            et_translation_1.setText(card.translation_1)
            et_translation_1_example.setText(card.translation_1_example)
            et_translation_2.setText(card.translation_2)
            et_translation_2_example.setText(card.translation_2_example)
            et_translation_3.setText(card.translation_3)
            et_translation_3_example.setText(card.translation_3_example)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                this.finish()
                return true
            }
            R.id.menu_change_deck ->  {
                Toast.makeText(applicationContext, "Change deck", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.bt_translation_3_color -> {
                val dialogBuilder = AlertDialog.Builder(this)

                val colorPickerLayout = layoutInflater.inflate(R.layout.color_picker, null)

                dialogBuilder.setView(colorPickerLayout)
                    .setCancelable(false)
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                            dialog, id -> dialog.cancel()
                    })
                    .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                            dialog, id -> bt_translation_3_color.setBackgroundColor(bouboucolor)
                    })

                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle("AlertDialogExample")
                // show alert dialog
                alert.show()
            }
            R.id.bt_save_card -> {
                if (et_word.text.toString() != "" && et_translation_1.text.toString() != "") {
                    val card = Card()
                    card.id = cardId
                    card.deck_id = deckId
                    card.word = et_word.text.toString()
                    card.word_color = 0
                    card.word_example = et_word_example.text.toString()
                    card.translation_1 = et_translation_1.text.toString()
                    card.translation_1_color = 0
                    card.translation_1_example = et_translation_1_example.text.toString()
                    card.translation_2 = et_translation_2.text.toString()
                    card.translation_2_color = 0
                    card.translation_2_example = et_translation_2_example.text.toString()
                    card.translation_3 = et_translation_3.text.toString()
                    card.translation_3_color = 0
                    card.translation_3_example = et_translation_3_example.text.toString()

                    when(cardId) {
                        -1 -> {
                            dbHandler!!.addCard(card)

                            /*val success = dbHandler!!.addCard(card)
                            if (success){
                                val toast = Toast.makeText(this,"Saved Successfully", Toast.LENGTH_LONG).show()
                            }*/
                        }
                        else -> dbHandler!!.updateCard(card)
                    }
                    finish()
                }
                else
                    Toast.makeText(applicationContext, "Les champs 'Mot' et 'Traduction' sont obligatoires", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
