package com.example.savethecat_colormatching.Controllers

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.util.Log
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R
import com.shephertz.app42.gaming.multiplayer.client.WarpClient
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener

class MultiPlayerController {

    private val apiKey:String = "0d060465b202e38b86e0a96ac5ad2063e1cb6c4634bd7e89e26c8afb3aea24bc"
    private val secretKey:String = "0d060465b202e38b86e0a96ac5ad2063e1cb6c4634bd7e89e26c8afb3aea24bc"
    private var playerID:String = ""
    private var myGame:WarpClient? = null


    companion object {
        var connected:Boolean = false
        var calledDisconnect:Boolean = false
    }

    fun connectToClient(playerID:String) {
        this.playerID = playerID
        WarpClient.initialize(apiKey, secretKey)
        myGame = WarpClient.getInstance()
        myGame!!.addConnectionRequestListener(ConnectionListener())
    }

    fun didGetPlayerID():Boolean {
        return (playerID != "")
    }

    fun connect() {
        if (MainActivity.isGooglePlayGameServicesAvailable && MainActivity.isInternetReachable) {
            myGame!!.connectWithUserName(MainActivity.localPlayer!!.displayName)
//            myGame!!.joinRoomInRange(2, 2, true)
        } else {
            displayFailureReason()
        }
    }

    fun disconnect() {
        if (MainActivity.isGooglePlayGameServicesAvailable && MainActivity.isInternetReachable) {
            calledDisconnect = true
            myGame!!.disconnect()
        } else {
            displayFailureReason()
        }
    }

    fun displayFailureReason() {
        if (!MainActivity.isInternetReachable) {
            MainActivity.gameNotification!!.displayNoInternet()
        }
        if (!MainActivity.isGooglePlayGameServicesAvailable) {
            MainActivity.gameNotification!!.displayNoGooglePlayGameServices()
        }
    }
}

class ConnectionListener:ConnectionRequestListener {
    override fun onDisconnectDone(p0: ConnectEvent?) {
        MultiPlayerController.calledDisconnect = true
        Log.i("CONNECTION STATE", "DISCONNECT")
    }

    override fun onConnectDone(p0: ConnectEvent?) {
        MultiPlayerController.connected = true
        Log.i("CONNECTION STATE", "CONNECT")
    }

    override fun onInitUDPDone(p0: Byte) {
        Log.i("CONNECTION STATE", "UDP")
    }
}

enum class  MGPosition {
    TOPLEFT,
    TOPRIGHT,
    BOTTOMLEFT,
    BOTTOMRIGHT,
    CENTER
}

