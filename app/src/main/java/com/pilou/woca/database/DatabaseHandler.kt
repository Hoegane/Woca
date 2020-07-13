package com.pilou.woca.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.pilou.woca.simpleClass.Card
import com.pilou.woca.simpleClass.Deck
import java.util.*

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $CARD_TABLE_NAME " +
                "($CARD_ID Integer PRIMARY KEY AUTOINCREMENT," +
                "$CARD_DECK_ID Integer, " +
                "$CARD_WORD TEXT, " +
                "$CARD_WORD_COLOR Integer," +
                "$CARD_WORD_EXAMPLE TEXT," +
                "$CARD_IS_LEARNED INTEGER DEFAULT 0," +
                "$CARD_TAGS TEXT," +
                "$CARD_CREATION_DATE TEXT," +

                "$CARD_TRANSLATION_1 TEXT," +
                "$CARD_TRANSLATION_1_COLOR Integer," +
                "$CARD_TRANSLATION_1_EXAMPLE TEXT," +
                "$CARD_TRANSLATION_2 TEXT," +
                "$CARD_TRANSLATION_2_COLOR Integer," +
                "$CARD_TRANSLATION_2_EXAMPLE TEXT," +
                "$CARD_TRANSLATION_3 TEXT," +
                "$CARD_TRANSLATION_3_COLOR Integer," +
                "$CARD_TRANSLATION_3_EXAMPLE TEXT)")

        db?.execSQL("CREATE TABLE $DECK_TABLE_NAME " +
                "($DECK_ID Integer PRIMARY KEY AUTOINCREMENT," +
                "$DECK_LABEL TEXT, " +
                "$DECK_IMAGE_PATH_1 TEXT, " +
                "$DECK_IMAGE_PATH_2 TEXT)")

        db?.execSQL("CREATE TABLE $TAG_TABLE_NAME " +
                "($TAG_ID Integer PRIMARY KEY AUTOINCREMENT," +
                "$TAG_NAME TEXT, " +
                "$TAG_DECK TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.e(">>", "oldVersion : " + oldVersion)

        if (oldVersion < 5)
            db!!.execSQL(DATABASE_ALTER_TABLE_CARD_1);
        if (oldVersion < 9) {
            db!!.execSQL(DATABASE_ALTER_TABLE_CARD_2);
            db.execSQL(DATABASE_ALTER_TABLE_CARD_3);
        }
        if (oldVersion < 10) {
            db!!.execSQL("CREATE TABLE $TAG_TABLE_NAME " +
                    "($TAG_ID Integer PRIMARY KEY AUTOINCREMENT," +
                    "$TAG_NAME TEXT, " +
                    "$TAG_DECK TEXT)")
        }

        //db!!.execSQL("DROP TABLE " + CARD_TABLE_NAME + ";")
        //db.execSQL("DROP TABLE " + DECK_TABLE_NAME + ";")
        //db.execSQL("DROP TABLE " + TAG_TABLE_NAME + ";")
        //onCreate(db)
    }

    private fun getCardsFromCursor(cursor: Cursor):MutableList<Card> {
        val allCards = mutableListOf<Card>()
        if (cursor.moveToFirst()) {
            do {
                val card = Card()
                card.id = cursor.getString(cursor.getColumnIndex(CARD_ID)).toInt()
                card.deck_id = cursor.getString(cursor.getColumnIndex(CARD_DECK_ID)).toInt()
                card.word = cursor.getString(cursor.getColumnIndex(CARD_WORD))
                card.word_color = cursor.getString(cursor.getColumnIndex(CARD_WORD_COLOR)).toInt()
                card.word_example = cursor.getString(cursor.getColumnIndex(CARD_WORD_EXAMPLE))

                card.is_learned = cursor.getInt(cursor.getColumnIndex(CARD_IS_LEARNED)) != 0

                if (cursor.getString(cursor.getColumnIndex(CARD_TAGS)) != null )
                    card.tags.add(cursor.getString(cursor.getColumnIndex(CARD_TAGS)))

                card.creationDate = if (cursor.getString(cursor.getColumnIndex(CARD_CREATION_DATE)) != null ) Date(cursor.getString(cursor.getColumnIndex(CARD_CREATION_DATE))) else Date("31/07/1991")

                //Log.e(">>", card.id.toString() + " - " + card.word + ":" + tmp)
                //card.is_learned = cursor.getInt(cursor.getColumnIndex(CARD_WORD_EXAMPLE))

                card.translation_1 = cursor.getString(cursor.getColumnIndex(CARD_TRANSLATION_1))
                card.translation_1_color = cursor.getString(cursor.getColumnIndex(
                    CARD_TRANSLATION_1_COLOR
                )).toInt()
                card.translation_1_example = cursor.getString(cursor.getColumnIndex(
                    CARD_TRANSLATION_1_EXAMPLE
                ))

                card.translation_2 = cursor.getString(cursor.getColumnIndex(CARD_TRANSLATION_2))
                card.translation_2_color = cursor.getString(cursor.getColumnIndex(
                    CARD_TRANSLATION_2_COLOR
                )).toInt()
                card.translation_2_example = cursor.getString(cursor.getColumnIndex(
                    CARD_TRANSLATION_2_EXAMPLE
                ))

                card.translation_3 = cursor.getString(cursor.getColumnIndex(CARD_TRANSLATION_3))
                card.translation_3_color = cursor.getString(cursor.getColumnIndex(
                    CARD_TRANSLATION_3_COLOR
                )).toInt()
                card.translation_3_example = cursor.getString(cursor.getColumnIndex(
                    CARD_TRANSLATION_3_EXAMPLE
                ))

                allCards.add(card)
            } while (cursor.moveToNext())
        }
        return allCards
    }

    fun getUnknownCardsNumber(deckId:Int):Int {
        //TODO : Use cursor.count ?
        val db = this.writableDatabase
        val selectALLQuery = "SELECT * FROM $CARD_TABLE_NAME WHERE $CARD_DECK_ID = $deckId AND $CARD_IS_LEARNED = 0"
        val cursor = db.rawQuery(selectALLQuery, null)
        var size = 0
        //cursor.count
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    size++
                } while (cursor.moveToNext())
            }
        cursor.close()
        return size
    }

    fun getDeckSize(deck_id:Int):Int {
        //TODO : Use cursor.count ?
        val db = this.writableDatabase
        val selectALLQuery = "SELECT * FROM $CARD_TABLE_NAME WHERE $CARD_DECK_ID = $deck_id"
        val cursor = db.rawQuery(selectALLQuery, null)
        var size = 0
        //cursor.count
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    size++
                } while (cursor.moveToNext())
            }
        cursor.close()
        return size
    }

    fun addCard(card: Card): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(CARD_DECK_ID, card.deck_id)
        values.put(CARD_WORD, card.word)
        values.put(CARD_WORD_COLOR, card.word_color)
        values.put(CARD_WORD_EXAMPLE, card.word_example)

        values.put(CARD_IS_LEARNED, card.is_learned)
        values.put(CARD_TAGS, if (card.tags.size == 0) "" else card.tags[0])
        values.put(CARD_CREATION_DATE, card.creationDate.toString())

        values.put(CARD_TRANSLATION_1, card.translation_1)
        values.put(CARD_TRANSLATION_1_COLOR, card.translation_1_color)
        values.put(CARD_TRANSLATION_1_EXAMPLE, card.translation_1_example)

        values.put(CARD_TRANSLATION_2, card.translation_2)
        values.put(CARD_TRANSLATION_2_COLOR, card.translation_2_color)
        values.put(CARD_TRANSLATION_2_EXAMPLE, card.translation_2_example)

        values.put(CARD_TRANSLATION_3, card.translation_3)
        values.put(CARD_TRANSLATION_3_COLOR, card.translation_3_color)
        values.put(CARD_TRANSLATION_3_EXAMPLE, card.translation_3_example)

        val updateResult = db.insert(CARD_TABLE_NAME, null, values)
        db.close()
        Log.v("Insert Card", "$updateResult")
        return (Integer.parseInt("$updateResult") != -1)
    }

    fun updateCard(card: Card): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(CARD_DECK_ID, card.deck_id)
        values.put(CARD_WORD, card.word)
        values.put(CARD_WORD_COLOR, card.word_color)
        values.put(CARD_WORD_EXAMPLE, card.word_example)
        values.put(CARD_IS_LEARNED, if (card.is_learned) 1 else 0)
        values.put(CARD_TAGS, if (card.tags.size == 0) "" else card.tags[0])
        values.put(CARD_CREATION_DATE, card.creationDate.toString())

        values.put(CARD_TRANSLATION_1, card.translation_1)
        values.put(CARD_TRANSLATION_1_COLOR, card.translation_1_color)
        values.put(CARD_TRANSLATION_1_EXAMPLE, card.translation_1_example)
        values.put(CARD_TRANSLATION_2, card.translation_2)
        values.put(CARD_TRANSLATION_2_COLOR, card.translation_2_color)
        values.put(CARD_TRANSLATION_2_EXAMPLE, card.translation_2_example)
        values.put(CARD_TRANSLATION_3, card.translation_3)
        values.put(CARD_TRANSLATION_3_COLOR, card.translation_3_color)
        values.put(CARD_TRANSLATION_3_EXAMPLE, card.translation_3_example)

        val updateResult = db.update(CARD_TABLE_NAME, values, "id="+card.id, null)
        db.close()
        Log.v("card updated", "$updateResult")
        return (Integer.parseInt("$updateResult") != -1)
    }

    fun deleteCard(card: Card): Boolean {
        val db = this.writableDatabase
        return db.delete(CARD_TABLE_NAME, CARD_ID + "=" + card.id, null) > 0
    }

    fun getCardById(card_id:Int): Card {
        var card = Card()
        val db = readableDatabase
        val selectALLQuery = "SELECT * FROM $CARD_TABLE_NAME WHERE $CARD_ID = $card_id"
        val cursor = db.rawQuery(selectALLQuery, null)
        if (cursor != null)
            card = getCardsFromCursor(cursor)[0]
        cursor.close()
        db.close()
        return card
    }

    fun getUnknownCards(deckId: Int): MutableList<Card> {
        return getCardsFromDeck(deckId, 0)
    }

    fun getKnownCards(deckId: Int): MutableList<Card> {
        return getCardsFromDeck(deckId, 1)
    }

    private fun getCardsFromDeck(deckId: Int, condition: Int): MutableList<Card> {
        var allCards = mutableListOf<Card>()
        val db = readableDatabase

        val selectALLQuery = when (condition) {
            0 -> "SELECT * FROM $CARD_TABLE_NAME WHERE $CARD_DECK_ID = $deckId AND $CARD_IS_LEARNED = 0" //All unknown cards
            1 -> "SELECT * FROM $CARD_TABLE_NAME WHERE $CARD_DECK_ID = $deckId AND $CARD_IS_LEARNED = 1" //All known cards
            else -> "SELECT * FROM $CARD_TABLE_NAME WHERE $CARD_DECK_ID = $deckId"
        }

        val cursor = db.rawQuery(selectALLQuery, null)
        if (cursor != null)
            allCards = getCardsFromCursor(cursor)
        cursor.close()
        db.close()
        return allCards
    }

    fun getCardsFromDeck(deckId: Int): MutableList<Card> {
        var allCards = mutableListOf<Card>()
        val db = readableDatabase
        val selectALLQuery = "SELECT * FROM $CARD_TABLE_NAME WHERE $CARD_DECK_ID = $deckId"
        val cursor = db.rawQuery(selectALLQuery, null)
        if (cursor != null)
            allCards = getCardsFromCursor(cursor)
        cursor.close()
        db.close()
        return allCards
    }

    fun getAllCards(): MutableList<Card> {
        var allCards = mutableListOf<Card>()
        val db = readableDatabase
        val selectALLQuery = "SELECT * FROM $CARD_TABLE_NAME"
        val cursor = db.rawQuery(selectALLQuery, null)
        if (cursor != null)
            allCards = getCardsFromCursor(cursor)
        cursor.close()
        db.close()
        return allCards
    }

    /*fun getAllCardsString(): String {
        var allCards = ""
        val db = readableDatabase
        val selectALLQuery = "SELECT * FROM $CARD_TABLE_NAME"
        val cursor = db.rawQuery(selectALLQuery, null)
        if (cursor != null)
            allCards = getCardsFromCursor(cursor)
        cursor.close()
        db.close()
        return allCards
    }*/

    fun addDeck(deck: Deck): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(DECK_LABEL, deck.label)
        values.put(DECK_IMAGE_PATH_1, deck.img_path_1)
        values.put(DECK_IMAGE_PATH_2, deck.img_path_2)

        val isSuccess = db.insert(DECK_TABLE_NAME, null, values)
        db.close()
        Log.v("Insert Deck", "$isSuccess")
        return (Integer.parseInt("$isSuccess") != -1)
    }

    fun updateDeck(deck: Deck): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(DECK_LABEL, deck.label)
        values.put(DECK_IMAGE_PATH_1, deck.img_path_1)
        values.put(DECK_IMAGE_PATH_2, deck.img_path_2)

        val isSuccess = db.update(DECK_TABLE_NAME, values, "id="+deck.id, null)
        db.close()
        Log.v("Update Deck", "$isSuccess")
        return (Integer.parseInt("$isSuccess") != -1)
    }

    fun deleteDeck(deck: Deck): Boolean {
        val db = this.writableDatabase
        return db.delete(DECK_TABLE_NAME, DECK_ID + "=" + deck.id, null) > 0
    }

    fun getAllDecks(): MutableList<Deck> {
        val allDecks = mutableListOf<Deck>()
        val db = readableDatabase
        val selectALLQuery = "SELECT * FROM $DECK_TABLE_NAME"
        val cursor = db.rawQuery(selectALLQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val deck = Deck()

                    deck.id = cursor.getString(cursor.getColumnIndex(DECK_ID)).toInt()
                    deck.label = cursor.getString(cursor.getColumnIndex(DECK_LABEL))
                    deck.img_path_1 = cursor.getString(cursor.getColumnIndex(DECK_IMAGE_PATH_1))
                    deck.img_path_2 = cursor.getString(cursor.getColumnIndex(DECK_IMAGE_PATH_2))

                    allDecks.add(deck)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        if (allDecks.size == 0) {
            val deck = Deck()
            deck.label = "Default label"
            addDeck(deck)
            allDecks.add(getAllDecks()[0])
            Log.e("Dbhandler - getAllDecks", "Default deck created")
        }

        db.close()
        return allDecks
    }

    companion object {
        private val DB_NAME = "CardsDB"
        private val DB_VERSION = 10

        private val CARD_TABLE_NAME = "card"
        private val CARD_ID = "id"
        private val CARD_DECK_ID = "deck_id"
        private val CARD_WORD = "word"
        private val CARD_WORD_COLOR = "word_color"
        private val CARD_WORD_EXAMPLE = "word_example"
        private val CARD_IS_LEARNED = "is_learned"
        private val CARD_TAGS = "tags"
        private val CARD_CREATION_DATE = "creation_date"

        private val CARD_TRANSLATION_1 = "translation_1"
        private val CARD_TRANSLATION_1_COLOR = "translation_1_color"
        private val CARD_TRANSLATION_1_EXAMPLE = "translation_1_example"

        private val CARD_TRANSLATION_2 = "translation_2"
        private val CARD_TRANSLATION_2_COLOR = "translation_2_color"
        private val CARD_TRANSLATION_2_EXAMPLE = "translation_2_example"

        private val CARD_TRANSLATION_3 = "translation_3"
        private val CARD_TRANSLATION_3_COLOR = "translation_3_color"
        private val CARD_TRANSLATION_3_EXAMPLE = "translation_3_example"

        private val DECK_TABLE_NAME = "deck"
        private val DECK_ID = "id"
        private val DECK_LABEL = "deck_label"
        private val DECK_IMAGE_PATH_1 = "deck_img_path_1"
        private val DECK_IMAGE_PATH_2 = "deck_img_path_2"

        private val TAG_TABLE_NAME = "tag"
        private val TAG_ID = "id"
        private val TAG_NAME = "tag_name"
        private val TAG_DECK = "tag_deck"


        private val DATABASE_ALTER_TABLE_CARD_1 = ("ALTER TABLE " + CARD_TABLE_NAME + " ADD COLUMN " + CARD_IS_LEARNED + " INTEGER DEFAULT 0;")
        private val DATABASE_ALTER_TABLE_CARD_2 = ("ALTER TABLE " + CARD_TABLE_NAME + " ADD COLUMN " + CARD_TAGS + " TEXT;")
        private val DATABASE_ALTER_TABLE_CARD_3 = ("ALTER TABLE " + CARD_TABLE_NAME + " ADD COLUMN " + CARD_CREATION_DATE + " TEXT;")
    }
}