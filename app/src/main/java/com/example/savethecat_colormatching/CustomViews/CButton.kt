package com.example.savethecat_colormatching.CustomViews

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.util.TypedValue
import android.view.ViewPropertyAnimator
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.MainActivity


class CButton(button: Button, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var isInverted: Boolean = false

    private var originalParams: LayoutParams? = null
    private var shrunkParams: LayoutParams? = null

    private var button: Button? = null
    var backgroundColor: Int? = null
    var targetBackgroundColor: Int? = null
    var shrinkType: ShrinkType = ShrinkType.mid
    var isColorOptionButton:Boolean = true

    init {
        this.button = button
        this.button!!.layoutParams = params
        parentLayout.addView(button)
        setOriginalParams(params)
        setShrunkParams()
        setStyle()
    }



    fun getThis(): Button {
        return this.button!!
    }

    fun setTextSize(size: Float) {
        button!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    }

    fun setText(text: String, caps: Boolean) {
        button!!.isAllCaps = caps
        button!!.typeface = Typeface.createFromAsset(
            MainActivity.rootView!!.context.assets,
            "SleepyFatCat.ttf"
        )
        button!!.text = text
    }

    private var shape: GradientDrawable? = null
    private var borderWidth:Int = 0
    private var cornerRadius:Int = 0
    fun setCornerRadiusAndBorderWidth(radius: Int, borderWidth: Int) {
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
        cornerRadius = radius
        shape!!.cornerRadius = radius.toFloat()
        button!!.setBackgroundDrawable(shape)
    }

    private var growWidthAndChangeColor: AnimatorSet? = null
    private var growWidthAnimator: ValueAnimator? = null
    private var changeColorAnimator: ValueAnimator? = null
    private var fadeOutAnimator:ValueAnimator? = null
    var growWidthAndChangeColorIsRunning: Boolean = false
    fun growWidth(width: Float) {
        if (growWidthAndChangeColor != null) {
            if (growWidthAndChangeColorIsRunning) {
                growWidthAndChangeColor!!.cancel()
                growWidthAndChangeColorIsRunning = false
                growWidthAndChangeColor = null
            }
        }
        growWidthAnimator = ValueAnimator.ofFloat(originalParams!!.width.toFloat(), width)
        growWidthAnimator!!.addUpdateListener {
            originalParams = LayoutParams(
                (it.animatedValue as Float).toInt(),
                getOriginalParams().height,
                getOriginalParams().x,
                getOriginalParams().y
            )
            button!!.layoutParams = originalParams!!
        }
        changeColorAnimator = ValueAnimator.ofArgb(getBackgroundColor(), targetBackgroundColor!!)
        changeColorAnimator!!.addUpdateListener {
            button!!.setBackgroundColor(it.animatedValue as Int)
            setCornerRadiusAndBorderWidth((originalParams!!.height / 5.0).toInt(), borderWidth)
        }
        growWidthAndChangeColor = AnimatorSet()
        growWidthAndChangeColor!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        growWidthAndChangeColor!!.play(growWidthAnimator!!).with(changeColorAnimator!!)
        growWidthAndChangeColor!!.duration = 1000
        growWidthAndChangeColor!!.startDelay = 125
        growWidthAndChangeColorIsRunning = true
        growWidthAndChangeColor!!.start()

        fadeOutAnimator = ValueAnimator.ofFloat(1f, 0f)
        fadeOutAnimator!!.addUpdateListener{
            button!!.alpha = it.animatedValue as Float
        }
        fadeOutAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        fadeOutAnimator!!.startDelay = 1250
        fadeOutAnimator!!.duration = 750
        fadeOutAnimator!!.start()
    }

    private var x: Float = 0f
    private var duration: Float = 0f
    private var translateXAnimator: ValueAnimator? = null
    private var shrinkWidthAnimator: ValueAnimator? = null
    private var shrinkAnimationSet: AnimatorSet? = null
    private var shrinkAnimationSetIsRunning: Boolean = false
    fun shrink(isColorOptionButton: Boolean) {
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
            originalParams = LayoutParams(
                originalParams!!.width,
                originalParams!!.height,
                (it.animatedValue as Float).toInt(),
                originalParams!!.y
            )
            button!!.layoutParams = originalParams!!
        }

        shrinkWidthAnimator = ValueAnimator.ofFloat((originalParams!!.width).toFloat(), 0f)
        shrinkWidthAnimator!!.addUpdateListener {
            originalParams = LayoutParams(
                (it.animatedValue as Float).toInt(),
                originalParams!!.height,
                originalParams!!.x,
                originalParams!!.y
            )
            button!!.layoutParams = originalParams
        }

        shrinkAnimationSet = AnimatorSet()
        shrinkAnimationSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        shrinkAnimationSet!!.play(translateXAnimator!!).with(shrinkWidthAnimator)
        shrinkAnimationSet!!.duration = duration.toLong()
        shrinkAnimationSet!!.start()
    }

    private var fadeAnimator: ViewPropertyAnimator? = null
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
            fadeAnimator = button!!.animate().alpha(1.0f)
            fadeAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        }
        if (Out and !In) {
            fadeAnimator = button!!.animate().alpha(0.0f)
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
            Log.i("Animation", "Fade")
            fadeAnimator!!.start()
        }
    }

    private fun getBackgroundColor(): Int {
        if (backgroundColor != null) {
            return backgroundColor!!
        } else {
            return if (MainActivity.isThemeDark) {
                if (isInverted) {
                    Color.WHITE
                } else {
                    Color.BLACK
                }
            } else {
                if (isInverted) {
                    Color.BLACK
                } else {
                    Color.WHITE
                }
            }

        }
    }

    fun setOriginalParams(params: LayoutParams) {
        originalParams = params
    }

    fun getOriginalParams(): LayoutParams {
        return originalParams!!
    }

    private fun setShrunkParams() {
        shrunkParams = LayoutParams(originalParams!!.x / 2, originalParams!!.y / 2, 1, 1)
    }

    private fun lightDominant() {
        if (backgroundColor == null) {
            button!!.setBackgroundColor(Color.BLACK)
        } else {
            button!!.setBackgroundColor(backgroundColor!!)
        }
        button!!.setTextColor(Color.WHITE)
    }

    private fun darkDominant() {
        if (backgroundColor == null) {
            button!!.setBackgroundColor(Color.WHITE)
        } else {
            button!!.setBackgroundColor(backgroundColor!!)
        }
        button!!.setTextColor(Color.BLACK)
    }

    fun setStyle() {
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
//        setCornerRadiusAndBorderWidth(cornerRadius, borderWidth)
    }
}
