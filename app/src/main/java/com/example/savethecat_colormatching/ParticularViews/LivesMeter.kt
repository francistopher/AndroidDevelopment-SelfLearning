package com.example.savethecat_colormatching.ParticularViews

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.animation.doOnEnd
import com.example.savethecat_colormatching.Characters.CatButton
import com.example.savethecat_colormatching.CustomViews.CLabel
import com.example.savethecat_colormatching.CustomViews.CView
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R

class LivesMeter(meterView: View,
                 parentLayout: AbsoluteLayout,
                 params: LayoutParams,
                 isOpponent:Boolean) {

    private var originalParams:LayoutParams? = null
    private var meterContext: Context? = null
    private var meterLayout:AbsoluteLayout? = null
    private var meterView:View? = null

    private var imageHeart:Int = 0

    private var livesLeft:Int = 1

    private var currentHeartButton:ImageButton? = null
    private var livesCountLabel:CLabel? = null
    private var parentLayout:AbsoluteLayout? = null

    private var containerView:CView? = null

    init {
        if (isOpponent) {
            imageHeart = R.drawable.opponentheart
        } else {
            imageHeart = R.drawable.heart
        }
        this.meterView = meterView
        meterContext = meterView.context
        meterLayout = AbsoluteLayout(meterContext)
        setOriginalParams(params = params)
        this.meterView!!.layoutParams = params
        // Set border width and corner radius
        setStyle()
        setCornerRadiusAndBorderWidth(radius = params.height / 2,
            borderWidth = params.height / 12)
        this.parentLayout = parentLayout
        setupContainerMeterView()
        parentLayout.addView(this.meterView!!)
        // Setup heart interactive buttons
        setupHeartInteractiveButtons()
        setupImageButtonText()
    }

    fun hideCurrentHeartButton() {
        currentHeartButton!!.alpha = 0f
        livesCountLabel!!.getThis().alpha = 0f
    }

    fun showCurrentHeartButton() {
        currentHeartButton!!.alpha = 1f
        livesCountLabel!!.getThis().alpha = 1f
    }

    private fun setupImageButtonText() {
        livesCountLabel = CLabel(textView = TextView(meterView!!.context),
            parentLayout = MainActivity.rootLayout!!, params =  LayoutParams(getOriginalParams().width,
                getOriginalParams().height, getOriginalParams().x,
                (getOriginalParams().y + (getOriginalParams().height * 0.05).toInt())))
        livesCountLabel!!.setText(livesLeft.toString())
        livesCountLabel!!.setTextSize((getOriginalParams().height * 0.15).toFloat())
        livesCountLabel!!.getThis().setBackgroundColor(Color.TRANSPARENT)
    }

    private var transitionPackages:MutableList<TransitionPackage> = mutableListOf()
    fun incrementLivesLeftCount(catButton: CatButton, forOpponent:Boolean) {
        transitionPackages.add(TransitionPackage(spawnParams = LayoutParams(getOriginalParams().width,
            getOriginalParams().height, (catButton.getOriginalParams().x +
                    (catButton.getOriginalParams().width * 0.25)).toInt(),
            (catButton.getOriginalParams().y +
                    (catButton.getOriginalParams().height * 0.25)).toInt()),
            targetParams = getOriginalParams(), heartButton = buildHeartButton()))
    }

    private fun setupHeartInteractiveButtons() {
        currentHeartButton = buildHeartButton()
    }

    fun getLivesLeftCount():Int {
        return livesLeft
    }

    fun dropLivesLeftHeart() {
        livesLeft -= 1
        if (transitionPackages.size > 0) {
            transitionPackages[0].drop()
            transitionPackages.remove(transitionPackages[0])
        }
        setLivesLeftTextCount()
    }

    fun incrementLivesLeftCount() {
        livesLeft += 1
    }

    fun resetLivesLeftCount() {
        livesLeft = 1
        setLivesLeftTextCount()
    }

    fun setLivesLeftTextCount() {
        livesCountLabel!!.setText(livesLeft.toString())
        livesCountLabel!!.getThis().bringToFront()
    }

    private fun setupContainerMeterView() {
        containerView = CView(view = View(meterView!!.context), parentLayout = parentLayout!!,
            params = LayoutParams(((getOriginalParams().width * 2) - borderWidth),
                getOriginalParams().height, getOriginalParams().x - getOriginalParams().width +
                        borderWidth, getOriginalParams().y))
        containerView!!.setCornerRadiusAndBorderWidth(getOriginalParams().height / 2,
            getOriginalParams().height / 12)
    }

    private fun buildHeartButton(): ImageButton {
        val currentHeartButton = ImageButton(meterView!!.context)
        currentHeartButton.layoutParams = LayoutParams(getOriginalParams().width,
            getOriginalParams().height, getOriginalParams().x, getOriginalParams().y)
        currentHeartButton.setBackgroundResource(imageHeart)
        parentLayout!!.addView(currentHeartButton)
        return currentHeartButton
    }

    private var shape: GradientDrawable? = null
    private var borderWidth:Int = 0
    private var cornerRadius:Int = 0
    private fun setCornerRadiusAndBorderWidth(radius: Int, borderWidth: Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        if (MainActivity.isThemeDark) {
            shape!!.setColor(Color.BLACK)
        } else {
            shape!!.setColor(Color.WHITE)
        }
        if (borderWidth > 0) {
            this.borderWidth = borderWidth
            if (MainActivity.isThemeDark) {
                shape!!.setStroke(borderWidth, Color.WHITE)
            } else {
                shape!!.setStroke(borderWidth, Color.BLACK)
            }
        }
        cornerRadius = radius
        shape!!.cornerRadius = radius.toFloat()
        meterView!!.setBackgroundDrawable(shape)
    }

    fun setOriginalParams(params: LayoutParams) {
        originalParams = params
    }

    fun getOriginalParams():LayoutParams {
        return originalParams!!
    }

    fun getThis():View {
        return this.meterView!!
    }

    private fun lightDominant() {
        meterView!!.setBackgroundColor(Color.BLACK)
    }

    private fun darkDominant() {
        meterView!!.setBackgroundColor(Color.WHITE)
    }

    fun setStyle() {
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
    }
}

