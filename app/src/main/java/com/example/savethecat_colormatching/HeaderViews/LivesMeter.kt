package com.example.savethecat_colormatching.HeaderViews

import TransitionPackage
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageButton
import android.widget.TextView
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.Characters.CatButton
import com.example.savethecat_colormatching.CustomViews.CLabel
import com.example.savethecat_colormatching.CustomViews.CView
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R
import java.util.*

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

    /*
        Creates the image button with the heart
     */
    private fun setupImageButtonText() {
        livesCountLabel = CLabel(textView = TextView(meterView!!.context),
            parentLayout = MainActivity.rootLayout!!, params =  LayoutParams(
                getOriginalParams().width, getOriginalParams().height,
                getOriginalParams().x,
                (getOriginalParams().y + (getOriginalParams().height * 0.05).toInt())))
        livesCountLabel!!.setText(livesLeft.toString())
        livesCountLabel!!.setTextSize((getOriginalParams().height * 0.15).toFloat())
        livesCountLabel!!.getThis().setBackgroundColor(Color.TRANSPARENT)
        livesCountLabel!!.getThis().setTextColor(Color.WHITE)
    }

    /*
        Increments the left count of the meter
        and visually adds a heart to the lives meter
     */
    private var transitionPackages:MutableList<TransitionPackage> = mutableListOf()
    fun incrementLivesLeftCount(catButton: CatButton?, forOpponent:Boolean) {
        if (!forOpponent) {
            transitionPackages.add(TransitionPackage(spawnParams = LayoutParams(
                getOriginalParams().width, getOriginalParams().height,
                (catButton!!.getOriginalParams().x +
                        (catButton.getOriginalParams().width * 0.25)).toInt(),
                (catButton.getOriginalParams().y +
                        (catButton.getOriginalParams().height * 0.25)).toInt()),
                targetParams = getOriginalParams(), heartButton = buildHeartButton(),
                isOpponent = forOpponent))
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    MainActivity.staticSelf!!.runOnUiThread {
                        MainActivity.mpController!!.setMyLivesLeft(
                            MainActivity.myLivesMeter!!.getLivesLeftCount().toLong()
                        )
                    }
                }
            }, 2750)
        }
    }

    private fun setupHeartInteractiveButtons() {
        currentHeartButton = buildHeartButton()
    }

    fun getLivesLeftCount():Int {
        return livesLeft
    }

    /*
        Decreases the lives meter and removes a heart
     */
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
        setLivesLeftTextCount()
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

    /*
        Updates the transparency of the lives meter
     */
    private var fadeInAnimator: ValueAnimator? = null
    private var fadeAnimatorIsRunning:Boolean = false
    private fun fade(In:Boolean, Out:Boolean, Duration:Float, Delay:Float) {
        // If the fade animation is running, cancel it
        if (fadeInAnimator != null) {
            if (fadeAnimatorIsRunning) {
                fadeInAnimator!!.cancel()
                fadeAnimatorIsRunning = false
                fadeInAnimator = null
            }
        }
        // Make the lives meter appear or disappear
        if (In) {
            fadeInAnimator = ValueAnimator.ofFloat(0f, 1f)
        } else if (Out and !In) {
            fadeInAnimator = ValueAnimator.ofFloat(1f, 0f)
        }
        fadeInAnimator!!.addUpdateListener {
            currentHeartButton!!.alpha = it.animatedValue as Float
            if (MainActivity.dAspectRatio !in 1.5..1.7) {
                containerView!!.getThis().alpha = it.animatedValue as Float
            }
            livesCountLabel!!.getThis().alpha = it.animatedValue as Float
            meterView!!.alpha = it.animatedValue as Float
        }
        // Setup properties for the fade animation
        fadeInAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        fadeInAnimator!!.startDelay = (1000.0f * Delay).toLong()
        fadeInAnimator!!.duration = (1000.0f * Duration).toLong()
        fadeInAnimator!!.start()
        fadeAnimatorIsRunning = true
    }

    /*
        Creates the view that containes the hearts
     */
    private fun setupContainerMeterView() {
        containerView = CView(view = View(meterView!!.context), parentLayout = parentLayout!!,
            params = LayoutParams(((getOriginalParams().width * 2) - borderWidth),
                getOriginalParams().height, getOriginalParams().x - getOriginalParams().width +
                        borderWidth, getOriginalParams().y))
        containerView!!.setCornerRadiusAndBorderWidth(getOriginalParams().height / 2,
            getOriginalParams().height / 12)
        if (MainActivity.dAspectRatio in 1.5..1.7) {
            containerView!!.getThis().alpha = 0f
        }
    }

    /*
        Creates the original heart butto
     */
    private fun buildHeartButton(): ImageButton {
        val currentHeartButton = ImageButton(meterView!!.context)
        currentHeartButton.layoutParams = LayoutParams(getOriginalParams().width,
            getOriginalParams().height, getOriginalParams().x, getOriginalParams().y)
        currentHeartButton.setBackgroundResource(imageHeart)
        parentLayout!!.addView(currentHeartButton)
        return currentHeartButton
    }

    /*
        Draws the corners and border width of the lives meter
     */
    private var shape: GradientDrawable? = null
    private var borderWidth:Int = 0
    private var cornerRadius:Int = 0
    private fun setCornerRadiusAndBorderWidth(radius: Int, borderWidth: Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        // Draws the background of the lives meter
        if (MainActivity.isThemeDark) {
            shape!!.setColor(Color.BLACK)
        } else {
            shape!!.setColor(Color.WHITE)
        }
        // Draws the border of the meter
        if (borderWidth > 0) {
            this.borderWidth = borderWidth
            if (MainActivity.isThemeDark) {
                shape!!.setStroke(borderWidth, Color.WHITE)
            } else {
                shape!!.setStroke(borderWidth, Color.BLACK)
            }
        }
        // Draws the corner radius of the lives meter
        cornerRadius = radius
        shape!!.cornerRadius = radius.toFloat()
        meterView!!.setBackgroundDrawable(shape)
    }

    /*
        Translates the opponent's hearts from under neath the user's hearts
     */
    private var translateXY:ValueAnimator? = null
    fun translate(show:Boolean) {
        if (translateXY != null) {
            translateXY!!.cancel()
        }
        // Show or hide the opponent's hearts
        if (show) {
            if (MainActivity.dAspectRatio > 2.08 && MainActivity.dAspectRatio < 1.5) {
                translateXY = ValueAnimator.ofInt(
                    (meterView!!.layoutParams as LayoutParams).x, x - originalParams!!.height)
            } else {
                translateXY = ValueAnimator.ofInt(y, y + originalParams!!.height)
            }
        } else {
            if (MainActivity.dAspectRatio > 2.08 && MainActivity.dAspectRatio < 1.5) {
                translateXY = ValueAnimator.ofInt((
                        meterView!!.layoutParams as LayoutParams).x, x)
            } else {
                translateXY = ValueAnimator.ofInt(y + originalParams!!.height, y)
            }
        }
        // Update the x and y of the opponent's heart stack
        translateXY!!.addUpdateListener {
            val params:LayoutParams?
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
            livesCountLabel!!.getThis().layoutParams = LayoutParams(
                params.width, params.height,
                params.x, params.y + (getOriginalParams().height * 0.05).toInt())
            originalParams = params
        }
        translateXY!!.duration = 1000
        translateXY!!.start()
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


    /*
        Set the style of all the elements of the lives meter
     */
    fun setCompiledStyle() {
        containerView!!.setStyle()
        containerView!!.setCornerRadiusAndBorderWidth(getOriginalParams().height / 2,
            getOriginalParams().height / 12)
        setStyle()
    }

    private fun setStyle() {
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
        setCornerRadiusAndBorderWidth(radius = getOriginalParams().height / 2,
            borderWidth = getOriginalParams().height / 12)
    }
}