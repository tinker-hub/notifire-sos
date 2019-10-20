package com.tinker.notifire.common.services

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import com.bsk.floatingbubblelib.FloatingBubbleActionListener
import com.bsk.floatingbubblelib.FloatingBubbleConfig
import com.bsk.floatingbubblelib.FloatingBubbleService
import com.tinker.notifire.R
import com.tinker.notifire.common.Constants.LOG.EMERGENCY_BUBBLE
import com.tinker.notifire.features.EmergencyBubbleActivity

class EmergencyBubbleService : FloatingBubbleService(), FloatingBubbleActionListener {

    @SuppressLint("InflateParams")
    override fun getConfig(): FloatingBubbleConfig {
        Log.d(EMERGENCY_BUBBLE, "Setup config")

        return FloatingBubbleConfig.Builder()
            .bubbleIcon(R.layout.layout_bubble_view)
            .removeBubbleIcon(R.layout.layout_remove_bubble_view)
            .bubbleIconDp(84)
            .removeBubbleIconDp(84)
            .physicsEnabled(true)
            .onActionListener(this)
            .build()
    }

    override fun onGetIntent(intent: Intent): Boolean {
        return true
    }

    override fun onExpandedView() {
        val dialogIntent = Intent(this, EmergencyBubbleActivity::class.java)
        dialogIntent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        startActivity(dialogIntent)
    }

    override fun onBubbleViewCreated() {

    }

    override fun onBubbleViewClosed() {

    }

    override fun onDragToRemove() {

    }

    override fun onBubbleViewClicked() {

    }
}