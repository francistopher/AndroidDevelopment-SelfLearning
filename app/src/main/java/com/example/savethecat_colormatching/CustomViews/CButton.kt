package com.example.savethecat_colormatching.CustomViews

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.ViewPropertyAnimator
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.MainActivity


class CButton(button: Button, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var isInverted: Boolean = false

    private var originalParams: LayoutParams? = null
    private var shrunkParams: LayoutParams? = null
    private var minHeight:Int = 0

    private var button: Button? = null
    var backgroundColor: Int? = null
    var targetBackgroundColor: Int? = null
    var shrinkType: ShrinkType = ShrinkType.mid

    var isSelected:Boolean = false
    var willBeShrunk:Boolean = false

    private var parentLayout:AbsoluteLayout? = null

    init {
        this.button = button
        this.button!!.layoutParams = params
        this.minHeight = params.height
        this.parentLayout = parentLayout
        parentLayout.addView(button)
        setOriginalParams(params)
        setShrunkParams()
        setStyle()
        this.button!!.alpha = 0f
    }

    fun getThis(): Button {
        return this.button!!
    }

    fun getParentLayout():AbsoluteLayout {
        return parentLayout!!
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
        if (backgroundColor != null) {
            shape!!.setColor(backgroundColor!!)
        } else if (targetBackgroundColor != null) {
            shape!!.setColor(targetBackgroundColor!!)
        } else {
            if (MainActivity.isThemeDark){
                shape!!.setColor(Color.BLACK)
            } else {
                shape!!.setColor(Color.WHITE)
            }
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
        button!!.setBackgroundDrawable(shape)
    }

    private var growWidthAndChangeColor: AnimatorSet? = null
    private var growWidthAnimator: ValueAnimator? = null
    private var transitionColorAnimator: ValueAnimator? = null
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
            button!!.layoutParams = LayoutParams(
                (it.animatedValue as Float).toInt(),
                getOriginalParams().height,
                getOriginalParams().x,
                getOriginalParams().y
            )
        }
        transitionColorAnimator = ValueAnimator.ofArgb(getBackgroundColor(), targetBackgroundColor!!)
        transitionColorAnimator!!.addUpdateListener {
            button!!.setBackgroundColor(it.animatedValue as Int)
            targetBackgroundColor = (it.animatedValue as Int)
            setCornerRadiusAndBorderWidth((originalParams!!.height / 5.0).toInt(), borderWidth)
        }
        growWidthAndChangeColor = AnimatorSet()
        growWidthAndChangeColor!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        growWidthAndChangeColor!!.play(growWidthAnimator!!).with(transitionColorAnimator!!)
        growWidthAndChangeColor!!.duration = 1000
        growWidthAndChangeColor!!.startDelay = 125
        growWidthAndChangeColorIsRunning = true
        growWidthAndChangeColor!!.start()

        growWidthAndChangeColor!!.doOnEnd {
            growWidthAndChangeColorIsRunning = false
            fadeOutAnimator = ValueAnimator.ofFloat(1f, 0f)
            fadeOutAnimator!!.addUpdateListener{
                button!!.alpha = it.animatedValue as Float
            }
            fadeOutAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
            fadeOutAnimator!!.duration = 750
            fadeOutAnimator!!.start()
            // Do on end
            fadeOutAnimator!!.doOnEnd {
                parentLayout!!.removeView(getThis())
            }
        }
    }

    private var growAnimatorSet:AnimatorSet? = null
    private var growHeightAnimator:ValueAnimator? = null
    private var isGrowing:Boolean = false
    private var width:Float = 0f
    private var height:Float = 0f
    private var y:Float = 0f
    fun grow() {
        if (growAnimatorSet != null) {
            if (isGrowing) {
                growAnimatorSet!!.cancel()
                isGrowing = false
                growAnimatorSet = null
            }
        }
        growWidthAnimator = ValueAnimator.ofFloat(1f, originalParams!!.width.toFloat())
        growWidthAnimator!!.addUpdateListener {
            width = (it.animatedValue as Float)
            x = ((originalParams!!.x + (originalParams!!.width * 0.5) - (width * 0.5)).toFloat())
            button!!.layoutParams = LayoutParams(width.toInt(), height.toInt(), x.toInt(), y.toInt())
        }

        growHeightAnimator = ValueAnimator.ofFloat(1f, originalParams!!.height.toFloat())
        growHeightAnimator!!.addUpdateListener {
            height = (it.animatedValue as Float)
            y = (originalParams!!.y + (originalParams!!.height * 0.5) - (height * 0.5)).toFloat()
            button!!.layoutParams = LayoutParams(width.toInt(), height.toInt(), x.toInt(), y.toInt())
        }
        growAnimatorSet = AnimatorSet()
        growAnimatorSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        growAnimatorSet!!.play(growHeightAnimator!!).with(growWidthAnimator!!)
        growAnimatorSet!!.duration = 1000
        growAnimatorSet!!.startDelay = 125
        isGrowing = true
        growAnimatorSet!!.start()
        growAnimatorSet!!.doOnEnd {
            button!!.layoutParams = originalParams!!
        }
    }

    private var translateAnimatorSet:AnimatorSet? = null
    private var translateWidthAnimator:ValueAnimator? = null
    private var selectAnimator:ValueAnimator? = null
    private var isSelectRunning:Boolean = false
    private var targetWidth:Float = -1f
    private var targetX:Float = -1f
    fun select(targetX:Float, targetWidth:Float) {
        if (selectAnimator != null) {
            if (isSelectRunning) {
                selectAnimator!!.cancel()
                isSelectRunning = false
                selectAnimator = null
            }
        }
        this.targetWidth = targetWidth
        this.targetX = targetX
        selectAnimator = ValueAnimator.ofFloat((button!!.layoutParams as LayoutParams).
        height.toFloat(), minHeight * 1.275f)
        selectAnimator!!.addUpdateListener {
            height = (it.animatedValue as Float)
            y = originalParams!!.y - (((it.animatedValue as Float) - originalParams!!.
            height.toFloat()) * 0.5f)
                button!!.layoutParams = LayoutParams(width.toInt(), height.toInt(), x.toInt(),
                    y.toInt())
        }

        if (targetWidth != -1f && targetX != -1f) {
            translateXAnimator = ValueAnimator.ofFloat((button!!.layoutParams as
                LayoutParams).x.toFloat(), targetX)
            translateWidthAnimator = ValueAnimator.ofFloat((button!!.layoutParams as
                    LayoutParams).width.toFloat(), targetWidth)
        } else {
            translateXAnimator = ValueAnimator.ofFloat((button!!.layoutParams as
                    LayoutParams).x.toFloat(), originalParams!!.x.toFloat())
            translateWidthAnimator = ValueAnimator.ofFloat((button!!.layoutParams as
                    LayoutParams).width.toFloat(), originalParams!!.width.toFloat())
        }

        translateXAnimator!!.addUpdateListener {
            x = (it.animatedValue as Float)
            button!!.layoutParams = LayoutParams(width.toInt(), height.toInt(), x.toInt(), y.toInt())
        }
        translateWidthAnimator!!.addUpdateListener {
            width = (it.animatedValue as Float)
            button!!.layoutParams = LayoutParams(width.toInt(), height.toInt(), x.toInt(), y.toInt())
        }

        translateAnimatorSet = AnimatorSet()
        translateAnimatorSet!!.play(selectAnimator!!).with(translateWidthAnimator!!).
            with(translateXAnimator!!)
        translateAnimatorSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        translateAnimatorSet!!.startDelay = 125
        translateAnimatorSet!!.duration = 500
        isSelectRunning = true
        translateAnimatorSet!!.start()
    }

    private var unSelectAnimatorSet:AnimatorSet? = null
    private var unSelectAnimator:ValueAnimator? = null
    private var isUnSelectRunning:Boolean = false
    fun unSelect() {
        if (unSelectAnimator != null) {
            if (isUnSelectRunning) {
                unSelectAnimator!!.cancel()
                isUnSelectRunning = false
                unSelectAnimator = null
            }
        }
        unSelectAnimator = ValueAnimator.ofFloat((button!!.layoutParams as LayoutParams).
        height.toFloat(), minHeight.toFloat())
        unSelectAnimator!!.addUpdateListener {
            height = (it.animatedValue as Float)
            y = originalParams!!.y - (((it.animatedValue as Float) -
                    originalParams!!.height.toFloat()) * 0.5f)
            button!!.layoutParams = LayoutParams(width.toInt(), height.toInt(),
                x.toInt(), y.toInt())
        }
        translateXAnimator = ValueAnimator.ofFloat((button!!.layoutParams as
                LayoutParams).x.toFloat(), originalParams!!.x.toFloat())
        translateWidthAnimator = ValueAnimator.ofFloat((button!!.layoutParams as
                LayoutParams).width.toFloat(), originalParams!!.width.toFloat())
        translateXAnimator!!.addUpdateListener {
            x = (it.animatedValue as Float)
            button!!.layoutParams = LayoutParams(width.toInt(), height.toInt(), x.toInt(), y.toInt())
        }
        translateWidthAnimator!!.addUpdateListener {
            width = (it.animatedValue as Float)
            button!!.layoutParams = LayoutParams(width.toInt(), height.toInt(), x.toInt(), y.toInt())
        }
        unSelectAnimatorSet = AnimatorSet()
        unSelectAnimatorSet!!.play(unSelectAnimator!!).with(translateXAnimator!!).
        with(translateWidthAnimator!!)
        unSelectAnimatorSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        unSelectAnimatorSet!!.startDelay = 125
        unSelectAnimatorSet!!.duration = 500
        isUnSelectRunning = true
        unSelectAnimatorSet!!.start()
    }

    fun fadeIn() {
        fade(true, false, 1f, 0.125f)
    }

    private var x: Float = 0f
    private var duration: Float = 0f
    private var translateXAnimator: ValueAnimator? = null
    private var shrinkWidthAnimator: ValueAnimator? = null
    private var shrinkAnimationSet: AnimatorSet? = null
    private var shrinkAnimationSetIsRunning: Boolean = false
    fun shrink() {
        if (shrinkAnimationSet != null) {
            if (shrinkAnimationSetIsRunning) {
                shrinkAnimationSet!!.cancel()
                shrinkAnimationSetIsRunning = false
                shrinkAnimationSet = null
            }
        }
        when (shrinkType) {
            ShrinkType.left -> {
                x = ((button!!.layoutParams as LayoutParams).x).toFloat()
                duration = 750f
            }
            ShrinkType.mid -> {
                x = ((button!!.layoutParams as LayoutParams).x +
                        (button!!.layoutParams as LayoutParams).width * 0.5).toFloat()
                duration = 500f
            }
            ShrinkType.right -> {
                x = ((button!!.layoutParams as LayoutParams).x + (button!!.layoutParams as LayoutParams).width).toFloat()
                duration = 750f
            }
        }
        translateXAnimator = ValueAnimator.ofFloat((button!!.layoutParams as LayoutParams).x.toFloat(), x)
        translateXAnimator!!.addUpdateListener {
            x = (it.animatedValue as Float)
            button!!.layoutParams =  LayoutParams(width.toInt(), (button!!.layoutParams as LayoutParams).height,
                x.toInt(), (button!!.layoutParams as LayoutParams).y)
        }
        shrinkWidthAnimator = ValueAnimator.ofFloat((button!!.layoutParams as LayoutParams).width.toFloat(), 0f)
        shrinkWidthAnimator!!.addUpdateListener {
            width = (it.animatedValue as Float)
            button!!.layoutParams = LayoutParams(width.toInt(), (button!!.layoutParams as LayoutParams).height,
                x.toInt(),(button!!.layoutParams as LayoutParams).y)
        }
        shrinkAnimationSet = AnimatorSet()
        shrinkAnimationSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        shrinkAnimationSet!!.play(translateXAnimator!!).with(shrinkWidthAnimator)
        shrinkAnimationSet!!.duration = duration.toLong()
        shrinkAnimationSet!!.start()
        // Do on end
        shrinkAnimationSet!!.doOnEnd {
            parentLayout!!.removeView(this.getThis())
        }
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

    fun shrunk() {
        button!!.layoutParams = shrunkParams!!
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
        setCornerRadiusAndBorderWidth(cornerRadius, borderWidth)
    }
}
