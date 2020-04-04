package com.example.savethecat_colormatching.CustomViews

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import androidx.core.animation.doOnStart
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.MainActivity

class CButton(button:Button, parentLayout:AbsoluteLayout, params:LayoutParams) {

    private var isInverted:Boolean = false

    private var originalParams:LayoutParams? = null
    private var shrunkParams:LayoutParams? = null

    private var button:Button? = null
    private var backgroundColor:Int? = null
    var shrinkType:ShrinkType = ShrinkType.mid

    init {
        this.button = button
        this.button!!.layoutParams = params
        parentLayout.addView(button)
        setOriginalParams(params)
        setShrunkParams()
        setStyle()
    }

    fun getThis():Button {
        return this.button!!
    }

    fun setTextSize(size:Float) {
        button!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    }

    fun setText(text:String, caps:Boolean) {
        button!!.isAllCaps = caps
        button!!.typeface = Typeface.createFromAsset(MainActivity.rootView!!.context.assets,
            "SleepyFatCat.ttf")
        button!!.text = text
    }

    private var shape: GradientDrawable? = null
    fun setCornerRadiusAndBorderWidth(radius:Int, borderWidth:Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        shape!!.setColor((button!!.background as ColorDrawable).color)
        if (borderWidth > 0) {
            if (MainActivity.isThemeDark) {
                shape!!.setStroke(borderWidth, Color.WHITE)
            } else {
                shape!!.setStroke(borderWidth, Color.BLACK)
            }

        }
        shape!!.cornerRadius = radius.toFloat()
        button!!.setBackgroundDrawable(shape)
    }

    private var growWidthAnimator:ValueAnimator? = null
    private var growWidthIsRunning:Boolean = false
    fun growWidth(width:Float) {
        if (growWidthAnimator != null) {
            if (growWidthIsRunning) {
                growWidthAnimator!!.cancel()
                growWidthIsRunning = false
                growWidthAnimator = null
            }
        }
        growWidthAnimator = ValueAnimator.ofFloat(originalParams!!.width.toFloat(), width)
        growWidthAnimator!!.addUpdateListener {
            originalParams = LayoutParams((it.animatedValue as Float).toInt(), getOriginalParams().
            height, getOriginalParams().x, getOriginalParams().y)
            button!!.layoutParams = originalParams!!
        }
        growWidthAnimator!!.duration = 1000
        growWidthAnimator!!.startDelay = 125
        growWidthIsRunning = true
        growWidthAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        growWidthAnimator!!.start()
    }

    private var x:Float = 0f
    private var duration:Float = 0f
    private var translateXAnimator:ValueAnimator? = null
    private var shrinkWidthAnimator:ValueAnimator? = null
    private var shrinkAnimationSet:AnimatorSet? = null
    private var shrinkAnimationSetIsRunning:Boolean = false
    fun shrink(isColorOptionButton:Boolean) {
        if (shrinkAnimationSet != null) {
            if (shrinkAnimationSetIsRunning) {
                shrinkAnimationSet!!.cancel()
                shrinkAnimationSetIsRunning = false
                shrinkAnimationSet = null
            }
        }
        when (shrinkType) {
            ShrinkType.left -> {
                x = (originalParams!!.x).toFloat()
                duration = 750f
            }
            ShrinkType.mid -> {
                x = (originalParams!!.x + originalParams!!.width * 0.5).toFloat()
                duration = 500f
            }
            ShrinkType.right -> {
                x = (originalParams!!.x + originalParams!!.width).toFloat()
                duration = 750f
            }
        }

        translateXAnimator = ValueAnimator.ofFloat((originalParams!!.x).toFloat(), x)
        translateXAnimator!!.addUpdateListener {
            originalParams = LayoutParams(originalParams!!.width, originalParams!!.
            height, (it.animatedValue as Float).toInt(), originalParams!!.y)
            button!!.layoutParams = originalParams!!
        }

        shrinkWidthAnimator = ValueAnimator.ofFloat((originalParams!!.width).toFloat(), 0f)
        shrinkWidthAnimator!!.addUpdateListener {
            originalParams = LayoutParams((it.animatedValue as Float).toInt(), originalParams!!.
            height, originalParams!!.x, originalParams!!.y)
            button!!.layoutParams = originalParams
        }

        shrinkAnimationSet = AnimatorSet()
        shrinkAnimationSet!!.play(translateXAnimator!!).with(shrinkWidthAnimator!!)
        shrinkAnimationSet!!.duration = duration.toLong()
        shrinkAnimationSet!!.start()
    }

    fun setOriginalParams(params:LayoutParams) {
        originalParams = params
    }

    fun getOriginalParams():LayoutParams {
        return originalParams!!
    }

    private fun setShrunkParams() {
        shrunkParams = LayoutParams(originalParams!!.x / 2, originalParams!!.y / 2, 1, 1)
    }

    fun setStyle() {
        fun lightDominant() {
            if (backgroundColor == null) {
                button!!.setBackgroundColor(Color.BLACK)
            } else {
                button!!.setBackgroundColor(backgroundColor!!)
                button!!.setBackgroundResource(backgroundColor!!)
            }
            button!!.setTextColor(Color.WHITE)
        }

        fun darkDominant() {
            if (backgroundColor == null) {
                button!!.setBackgroundColor(Color.WHITE)
            } else {
                button!!.setBackgroundColor(backgroundColor!!)
                button!!.setBackgroundResource(backgroundColor!!)
            }
            button!!.setTextColor(Color.BLACK)
        }
        if (MainActivity.isThemeDark) {
            if (isInverted) {
                darkDominant()
            } else {
                lightDominant()
            }
        } else {
            if (isInverted) {
                lightDominant()
            } else {
                darkDominant()
            }
        }
    }
}
