package com.example.savethecat_colormatching.Characters

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.MainActivity

class CatButton(button: Button, parentLayout: AbsoluteLayout, params: AbsoluteLayout.LayoutParams, backgroundColor:Int) {

    private var originalParams: LayoutParams? = null
    private var shrunkParams: LayoutParams? = null

    private var button: Button? = null
    private var originalBackgroundColor:Int = 0

    init {
        this.button = button
        this.button!!.layoutParams = params
        this.originalBackgroundColor = backgroundColor
        parentLayout.addView(button)
        setOriginalParams(params=params)
        setShrunkParams()
        this.button!!.setBackgroundColor(backgroundColor)
    }

    private var shape: GradientDrawable? = null
    private var borderWidth:Int = 0
    fun setCornerRadiusAndBorderWidth(radius:Int, borderWidth:Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        shape!!.setColor((button!!.background as ColorDrawable).color)
        if (borderWidth > 0) {
            this.borderWidth = borderWidth
            if (MainActivity.isThemeDark) {
                shape!!.setStroke(borderWidth, Color.WHITE)

            } else {
                shape!!.setStroke(borderWidth, Color.BLACK)
            }
        }
        shape!!.cornerRadius = radius.toFloat()
        button!!.setBackgroundDrawable(shape)
    }

    private var transitionColorAnimator: ValueAnimator? = null
    private var isTransitioningColor:Boolean = false
    fun transitionColor(targetColor:Int) {
        if (transitionColorAnimator != null) {
            if (isTransitioningColor) {
                transitionColorAnimator!!.cancel()
                isTransitioningColor = false
                transitionColorAnimator = null
            }
        }
        transitionColorAnimator = ValueAnimator.ofArgb(originalBackgroundColor, targetColor)
        transitionColorAnimator!!.addUpdateListener {
            button!!.setBackgroundColor(it.animatedValue as Int)
            setCornerRadiusAndBorderWidth((originalParams!!.height / 5.0).toInt(), borderWidth)
        }
        transitionColorAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        transitionColorAnimator!!.startDelay = 125
        transitionColorAnimator!!.duration = 500
        isTransitioningColor = true
        transitionColorAnimator!!.start()
    }

    private fun setOriginalParams(params:LayoutParams) {
        originalParams = params
    }

    private fun setShrunkParams() {
        shrunkParams = LayoutParams(originalParams!!.x / 2, originalParams!!.y / 2, 1, 1)
    }

    fun getOriginalBackgroundColor():Int {
        return originalBackgroundColor
    }

    fun getThis():Button {
        return button!!
    }

    fun setStyle() {
        if (MainActivity.isThemeDark) {
            button!!.setBackgroundColor(Color.WHITE)
        } else {
            button!!.setBackgroundColor(Color.BLACK)
        }
    }
}