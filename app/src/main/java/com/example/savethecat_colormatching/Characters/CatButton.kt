package com.example.savethecat_colormatching.Characters

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.renderscript.Sampler
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.Controllers.AudioController
import com.example.savethecat_colormatching.CustomViews.CImageView
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.ParticularViews.BoardGame
import com.example.savethecat_colormatching.R
import java.lang.Exception
import kotlin.random.Random

class CatButton(imageButton: ImageButton, parentLayout: AbsoluteLayout, params: LayoutParams, backgroundColor:Int) {

    private var originalParams: LayoutParams? = null
    private var shrunkParams: LayoutParams? = null

    private var imageButton: ImageButton? = null
    private var originalBackgroundColor:Int = 0
    private var imageView:CImageView? = null

    private var buttonContext:Context? = null

    private var buttonLayout:AbsoluteLayout? = null
    private var parentLayout:AbsoluteLayout? = null

    var isPodded:Boolean = false
    var isAlive:Boolean = true

    init {
        this.imageButton = imageButton
        this.buttonContext = imageButton.context
        this.buttonLayout = AbsoluteLayout(buttonContext)
        this.imageButton!!.layoutParams = params
        this.originalBackgroundColor = backgroundColor
        this.parentLayout = parentLayout
        parentLayout.addView(imageButton)
        setOriginalParams(params=params)
        setShrunkParams()
        this.imageButton!!.setBackgroundColor(backgroundColor)
        setupImageView()
        this.imageView!!.getThis().alpha = 0f
        this.imageButton!!.alpha = 0f
    }

    private var fadeButtonAnimator:ValueAnimator? = null
    private var fadeCatAnimator:ValueAnimator? = null
    private var fadeAnimatorSet:AnimatorSet? = null
    private var fadeAnimatorIsRunning:Boolean = false
    fun fade(In:Boolean, Out:Boolean, Duration:Float, Delay:Float) {
        if (fadeAnimatorSet != null) {
            if (fadeAnimatorIsRunning) {
                fadeAnimatorSet!!.cancel()
                fadeAnimatorIsRunning = false
                fadeAnimatorSet = null
            }
        }
        if (In) {
            fadeCatAnimator = ValueAnimator.ofFloat(imageView!!.getThis().alpha, 1f)
            fadeButtonAnimator = ValueAnimator.ofFloat(imageButton!!.alpha, 1f)

        } else if (Out and !In) {
            fadeCatAnimator = ValueAnimator.ofFloat(imageView!!.getThis().alpha, 0f)
            fadeButtonAnimator = ValueAnimator.ofFloat(imageButton!!.alpha, 0f)
        }

        fadeCatAnimator!!.addUpdateListener {
            imageView!!.getThis().alpha = it.animatedValue as Float
        }
        fadeButtonAnimator!!.addUpdateListener {
            imageButton!!.alpha = it.animatedValue as Float
        }

        fadeCatAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        fadeButtonAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)

        fadeAnimatorSet = AnimatorSet()
        fadeAnimatorSet!!.play(fadeCatAnimator!!).with(imageRotationAnimator!!)
        fadeAnimatorSet!!.startDelay = (1000.0f * Delay).toLong()
        fadeAnimatorSet!!.duration = (1000.0f * Duration).toLong()

