package com.pilou.woca

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter

import kotlinx.android.synthetic.main.activity_swipe.*
import swipeable.com.layoutmanager.OnItemSwiped
import swipeable.com.layoutmanager.SwipeableTouchHelperCallback
import android.support.v7.widget.AppCompatButton
import swipeable.com.layoutmanager.SwipeableLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.app_bar_main.*
import swipeable.com.layoutmanager.touchelper.ItemTouchHelper


class SwipeActivity : AppCompatActivity() {

    private var adapter: ListAdapter? = null
    private var swipeableTouchHelperCallback: SwipeableTouchHelperCallback ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swipe)
        setSupportActionBar(toolbar)
        adapter = ListAdapter()
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
        })

        {
            override fun getAllowedSwipeDirectionsMovementFlags(viewHolder: RecyclerView.ViewHolder?): Int {
                return ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT //or ItemTouchHelper.UP or ItemTouchHelper.DOWN
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeableTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recycler_view)
        recycler_view.setLayoutManager(
            SwipeableLayoutManager().setAngle(10)
                .setAnimationDuratuion(300)
                .setMaxShowCount(3)
                .setScaleGap(0.1f)
                .setTransYGap(0)
        )
        recycler_view.setAdapter(adapter)

        btn_swipe.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                itemTouchHelper.swipe(recycler_view.findViewHolderForAdapterPosition(0), ItemTouchHelper.RIGHT)
            }
        })
    }

}