class TransitionPackage(spawnParams:LayoutParams,
                        targetParams:LayoutParams,
                        heartButton:ImageButton) {

    private var targetParams:LayoutParams? = null
    private var spawnParams:LayoutParams? = null
    private var heartButton:ImageButton? = null
    private var animatorSet:AnimatorSet? = null
    private var xAnimation:ValueAnimator? = null
    private var yAnimation:ValueAnimator? = null
    private var transitionedToBase:Boolean = false
    private var targetX:Int = 0
    private var targetY:Int = 0

    init {
        this.targetParams = targetParams
        this.spawnParams = spawnParams
        this.heartButton = heartButton
        heartButton.bringToFront()
        setupAnimationX()
        setupAnimationY()
        setupAnimationSet()
    }

    private fun setupAnimationX() {
        targetX = spawnParams!!.x
        xAnimation = ValueAnimator.ofInt(spawnParams!!.x , targetParams!!.x)
        xAnimation!!.addUpdateListener {
            targetX = (it.animatedValue as Int)
            heartButton!!.layoutParams = LayoutParams(targetParams!!.width, targetParams!!.height,
                targetX, targetY)
        }
    }

    private fun setupAnimationY() {
        targetY = spawnParams!!.y
        yAnimation = ValueAnimator.ofInt(spawnParams!!.y, targetParams!!.y)
        yAnimation!!.addUpdateListener {
            targetY = (it.animatedValue as Int)
            heartButton!!.layoutParams = LayoutParams(targetParams!!.width, targetParams!!.height,
                targetX, targetY)
        }
    }

    private fun setupAnimationSet() {
        animatorSet = AnimatorSet()
        animatorSet!!.play(xAnimation!!).with(yAnimation!!)
        animatorSet!!.startDelay = 125
        animatorSet!!.duration = 2500
        animatorSet!!.start()
        animatorSet!!.doOnEnd {
            if (!transitionedToBase) {
                transitionedToBase = true
                MainActivity.myLivesMeter!!.incrementLivesLeftCount()
                MainActivity.myLivesMeter!!.setLivesLeftTextCount()
            } else {
                MainActivity.rootLayout!!.removeView(heartButton!!)
            }
        }
    }

    fun drop() {
        if (transitionedToBase) {
            spawnParams = heartButton!!.layoutParams as LayoutParams
            targetParams = LayoutParams(targetParams!!.width, targetParams!!.height,
                targetParams!!.x, (MainActivity.dUnitHeight * 16).toInt())
            heartButton!!.bringToFront()
            setupAnimationX()
            setupAnimationY()
            setupAnimationSet()
        } else {
            MainActivity.rootLayout!!.removeView(heartButton!!)
        }
    }
}