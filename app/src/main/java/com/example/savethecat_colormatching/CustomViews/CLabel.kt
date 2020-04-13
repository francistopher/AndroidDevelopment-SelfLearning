package com.example.savethecat_colormatching.CustomViews

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.GradientDrawable.RECTANGLE
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewPropertyAnimator
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.TextView
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.MainActivity

class CLabel(textView: TextView, parentLayout: AbsoluteLayout, params:LayoutParams) {

    var isInverted:Boolean = false

    private var originalParams:LayoutParams? = null
    private var shrunkParams:LayoutParams? = null

    private var textView:TextView? = null

    init {
        this.textView = textView
        this.textView!!.layoutParams = params
        parentLayout.addView(this.textView)
        setOriginalParams(params = params)
        setShrunkParams()
        setStyle()
    }

    fun getThis():TextView {
        return textView!!
    }

    private fun setOriginalParams(params:LayoutParams) {
        originalParams = params
    }

    private fun setShrunkParams() {
        shrunkParams = LayoutParams(originalParams!!.x / 2, originalParams!!.y / 2, 1, 1)
    }

    fun shrunk() {
        textView!!.layoutParams = shrunkParams!!
    }

    private var fadeAnimator:ViewPropertyAnimator? = null
    private var fadeAnimatorIsRunning:Boolean = false
    fun fade(In:Boolean, Out:Boolean, Duration:Float, Delay:Float) {
        if (fadeAnimator != null) {
            if (fadeAnimatorIsRunning) {
                fadeAnimator!!.cancel()
                fadeAnimatorIsRunning = false
                fadeAnimator = null
            }
        }
         if (In) {
             fadeAnimator = textView!!.animate().alpha(1.0f)
             fadeAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        }
        if (Out and !In) {
            fadeAnimator = textView!!.animate().alpha(0.0f)
            fadeAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        }
        fadeAnimator!!.startDelay = (1000.0f * Delay).toLong()
        fadeAnimator!!.duration = (1000.0f * Duration).toLong()
        fadeAnimator!!.withStartAction {
            fadeAnimatorIsRunning = true
        }
        fadeAnimator!!.withEndAction {
            if (In and Out) {
                this.fade(In = false, Out = true, Duration = Duration, Delay = 0.0f)
            } else {
                fadeAnimator!!.cancel()
                fadeAnimatorIsRunning = false
                fadeAnimator = null
            }
        }
        if (!fadeAnimatorIsRunning) {
            fadeAnimator!!.start()
        }
    }

    fun setText(text:String) {
        textView!!.text = text
        textView!!.typeface = Typeface.createFromAsset(
            MainActivity.rootView!!.context.assets,
            "SleepyFatCat.ttf"
        )
        textView!!.gravity = Gravity.CENTER
    }

    fun getOriginalParams():LayoutParams {
        return originalParams!!
    }

    fun setTextSize(size:Float) {
        textView!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    }

    private var shape:GradientDrawable? = null
    fun setCornerRadiusAndBorderWidth(radius:Int, borderWidth:Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = RECTANGLE
        shape!!.setColor((textView!!.background as ColorDrawable).color)
        if (borderWidth > 0) {
            shape!!.setStroke(borderWidth, Color.BLUE)
        }
        shape!!.cornerRadius = radius.toFloat()
        textView!!.setBackgroundDrawable(shape)
    }

    fun setStyle() {
        fun lightDominant() {
            textView!!.setBackgroundColor(Color.BLACK)
            textView!!.setTextColor(Color.WHITE)
        }

        fun darkDominant() {
            textView!!.setBackgroundColor(Color.WHITE)
            textView!!.setTextColor(Color.BLACK)
        }

        if (MainActivity.isThemeDark) {
            if (isInverted) {
                lightDominant()
            } else {
                darkDominant()
            }
        } else {
            if (isInverted) {
                darkDominant()
            } else {
                lightDominant()
            }
        }
    }

}