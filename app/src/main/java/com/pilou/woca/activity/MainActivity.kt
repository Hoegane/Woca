package com.pilou.woca.activity

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.SubMenu
import android.view.View
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import com.pilou.woca.database.DatabaseHandler
import com.pilou.woca.simpleClass.Deck
import com.pilou.woca.R
import com.pilou.woca.simpleClass.JsonDeck
import com.squareup.moshi.Moshi
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    //TODO : Add a Tag system
    //  - adapt DB : OK
    //  - adapt UI : NOK
    //  - add filtering before show cards
    //TODO : Allow user to change a card from one deck to another
    //TODO : decks backup in firebase ?
    //TODO : Improve the the way the deckId is shared between all activities (global var ?)
    //TODO : edit deck information (label ok, img nok)
    //TODO : improve icon shape, size, and provide a round one
    //TODO : allow user to change decks order
    //TODO : create a settings page

    var dbHandler: DatabaseHandler? = null
    var currentDeckPos:Int = 0
    var decks:MutableList<Deck> = mutableListOf()
    lateinit var channelMenu:SubMenu
    private var mPrefs:SharedPreferences ?= null
    private val REQUEST_CODE_ASK_PERMISSIONS = 111
    private val REQUEST_CODE_IMPORT_JSON_FILE = 112

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        /*var calendar = Calendar.getInstance()
        var today:Date = calendar.time
        var dateFormatter:DateFormat = SimpleDateFormat("dd-MM-yyyy")
        Log.e("onCreate", ">>" + dateFormatter.format(today))

        var today2:Date = dateFormatter.parse("31-07-1991")
        Log.e("onCreate", ">>" + dateFormatter.format(today2))*/

        dbHandler = DatabaseHandler(this)
        decks = dbHandler!!.getAllDecks()

        mPrefs = getSharedPreferences("wocaSharedPreferences", 0)
        currentDeckPos = mPrefs!!.getInt("currentDeckPos", 0)

        if (currentDeckPos >= decks.size) {
            currentDeckPos = 0
            mPrefs!!.edit().putInt("currentDeckPos", currentDeckPos).apply()
        }

        tv_deck_label.text = decks[currentDeckPos].label

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        bt_show_word_card.setOnClickListener(this)
        bt_add_word.setOnClickListener(this)
        bt_show_all_words.setOnClickListener(this)
        bt_stats.setOnClickListener(this)
        iv_deck_img_1.setOnClickListener(this)
        iv_deck_img_2.setOnClickListener(this)

        val menu:Menu = nav_view.menu
        channelMenu = menu.addSubMenu("Paquets")
        for (deck_pos in 0 until decks.size)
            channelMenu.add(0, deck_pos, 0, decks[deck_pos].label)
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
            R.id.action_settings -> {
                Toast.makeText(applicationContext, "Go to settings page (Feature not implememted yet)", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_import -> {

                checkUserPermissionsBeforeImporting()
                return true
            }
            R.id.action_export -> {
                Toast.makeText(applicationContext, "export (Feature not implememted yet)", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_edit_deck -> {
                val alert = AlertDialog.Builder(this)
                var etDeckLabel: EditText

                with (alert) {
                    //setTitle("Title of Alert")

                    etDeckLabel=EditText(context)
                    etDeckLabel.hint="Deck label"
                    etDeckLabel.setText(tv_deck_label.text.toString())
                    //editTextAge!!.inputType = InputType.TYPE_CLASS_NUMBER

                    setPositiveButton("OK") { dialog, _ ->
                        val newDeckLabel = etDeckLabel.text.toString()
                        if (newDeckLabel == "")
                            Toast.makeText(applicationContext, "Le nouveau label du paquet ne peut pas être vide", Toast.LENGTH_SHORT).show()
                        else if (newDeckLabel == tv_deck_label.text)
                            Toast.makeText(applicationContext, "Le nouveau label doit etre différent du précédent", Toast.LENGTH_SHORT).show()
                        else {
                            val deck = decks[currentDeckPos]
                            deck.label = newDeckLabel
                            dbHandler!!.updateDeck(deck)
                            tv_deck_label.text = newDeckLabel
                            channelMenu.getItem(currentDeckPos).title = newDeckLabel
                            //showMessage("display the game score or anything!")
                            dialog.dismiss()
                        }
                    }

                    setNegativeButton("Cancel") { dialog, _ ->
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
            /*R.id.nav_camera -> {}
            R.id.nav_gallery -> {}
            R.id.nav_slideshow -> {}
            R.id.nav_manage -> {}
            R.id.nav_share -> {}
            R.id.nav_send -> {}*/
            R.id.nav_show_all_decks -> startActivity(Intent(this, AllDecksActivity::class.java))
            R.id.nav_create_deck -> {
                Toast.makeText(applicationContext, "Create deck", Toast.LENGTH_SHORT).show()
                val deck = Deck()
                deck.label = "Nouveau deck"
                dbHandler!!.addDeck(deck)
                //decks.add(deck)
                decks = dbHandler!!.getAllDecks()
                Log.e("OnNavItem - create item", "" + decks.size)

                channelMenu.add(0, decks.size-1, 0, deck.label)

                currentDeckPos = decks.size-1
                mPrefs!!.edit().putInt("currentDeckPos", currentDeckPos).apply()
                tv_deck_label.text = deck.label
            }
            R.id.nav_all_cards -> startActivity(Intent(this, AllWordsActivity::class.java))
            R.id.nav_swipe_activity -> {
                val mBundle = Bundle()
                mBundle.putInt("deckId", decks[currentDeckPos].id)
                startActivity(Intent(this, SwipeActivity::class.java).putExtras(mBundle))
            }
            else -> {
                currentDeckPos = item.itemId
                mPrefs!!.edit().putInt("currentDeckPos", currentDeckPos).apply()
                tv_deck_label.text = decks[currentDeckPos].label
                Log.e("OnNavItem - click deck", "CurrentDeckPos : " + currentDeckPos)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onClick(view: View) {
        val mBundle = Bundle()
        mBundle.putInt("deckId", decks[currentDeckPos].id)
        var mIntent:Intent ?= null
        when (view.id) {
            R.id.bt_show_word_card ->
                if (dbHandler!!.getUnknownCardsNumber(decks[currentDeckPos].id) != 0)
                    mIntent = Intent(this, WordCardActivity::class.java)
                else
                    Toast.makeText(applicationContext, "Il n'y a pas de cartes dans le paquet", Toast.LENGTH_SHORT).show()
            R.id.bt_add_word ->  mIntent = Intent(this, EditCardActivity::class.java)
            R.id.bt_show_all_words -> mIntent = Intent(this, AllWordsActivity::class.java)
            R.id.bt_stats -> Toast.makeText(applicationContext, "stats", Toast.LENGTH_SHORT).show()
            R.id.iv_deck_img_1 -> Toast.makeText(applicationContext, "changer image 1", Toast.LENGTH_SHORT).show()
            R.id.iv_deck_img_2 -> Toast.makeText(applicationContext, "changer image 2", Toast.LENGTH_SHORT).show()
        }
        if (mIntent != null) {
            mIntent.putExtras(mBundle)
            startActivity(mIntent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_IMPORT_JSON_FILE && resultCode == RESULT_OK)
            importJson(data?.data) //The uri with the location of the file
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickFileToImport()
            } else {
                // Permission Denied
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                return
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun checkUserPermissionsBeforeImporting() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_ASK_PERMISSIONS)
                return
            }
        }
        pickFileToImport()
    }
    
    private fun pickFileToImport() {
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_CODE_IMPORT_JSON_FILE) // custom request code. 111 : import json
    }

    private fun importJson(selectedFile:Uri?) {

        Log.e("importJson", ">> " + selectedFile?.path)
        var filePath:String
        if (selectedFile?.path!!.split(":").size > 1)
            filePath = selectedFile.path!!.split(":")[1]
        else
            filePath = selectedFile.path.toString()

        Log.e("importJson", ">>2 " + filePath)

        //val jsonString:String = File("//com.android.providers.downloads.documents/document/1362").readText(Charsets.UTF_8)
        val jsonString:String = File(filePath).readText(Charsets.UTF_8)

        Toast.makeText(applicationContext, "import file : " + jsonString, Toast.LENGTH_SHORT).show()

        val moshi = Moshi.Builder().build()
        val deckOfNewCardsAdapter = moshi.adapter(JsonDeck::class.java)
        val deckOfNewCards = deckOfNewCardsAdapter.fromJson(jsonString)

        for (card in deckOfNewCards!!.cards) {
            card.deck_id = decks[currentDeckPos].id
            dbHandler!!.addCard(card)
        }

        Toast.makeText(applicationContext, deckOfNewCards.cards.size.toString() + " cards added to the current deck", Toast.LENGTH_SHORT).show()
    }
}