        fadeAnimatorIsRunning = true
    }

    private var shape: GradientDrawable? = null
    private var borderWidth:Int = 0
    private var cornerRadius:Int = 0
    fun setCornerRadiusAndBorderWidth(radius:Int, borderWidth:Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        try {
            shape!!.setColor((imageButton!!.background as ColorDrawable).color)
        } catch (e: Exception) {
            shape!!.setColor(originalBackgroundColor)
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
        imageButton!!.setBackgroundDrawable(shape)
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
        transitionColorAnimator = if (targetColor == Color.TRANSPARENT) {
            ValueAnimator.ofArgb(originalBackgroundColor, targetColor)
        } else {
            ValueAnimator.ofArgb(Color.TRANSPARENT, targetColor)
        }
        transitionColorAnimator!!.addUpdateListener {
            imageButton!!.setBackgroundColor(it.animatedValue as Int)
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

    fun getOriginalParams():LayoutParams {
        return this.originalParams!!
    }

    private fun setShrunkParams() {
        shrunkParams = LayoutParams(originalParams!!.x / 2, originalParams!!.y / 2, 1, 1)
    }

    fun getOriginalBackgroundColor():Int {
        return originalBackgroundColor
    }

    fun getThis():ImageButton {
        return imageButton!!
    }

    private fun setupImageView() {
        imageView = CImageView(imageView = ImageView(buttonContext!!),
            parentLayout = parentLayout!!, params = originalParams!!)
        imageView!!.loadImages(R.drawable.lightsmilingcat, R.drawable.darksmilingcat)
        startImageRotation()
    }

    var imageRotationAnimator:ValueAnimator? = null
    var isImageRotating:Boolean = false
    var rotateImageToRight:Boolean = true
    private fun startImageRotation() {
        if (imageRotationAnimator != null) {
            if (isImageRotating) {
                imageRotationAnimator!!.cancel()
                isImageRotating = false
                imageRotationAnimator = null
            }
        }
        imageRotationAnimator = if (rotateImageToRight) {
            ValueAnimator.ofFloat(-90f, 90f)
        } else {
            ValueAnimator.ofFloat(90f, -90f)
        }
        imageRotationAnimator!!.addUpdateListener {
            imageView!!.getThis().rotation = (it.animatedValue as Float)
        }
        imageRotationAnimator!!.duration = 1750
        imageRotationAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        isImageRotating = true
        imageRotationAnimator!!.start()
        imageRotationAnimator!!.doOnEnd {
            startImageRotation()
            rotateImageToRight = !rotateImageToRight
        }
    }

    private var podAnimator:ValueAnimator? = null
    private var isPodAnimatorRunning:Boolean = false
    fun pod() {
        isPodded = true
        if (podAnimator != null) {
            if (isPodAnimatorRunning) {
                podAnimator!!.cancel()
                isPodAnimatorRunning = false
                podAnimator = null
            }
        }
        AudioController.kittenMeow()
        podAnimator = ValueAnimator.ofFloat(cornerRadius.toFloat(), (imageButton!!.layoutParams as LayoutParams).width * 0.5f)
        podAnimator!!.addUpdateListener {
            setCornerRadiusAndBorderWidth((it.animatedValue as Float).toInt(), borderWidth)
        }
        podAnimator!!.duration = 500
        podAnimator!!.startDelay = 125
        podAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        isPodAnimatorRunning = true
        podAnimator!!.start()
    }

    private var growAnimatorSet:AnimatorSet? = null
    private var growWidthAnimator:ValueAnimator? = null
    private var growHeightAnimator:ValueAnimator? = null
    private var isGrowing:Boolean = false
    private var width:Int = 0
    private var height:Int = 0
    private var x:Int = 0
    private var y:Int = 0
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
            width = (it.animatedValue as Float).toInt()
            x = (originalParams!!.x + (originalParams!!.width * 0.5) - (width * 0.5)).toInt()
            y = (originalParams!!.y + (originalParams!!.height * 0.5) - (height * 0.5)).toInt()
            imageButton!!.layoutParams = LayoutParams(width, height, x, y)
            imageView!!.getThis().layoutParams = LayoutParams(width, height, x, y)
        }

        growHeightAnimator = ValueAnimator.ofFloat(1f, originalParams!!.height.toFloat())
        growHeightAnimator!!.addUpdateListener {
            height = (it.animatedValue as Float).toInt()
            x = (originalParams!!.x + (originalParams!!.width * 0.5) - (width * 0.5)).toInt()
            y = (originalParams!!.y + (originalParams!!.height * 0.5) - (height * 0.5)).toInt()
            imageButton!!.layoutParams = LayoutParams(width, height, x, y)
            imageView!!.getThis().layoutParams = LayoutParams(width, height, x, y)
            imageButton!!.alpha = 1f
            imageView!!.getThis().alpha = 1f
        }

        growAnimatorSet = AnimatorSet()
        growAnimatorSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        growAnimatorSet!!.play(growHeightAnimator!!).with(growWidthAnimator!!)
        growAnimatorSet!!.duration = 1000
        growAnimatorSet!!.startDelay = 125
        isGrowing = true
        growAnimatorSet!!.start()
        growAnimatorSet!!.doOnEnd {
            imageView!!.getThis().layoutParams = originalParams!!
            imageButton!!.layoutParams = originalParams!!
        }
    }

    private var angle:Float = 0f
    private var disperseVerticalSet:AnimatorSet? = null
    private var disperseVerticalXAnimator:ValueAnimator? = null
    private var disperseVerticalYAnimator:ValueAnimator? = null
    private var isDispersedVertically:Boolean = false
    private var targetY:Float = 0.0f
    fun disperseVertically() {
        imageView!!.loadImages(R.drawable.lightcheeringcat, R.drawable.darkcheeringcat)
        angle = (0..30).random().toFloat()
        disperseVerticalXAnimator = ValueAnimator.ofFloat(originalParams!!.x.toFloat(), getElevatedTargetX())
        disperseVerticalXAnimator!!.addUpdateListener {
            imageButton!!.layoutParams = LayoutParams(
                originalParams!!.width, originalParams!!.height,
                (it.animatedValue as Float).toInt(), targetY.toInt())
            imageView!!.getThis().layoutParams = LayoutParams(
                originalParams!!.width, originalParams!!.height,
                (it.animatedValue as Float).toInt(), targetY.toInt())
        }

        disperseVerticalYAnimator = ValueAnimator.ofFloat(originalParams!!.y.toFloat(), getElevatedTargetY())
        disperseVerticalYAnimator!!.addUpdateListener {
            targetY = (it.animatedValue as Float)
        }
        disperseVerticalSet = AnimatorSet()
        disperseVerticalSet!!.play(disperseVerticalYAnimator).with(disperseVerticalXAnimator)
        disperseVerticalSet!!.duration = 3000
        disperseVerticalSet!!.startDelay = 125
        disperseVerticalSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        isDispersedVertically = true
        disperseVerticalSet!!.start()
    }

    var targetX:Float = 0f
    private fun getElevatedTargetX():Float {
        targetX = MainActivity.dWidth.toFloat() * 0.5f
        if (angle < 15f) {
            targetX -= originalParams!!.width
        } else {
            targetX += originalParams!!.width
        }
        targetX *= kotlin.math.cos(angle.toDouble()).toFloat()
        return targetX
    }

    private fun getElevatedTargetY(): Float {
        return -Random.nextInt((originalParams!!.height),
            (originalParams!!.height * 2.0).toInt()).toFloat()
    }

    fun shrunk() {
        imageButton!!.layoutParams = shrunkParams!!
        imageView!!.getThis().layoutParams = shrunkParams!!
    }

}

