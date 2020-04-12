package com.example.savethecat_colormatching.Characters

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.Controllers.AudioController
import com.example.savethecat_colormatching.CustomViews.CImageView
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R
import kotlin.math.cos
import kotlin.math.sin
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

    private var dontStartImageRotation:Boolean = false
    var rowIndex:Int = 0
    var columnIndex:Int = 0

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

    private var fadeInAnimator:ValueAnimator? = null
    private var fadeAnimatorIsRunning:Boolean = false
    fun fade(In:Boolean, Out:Boolean, Duration:Float, Delay:Float) {
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
            imageView!!.getThis().alpha = it.animatedValue as Float
            imageButton!!.alpha = it.animatedValue as Float
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
    fun setCornerRadiusAndBorderWidth(radius:Int, borderWidth:Int, withBackground:Boolean) {
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        if (withBackground) {
            try {
                shape!!.setColor((imageButton!!.background as ColorDrawable).color)
            } catch (e: Exception) {
                shape!!.setColor(originalBackgroundColor)
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
            setCornerRadiusAndBorderWidth((originalParams!!.height / 5.0).toInt(), borderWidth,
                true)
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

    fun doNotStartRotationAndShow() {
        dontStartImageRotation = true
    }

    var imageRotationAnimator:ValueAnimator? = null
    var isImageRotating:Boolean = false
    var rotateImageToRight:Boolean = true
    var stopImageRotation:Boolean = false
    private fun startImageRotation() {
        if (dontStartImageRotation) {
            imageView!!.getThis().rotation = 0f
            return
        }
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
            if (!stopImageRotation) {
                startImageRotation()
                rotateImageToRight = !rotateImageToRight
            }
        }
    }

    private var podAnimator:ValueAnimator? = null
    private var isPodAnimatorRunning:Boolean = false
    fun pod() {
        AudioController.kittenMeow()
        isPodded = true
        if (podAnimator != null) {
            if (isPodAnimatorRunning) {
                podAnimator!!.cancel()
                isPodAnimatorRunning = false
                podAnimator = null
            }
        }
        podAnimator = ValueAnimator.ofFloat(cornerRadius.toFloat(), (imageButton!!.layoutParams as LayoutParams).width * 0.5f)
        podAnimator!!.addUpdateListener {
            setCornerRadiusAndBorderWidth((it.animatedValue as Float).toInt(), borderWidth, withBackground = true)
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
    private var disperseSet:AnimatorSet? = null
    private var disperseXAnimator:ValueAnimator? = null
    private var disperseYAnimator:ValueAnimator? = null
    private var isDispersed:Boolean = false
    private var targetY:Float = 0.0f
    fun disperseVertically() {
        imageView!!.loadImages(R.drawable.lightcheeringcat, R.drawable.darkcheeringcat)
        angle = (0..30).random().toFloat()
        disperseXAnimator = ValueAnimator.ofFloat(originalParams!!.x.toFloat(), getElevatedTargetX())
        disperseXAnimator!!.addUpdateListener {
            imageButton!!.layoutParams = LayoutParams(
                originalParams!!.width, originalParams!!.height,
                (it.animatedValue as Float).toInt(), targetY.toInt())
            imageView!!.getThis().layoutParams = LayoutParams(
                originalParams!!.width, originalParams!!.height,
                (it.animatedValue as Float).toInt(), targetY.toInt())
        }

        disperseYAnimator = ValueAnimator.ofFloat(originalParams!!.y.toFloat(), getElevatedTargetY())
        disperseYAnimator!!.addUpdateListener {
            targetY = (it.animatedValue as Float)
        }
        disperseSet = AnimatorSet()
        disperseSet!!.play(disperseYAnimator).with(disperseXAnimator)
        disperseSet!!.duration = 3000
        disperseSet!!.startDelay = 125
        disperseSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        isDispersed = true
        disperseSet!!.start()
    }

    var targetX:Float = 0f
    private fun getElevatedTargetX():Float {
        targetX = MainActivity.dWidth.toFloat() * 0.5f
        if (angle < 15f) {
            targetX -= originalParams!!.width
        } else {
            targetX += originalParams!!.width
        }
        targetX *= cos(angle.toDouble()).toFloat()
        return targetX
    }

    fun getThisImage():ImageView {
        return imageView!!.getThis()
    }

    private fun getElevatedTargetY(): Float {
        return -Random.nextInt((originalParams!!.height),
            (originalParams!!.height * 2.0).toInt()).toFloat()
    }

    private var radialAnimator:ValueAnimator? = null
    fun disperseRadially() {
        MainActivity.rootLayout!!.removeView(imageView!!.getThis())
        MainActivity.rootLayout!!.addView(imageView!!.getThis(), 0)
        MainActivity.rootLayout!!.removeView(imageButton!!)
        MainActivity.rootLayout!!.addView(imageButton!!, 0)
        stopImageRotation = true
        imageRotationAnimator!!.cancel()
        transitionColor(targetColor = originalBackgroundColor)
        isAlive = false
        AudioController.kittenDie()
        imageView!!.loadImages(lightImageR = R.drawable.lightdeadcat,
            darkImageR = R.drawable.darkdeadcat)
        disperseXAnimator = ValueAnimator.ofFloat(originalParams!!.x.toFloat(), getRadialTargetX())
        disperseXAnimator!!.addUpdateListener {
            targetX = (it.animatedValue as Float)
            imageButton!!.layoutParams = LayoutParams(
                originalParams!!.width, originalParams!!.height,
                targetX.toInt(), targetY.toInt())
            imageView!!.getThis().layoutParams = LayoutParams(
                originalParams!!.width, originalParams!!.height,
               targetX.toInt(), targetY.toInt())
        }
        disperseYAnimator = ValueAnimator.ofFloat(originalParams!!.y.toFloat(), getRadialTargetY())
        disperseYAnimator!!.addUpdateListener {
            targetY = (it.animatedValue as Float)
        }
        if ((0..1).random() == 0) {
            radialAnimator = ValueAnimator.ofFloat(0f,
                360f)
        } else {
            radialAnimator = ValueAnimator.ofFloat(0f,
                -360f)
        }
        radialAnimator!!.addUpdateListener {
            imageView!!.getThis().rotation = (it.animatedValue as Float)
            imageButton!!.rotation = (it.animatedValue as Float)
        }
        disperseSet = AnimatorSet()
        disperseSet!!.play(disperseYAnimator).with(disperseXAnimator).with(radialAnimator!!)
        disperseSet!!.duration = 3000
        disperseSet!!.startDelay = 125
        disperseSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        isDispersed = true
        disperseSet!!.start()
        disperseSet!!.doOnEnd {
            parentLayout!!.removeView(imageButton!!)
            parentLayout!!.removeView(imageView!!.getThis())
        }
    }

    fun setCatImageAlpha(alpha:Float) {
        imageView!!.getThis().alpha = alpha
    }

    private fun getRadialTargetX():Float {
        angle = (0..45).random().toFloat()
        targetX = (MainActivity.dWidth.toFloat() + originalParams!!.width) * 1.42f
        targetX *= cos(angle)
        if ((0..1).random() == 1) {
            targetX *= -1
        }
        return targetX
    }

    private fun getRadialTargetY():Float {
        angle = (45..90).random().toFloat()
        targetY = (MainActivity.dHeight.toFloat() + originalParams!!.height) * 1.42f
        targetY *= sin(angle)
        if ((0..1).random() == 1) {
            targetY *= -1
        }
        return targetY
    }

    private var transformAnimatorSet:AnimatorSet? = null
    private var transformXAnimator:ValueAnimator? = null
    private var transformYAnimator:ValueAnimator? = null
    private var transformWidthAnimator:ValueAnimator? = null
    private var transformHeightAnimator:ValueAnimator? = null
    private var isTransforming:Boolean = false
    fun transformTo(newParams:LayoutParams) {
        if (transformAnimatorSet != null) {
            if (isTransforming) {
                transformAnimatorSet!!.cancel()
                isTransforming = false
                transformAnimatorSet = null
            }
        }
        originalParams = newParams
        transformXAnimator = ValueAnimator.ofFloat((imageButton!!.layoutParams as
                LayoutParams).x.toFloat(), newParams.x.toFloat())
        transformXAnimator!!.addUpdateListener {
            x = (it.animatedValue as Float).toInt()
            imageButton!!.layoutParams = LayoutParams(width, height, x, y)
            imageView!!.getThis().layoutParams = LayoutParams(width, height, x, y)
        }
        transformYAnimator = ValueAnimator.ofFloat((imageButton!!.layoutParams as
                LayoutParams).y.toFloat(), newParams.y.toFloat())
        transformYAnimator!!.addUpdateListener {
            y = (it.animatedValue as Float).toInt()
            imageButton!!.layoutParams = LayoutParams(width, height, x, y)
            imageView!!.getThis().layoutParams = LayoutParams(width, height, x, y)
        }
        transformWidthAnimator = ValueAnimator.ofFloat((imageButton!!.layoutParams as
                LayoutParams).width.toFloat(), newParams.width.toFloat())
        transformWidthAnimator!!.addUpdateListener {
            width = (it.animatedValue as Float).toInt()
            imageButton!!.layoutParams = LayoutParams(width, height, x, y)
            imageView!!.getThis().layoutParams = LayoutParams(width, height, x, y)
        }
        transformHeightAnimator = ValueAnimator.ofFloat((imageButton!!.layoutParams as
                LayoutParams).height.toFloat(), newParams.height.toFloat())
        transformHeightAnimator!!.addUpdateListener {
            height = (it.animatedValue as Float).toInt()
            imageButton!!.layoutParams = LayoutParams(width, height, x, y)
            imageView!!.getThis().layoutParams = LayoutParams(width, height, x, y)
            if (isPodded) {
                setCornerRadiusAndBorderWidth((height / 5f).toInt(), borderWidth,
                    withBackground = true)
            } else {
                setCornerRadiusAndBorderWidth((height / 5f).toInt(), borderWidth,
                    withBackground = false)
            }
        }
        transformAnimatorSet = AnimatorSet()
        transformAnimatorSet!!.play(transformXAnimator!!).with(transformYAnimator!!).
        with(transformWidthAnimator!!).with(transformHeightAnimator!!)
        transformAnimatorSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        transformAnimatorSet!!.startDelay = 125
        transformAnimatorSet!!.duration = 500
        isTransforming = true
        transformAnimatorSet!!.start()
        transformAnimatorSet!!.doOnEnd {
            imageButton!!.layoutParams = originalParams!!
            imageView!!.getThis().layoutParams = originalParams!!
        }
    }

    fun shrunk() {
        imageButton!!.layoutParams = shrunkParams!!
        imageView!!.getThis().layoutParams = shrunkParams!!
    }

}

