package com.pilou.woca

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_all_words.*

class AllWordsActivity : AppCompatActivity() {

    private lateinit var adapter: AllWordsAdapter

    var dbHandler: DatabaseHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_all_words)
        dbHandler = DatabaseHandler(this)
        val adapter = AllWordsAdapter(this, dbHandler!!.getAllCards())

        lv_all_words.adapter = adapter
    }
}
