package com.example.savethecat_colormatching.ParticularViews

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.widget.AbsoluteLayout
import android.widget.Button
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R


class SearchMG(button: Button,
               parentLayout: AbsoluteLayout,
               params: AbsoluteLayout.LayoutParams,
               topLeftCorner:Pair<Int, Int>,
               bottomRightCorner:Pair<Int, Int>) {

    private var buttonMG: Button? = null
    private var textButton: Button? = null
    private var searchContext: Context? = null
    private var originalParams: AbsoluteLayout.LayoutParams? = null
    private var parentLayout: AbsoluteLayout? = null
    private var targetParams: MutableMap<MGPosition, AbsoluteLayout.LayoutParams>? = mutableMapOf()
    private var nextTarget: MGPosition? = null
    private var previousTarget: MGPosition? = null

    init {
        setupMGButton(button)
        setupOriginalParams(params)
        setupParentLayout(parentLayout)
        setupTextButton()
        setupTargetParams(topLeftCorner, bottomRightCorner)
        setupRotationAnimation()
        setupTransitionAnimation()
        setStyle()
    }

    private fun setupTargetParams(topLeft: Pair<Int, Int>, bottomRight: Pair<Int, Int>) {
        targetParams!![MGPosition.TOPLEFT] = AbsoluteLayout.LayoutParams(
            originalParams!!.width, originalParams!!.height,
            topLeft.first + (originalParams!!.width * 0.15).toInt(),
            topLeft.second + (originalParams!!.height * 0.15).toInt()
        )
        targetParams!![MGPosition.TOPRIGHT] = AbsoluteLayout.LayoutParams(
            originalParams!!.width, originalParams!!.height,
            bottomRight.first - (originalParams!!.width * 1.15).toInt(),
            topLeft.second + (originalParams!!.height * 0.15).toInt()
        )
        targetParams!![MGPosition.BOTTOMLEFT] = AbsoluteLayout.LayoutParams(
            originalParams!!.width, originalParams!!.height,
            topLeft.first + (originalParams!!.width * 0.15).toInt(),
            bottomRight.second - (originalParams!!.height * 1.15).toInt()
        )
        targetParams!![MGPosition.BOTTOMRIGHT] = AbsoluteLayout.LayoutParams(
            originalParams!!.width, originalParams!!.height,
            bottomRight.first - (originalParams!!.width * 1.15).toInt(),
            bottomRight.second - (originalParams!!.height * 1.15).toInt()
        )
    }

    private fun setupMGButton(button: Button) {
        button.alpha = 0f
        buttonMG = button
        searchContext = button.context
    }

    private fun setupTextButton() {
        textButton = Button(searchContext)
        textButton!!.alpha = 0f
        textButton!!.layoutParams = originalParams!!
        parentLayout!!.addView(textButton!!)
    }

    private fun setupOriginalParams(params: AbsoluteLayout.LayoutParams) {
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
            val lp = AbsoluteLayout.LayoutParams(
                getThisParams().width, getThisParams().height,
                (it.animatedValue as Int), getThisParams().y
            )
            buttonMG!!.layoutParams = lp
            textButton!!.layoutParams = lp
        }
        transitionYAnimator = ValueAnimator.ofInt(getThisParams().y, getNextTargetParams().y)
        transitionYAnimator!!.addUpdateListener {
            val lp = AbsoluteLayout.LayoutParams(
                getThisParams().width, getThisParams().height,
                getThisParams().x, (it.animatedValue as Int)
            )
            buttonMG!!.layoutParams = lp
            textButton!!.layoutParams = lp
            buttonMG!!.bringToFront()
        }
        transitionAnimatorSet = AnimatorSet()
        transitionAnimatorSet!!.play(transitionXAnimator!!).with(transitionYAnimator!!)
        transitionAnimatorSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        transitionAnimatorSet!!.duration = 1500
        transitionAnimatorSet!!.doOnEnd {
            if (!stoppingAnimation) {
                setupTransitionAnimation()
                transitionAnimatorSet!!.start()
            } else {
                buttonMG!!.layoutParams = originalParams!!
                textButton!!.layoutParams = originalParams!!
                stoppingAnimation = false
            }
        }
    }

    private var stoppingAnimation:Boolean = false
    fun stopAnimation() {
        stoppingAnimation = true
        fade(true)
    }

    private var rotationAnimation: ValueAnimator? = null
    private fun setupRotationAnimation() {
        if (rotationAnimation != null) {
            rotationAnimation!!.cancel()
        }
        rotationAnimation = ValueAnimator.ofFloat(textButton!!.rotation, textButton!!.rotation + 90f)
        rotationAnimation!!.addUpdateListener {
            textButton!!.rotation = (it.animatedValue as Float)
        }
        rotationAnimation!!.interpolator = EasingInterpolator(Ease.LINEAR)
        rotationAnimation!!.startDelay = 0
        rotationAnimation!!.duration = 1000
        rotationAnimation!!.doOnEnd {
            setupRotationAnimation()
            rotationAnimation!!.start()
        }
    }

    private var fadeAnimator: ValueAnimator? = null
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
            textButton!!.alpha = (it.animatedValue as Float)
        }
        fadeAnimator!!.duration = 1000
        fadeAnimator!!.start()
        fadeAnimator!!.doOnEnd {
            if (stoppingAnimation && out) {
                if (transitionAnimatorSet != null) {
                    transitionAnimatorSet!!.cancel()
                }
            }
        }
    }

    fun startSearchingAnimation() {
        if (MainActivity.isGooglePlayGameServicesAvailable && MainActivity.isInternetReachable) {
            if (transitionAnimatorSet!!.isRunning) {
                return
            }
            fade(false)
            transitionAnimatorSet!!.start()
            rotationAnimation!!.start()
        } else {
            displayFailureReason()
        }
    }

    private fun displayFailureReason() {
        if (!MainActivity.isInternetReachable) {
            MainActivity.gameNotification!!.displayNoInternet()
        }
        if (!MainActivity.isGooglePlayGameServicesAvailable) {
            MainActivity.gameNotification!!.displayNoGooglePlayGameServices()
        }
    }

    private fun getNextTargetParams(): AbsoluteLayout.LayoutParams {
        return targetParams!![nextTarget!!]!!
    }

    private fun getThisParams(): AbsoluteLayout.LayoutParams {
        return (buttonMG!!.layoutParams as AbsoluteLayout.LayoutParams)
    }

    fun setStyle() {
        fun lightDominant() {
            buttonMG!!.setBackgroundResource(R.drawable.lightmagnify)
            textButton!!.setBackgroundResource(R.drawable.darksearchingtext)
        }
        fun darkDominant() {
            buttonMG!!.setBackgroundResource(R.drawable.darkmagnify)
            textButton!!.setBackgroundResource(R.drawable.lightsearchingtext)
        }
        if (MainActivity.isThemeDark) {
            darkDominant()
        } else {
            lightDominant()
        }
    }
}