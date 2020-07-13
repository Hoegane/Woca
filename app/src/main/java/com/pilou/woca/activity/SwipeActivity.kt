package com.pilou.woca.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_swipe.*
import swipeable.com.layoutmanager.OnItemSwiped
import swipeable.com.layoutmanager.SwipeableTouchHelperCallback
import swipeable.com.layoutmanager.SwipeableLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.pilou.woca.database.DatabaseHandler
import com.pilou.woca.R
import com.pilou.woca.adapter.SwipeCardAdapter
import kotlinx.android.synthetic.main.activity_word_card.*
import swipeable.com.layoutmanager.touchelper.ItemTouchHelper

class SwipeActivity : AppCompatActivity() {

    private var adapter: SwipeCardAdapter? = null
    private var swipeableTouchHelperCallback: SwipeableTouchHelperCallback ?= null
    private var deckId:Int = -1
    private var showWordAndHideTranslation = true

    private var dbHandler: DatabaseHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swipe)

        dbHandler = DatabaseHandler(this)

        deckId = intent.getIntExtra("deckId",-1)

        val cards = dbHandler!!.getCardsFromDeck(deckId)
        cards.shuffle()

        adapter = SwipeCardAdapter(cards)
        swipeableTouchHelperCallback = object : SwipeableTouchHelperCallback(object : OnItemSwiped {
            override fun onItemSwiped() {
                adapter!!.removeTopItem()
            }

            override fun onItemSwipedLeft() {
                Log.e("SWIPE", "LEFT")
            }

            override fun onItemSwipedRight() {
                Log.e("SWIPE", "RIGHT")
            }

            override fun onItemSwipedUp() {
                Log.e("SWIPE", "UP")
            }

            override fun onItemSwipedDown() {
                Log.e("SWIPE", "DOWN")
            }
        }) {
            override fun getAllowedSwipeDirectionsMovementFlags(viewHolder: RecyclerView.ViewHolder?): Int {
                    return ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT  //or ItemTouchHelper.UP or ItemTouchHelper.DOWN
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeableTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recycler_view)

        recycler_view.layoutManager =  SwipeableLayoutManager().setAngle(10)
                .setAnimationDuratuion(200)
                .setMaxShowCount(3)
                .setScaleGap(0.1f)
                .setTransYGap(0)

        recycler_view.adapter = adapter

        btn_swipe.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {

                if (adapter!!.itemCount != 0 && recycler_view.findViewHolderForAdapterPosition(0)!!.isRecyclable) {
                    itemTouchHelper.swipe(recycler_view.findViewHolderForAdapterPosition(0), ItemTouchHelper.RIGHT)
                    Log.e("SwipeAct - btn swipe", "count : " + adapter!!.itemCount)
                }
            }
        })

        btn_show.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if (showWordAndHideTranslation)
                    showOrHide(ll_card_traduction)
                else
                    showOrHide(ll_card_word)
            }
        })
    }

    private fun showOrHide(linearLayout: LinearLayout) {
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