class SearchMG(button: Button,
               parentLayout:AbsoluteLayout,
               params:LayoutParams,
               topLeftCorner:Pair<Int, Int>,
               bottomRightCorner:Pair<Int, Int>) {

    private var buttonMG: Button? = null
    private var searchContext: Context? = null
    private var originalParams: LayoutParams? = null
    private var parentLayout: AbsoluteLayout? = null
    private var targetParams: MutableMap<MGPosition, LayoutParams>? = mutableMapOf()
    private var nextTarget: MGPosition? = null
    private var previousTarget: MGPosition? = null

    init {
        setupMGButton(button)
        setupOriginalParams(params)
        setupParentLayout(parentLayout)
        setupTargetParams(topLeftCorner, bottomRightCorner)
        setupTransitionAnimation()
        setStyle()
    }

    private fun setupTargetParams(topLeft: Pair<Int, Int>, bottomRight: Pair<Int, Int>) {
        targetParams!![MGPosition.TOPLEFT] = LayoutParams(
            originalParams!!.width,
            originalParams!!.height, topLeft.first, topLeft.second
        )
        targetParams!![MGPosition.TOPRIGHT] = LayoutParams(
            originalParams!!.width,
            originalParams!!.height, bottomRight.first - originalParams!!.width, topLeft.second
        )
        targetParams!![MGPosition.BOTTOMLEFT] = LayoutParams(
            originalParams!!.width,
            originalParams!!.height, topLeft.first, bottomRight.second - originalParams!!.height
        )
        targetParams!![MGPosition.BOTTOMRIGHT] = LayoutParams(
            originalParams!!.width,
            originalParams!!.height, bottomRight.first - originalParams!!.width,
            bottomRight.second - originalParams!!.height
        )
    }

    private fun setupMGButton(button: Button) {
        button.alpha = 0f
        buttonMG = button
        searchContext = button.context
    }

    private fun setupOriginalParams(params: LayoutParams) {
        originalParams = params
        buttonMG!!.layoutParams = params
        targetParams!![MGPosition.CENTER] = params
    }

    private fun setupParentLayout(layout: AbsoluteLayout) {
        parentLayout = layout
        layout.addView(buttonMG!!)
    }

    private fun setNextTarget() {
        var targets: MutableList<MGPosition> = mutableListOf(
            MGPosition.TOPLEFT, MGPosition.TOPRIGHT,
            MGPosition.BOTTOMLEFT, MGPosition.BOTTOMRIGHT, MGPosition.CENTER
        )
        var index: Int
        if (nextTarget != null) {
            index = targets.indexOf(nextTarget!!)
            targets.removeAt(index)
            if (previousTarget != null) {
                index = targets.indexOf(previousTarget!!)
                targets.removeAt(index)
            }
            previousTarget = nextTarget!!
        }
        nextTarget = targets.random()
    }

    private var transitionAnimatorSet: AnimatorSet? = null
    private var transitionXAnimator: ValueAnimator? = null
    private var transitionYAnimator: ValueAnimator? = null
    private fun setupTransitionAnimation() {
        setNextTarget()
        transitionXAnimator = ValueAnimator.ofInt(getThisParams().x, getNextTargetParams().x)
        transitionXAnimator!!.addUpdateListener {
            buttonMG!!.layoutParams = LayoutParams(
                getThisParams().width, getThisParams().height,
                (it.animatedValue as Int), getThisParams().y
            )
        }
        transitionYAnimator = ValueAnimator.ofInt(getThisParams().y, getNextTargetParams().y)
        transitionYAnimator!!.addUpdateListener {
            buttonMG!!.layoutParams = LayoutParams(
                getThisParams().width, getThisParams().height,
                getThisParams().x, (it.animatedValue as Int)
            )
            buttonMG!!.bringToFront()
        }
        transitionAnimatorSet = AnimatorSet()
        transitionAnimatorSet!!.play(transitionXAnimator!!).with(transitionYAnimator!!)
        transitionAnimatorSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        transitionAnimatorSet!!.duration = 1500
        transitionAnimatorSet!!.doOnEnd {
            setupTransitionAnimation()
            transitionAnimatorSet!!.start()
        }
    }

    private var fadeAnimator:ValueAnimator? = null
    private fun fade(out:Boolean) {
        if (fadeAnimator != null) {
            fadeAnimator!!.cancel()
        }
        fadeAnimator = if (out) {
            ValueAnimator.ofFloat(buttonMG!!.alpha, 0f)
        } else {
            ValueAnimator.ofFloat(buttonMG!!.alpha, 1f)
        }
        fadeAnimator!!.addUpdateListener {
            buttonMG!!.alpha = (it.animatedValue as Float)
        }
        fadeAnimator!!.duration = 1000
        fadeAnimator!!.start()
    }

    fun startSearchingAnimation() {
        fade(false)
        transitionAnimatorSet!!.start()
    }

    private fun getNextTargetParams(): LayoutParams {
        return targetParams!![nextTarget!!]!!
    }

    private fun getThisParams(): LayoutParams {
        return (buttonMG!!.layoutParams as LayoutParams)
    }

    fun setStyle() {

        fun lightDominant() {
            buttonMG!!.setBackgroundResource(R.drawable.lightmagnifying)
        }

        fun darkDominant() {
            buttonMG!!.setBackgroundResource(R.drawable.darkmagnifying)
        }

        if (MainActivity.isThemeDark) {
            darkDominant()
        } else {
            lightDominant()
        }

    }
}