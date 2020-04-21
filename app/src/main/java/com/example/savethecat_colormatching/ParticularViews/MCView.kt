package com.example.savethecat_colormatching.ParticularViews

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.TextView
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.MainActivity
import kotlin.math.abs

class MCView(textView: TextView, parentLayout: AbsoluteLayout,
             params: LayoutParams) {

    private var originalParams:LayoutParams? = null
    private var mouseCoinView:TextView? = null

    private var parentLayout:AbsoluteLayout? = null

    companion object {
        var mouseCoinCount:Int = 0
        var neededMouseCoinCount:Int = 0
    }

    init {
        this.mouseCoinView = textView
        this.mouseCoinView!!.setBackgroundColor(Color.TRANSPARENT)
        setupOriginalParams(params = params)
        setupLayout(layout = parentLayout)
        setCornerRadiusAndBorderWidth((params.height / 2.0).toInt(),
            (params.height / 12.0).toInt())
        mouseCoinView!!.alpha = 0f
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
            mouseCoinView!!.alpha = it.animatedValue as Float
        }
        fadeInAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        fadeInAnimator!!.startDelay = (1000.0f * Delay).toLong()
        fadeInAnimator!!.duration = (1000.0f * Duration).toLong()
        fadeInAnimator!!.start()
        fadeAnimatorIsRunning = true
    }

    private var shape: GradientDrawable? = null
    private var borderWidth:Int = 0
    private var cornerRadius:Int = 0
    private fun setCornerRadiusAndBorderWidth(radius: Int, borderWidth: Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        shape!!.setColor(Color.TRANSPARENT)
        if (borderWidth > 0) {
            this.borderWidth = borderWidth
            shape!!.setStroke(borderWidth, Color.parseColor("#ffd60a"))
        }
        cornerRadius = radius
        shape!!.cornerRadius = radius.toFloat()
        mouseCoinView!!.setBackgroundDrawable(shape)
    }

    private var mouseCoinValueAnimator:ValueAnimator? = null

    fun startMouseCoinCount(startingCount:Int) {
        mouseCoinCount = 0
        updateCount(startingCount)
    }

    fun submitMouseCoinCount() {
        MainActivity.gameSPEditor!!.putInt("mouseCoins", mouseCoinCount)
        if (!MainActivity.gameSPEditor!!.commit()) {
            MainActivity.gameNotification!!.displayFirebaseTrouble()
        }
    }

    fun updateCount(difference:Int) {
        if (mouseCoinCount + difference < 0) {
            return
        }
        if (abs(difference) == 1) {
            mouseCoinCount += difference
            setText(mouseCoinCount.toString())
            return
        }

        if (mouseCoinValueAnimator != null) {
            mouseCoinValueAnimator!!.cancel()
        }
        mouseCoinValueAnimator = ValueAnimator.ofInt(mouseCoinCount, mouseCoinCount + difference)
        mouseCoinValueAnimator!!.addUpdateListener {
            setText((it.animatedValue as Int).toString())
        }
        mouseCoinCount += difference
        mouseCoinValueAnimator!!.duration = 1000
        mouseCoinValueAnimator!!.start()
    }

    private fun setText(text:String) {
        mouseCoinView!!.text = text
        mouseCoinView!!.typeface = Typeface.createFromAsset(
            MainActivity.rootView!!.context.assets,
            "SleepyFatCat.ttf"
        )
        mouseCoinView!!.gravity = Gravity.CENTER
    }

    fun getThis():TextView {
        return mouseCoinView!!
    }

    fun setTextSize(size:Float) {
        mouseCoinView!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
        mouseCoinView!!.setTextColor(Color.parseColor("#ffd60a"))
    }

    private fun setupOriginalParams(params:LayoutParams) {
        this.mouseCoinView!!.layoutParams = params
        this.originalParams = params
    }

    fun getOriginalParams():LayoutParams {
        return originalParams!!
    }

    private fun setupLayout(layout:AbsoluteLayout) {
        layout.addView(this.mouseCoinView!!)
        this.parentLayout = layout
    }
}