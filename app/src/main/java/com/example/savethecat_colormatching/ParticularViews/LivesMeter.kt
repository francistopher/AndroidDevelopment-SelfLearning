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
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
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
    private var y:Int = 0
    private var x:Int = 0
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
        this.meterView!!.alpha = 0f
        currentHeartButton!!.alpha = 0f
        containerView!!.getThis().alpha = 0f
        livesCountLabel!!.getThis().alpha = 0f
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
        livesCountLabel!!.getThis().setTextColor(Color.WHITE)
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

    fun fadeIn() {
        fade(true, false, 1f, 0.125f)
    }

    private var fadeInAnimator: ValueAnimator? = null
    private var fadeAnimatorIsRunning:Boolean = false
    private fun fade(In:Boolean, Out:Boolean, Duration:Float, Delay:Float) {
        if (fadeInAnimator != null) {
            if (fadeAnimatorIsRunning) {
                fadeInAnimator!!.cancel()
                fadeAnimatorIsRunning = false
                fadeInAnimator = null
            }
        }
        if (In) {
            fadeInAnimator = ValueAnimator.ofFloat(0f, 1f)
        } else if (Out and !In) {
            fadeInAnimator = ValueAnimator.ofFloat(1f, 0f)
        }
        fadeInAnimator!!.addUpdateListener {
            currentHeartButton!!.alpha = it.animatedValue as Float
            containerView!!.getThis().alpha = it.animatedValue as Float
            livesCountLabel!!.getThis().alpha = it.animatedValue as Float
            meterView!!.alpha = it.animatedValue as Float
        }
        fadeInAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        fadeInAnimator!!.startDelay = (1000.0f * Delay).toLong()
        fadeInAnimator!!.duration = (1000.0f * Duration).toLong()
        fadeInAnimator!!.start()
        fadeAnimatorIsRunning = true
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

    private var transformYAnimator:ValueAnimator? = null
    fun translate(show:Boolean) {
        if (transformYAnimator != null) {
            transformYAnimator!!.cancel()
        }
        if (show) {
            if (MainActivity.dAspectRatio > 2.08) {
                transformYAnimator = ValueAnimator.ofInt(
                    (meterView!!.layoutParams as LayoutParams).x, x - originalParams!!.height)
            } else {
                transformYAnimator = ValueAnimator.ofInt(y, y + originalParams!!.height)
            }
        } else {
            if (MainActivity.dAspectRatio > 2.08) {
                transformYAnimator = ValueAnimator.ofInt((
                        meterView!!.layoutParams as LayoutParams).x, x)
            } else {
                transformYAnimator = ValueAnimator.ofInt(y + originalParams!!.height, y)
            }
        }
        transformYAnimator!!.addUpdateListener {
            var params:LayoutParams?
            if (MainActivity.dAspectRatio > 2.08) {
                params = LayoutParams(
                    originalParams!!.width,
                    originalParams!!.height,
                    (it.animatedValue as Int),
                    originalParams!!.y)

            } else {
                params = LayoutParams(
                    originalParams!!.width,
                    originalParams!!.height,
                    originalParams!!.x,
                    (it.animatedValue as Int))
            }
            meterView!!.layoutParams = params
            currentHeartButton!!.layoutParams = params
            livesCountLabel!!.getThis().layoutParams = LayoutParams(params.width,
                params.height, params.x, params.y + (getOriginalParams().height * 0.05).toInt())
        }
        transformYAnimator!!.duration = 1000
        transformYAnimator!!.start()
    }

    fun setOriginalParams(params: LayoutParams) {
        x = params.x
        y = params.y
        originalParams = params
    }

    fun getOriginalParams():LayoutParams {
        return originalParams!!
    }

    fun getThis():View {
        return this.meterView!!
    }

    fun getCircularView():View {
        return meterView!!
    }

    fun getCountView():TextView {
        return livesCountLabel!!.getThis()
    }

    fun getHeartView():ImageButton {
        return currentHeartButton!!
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