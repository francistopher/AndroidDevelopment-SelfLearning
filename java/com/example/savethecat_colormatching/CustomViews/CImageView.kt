package com.example.savethecat_colormatching.CustomViews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.view.ViewPropertyAnimator
import android.view.animation.LinearInterpolator
import android.widget.AbsoluteLayout
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.Controllers.AudioController
import com.example.savethecat_colormatching.MainActivity

class CImageView(imageView: ImageView, parentLayout: AbsoluteLayout, params: AbsoluteLayout.LayoutParams) {

    var isCatImage:Boolean = false

    private var originalParams: AbsoluteLayout.LayoutParams? = null
    private var shrunkParams: AbsoluteLayout.LayoutParams? = null
    private var imageView: ImageView? = null
    private var lightImageR: Int = 0
    private var darkImageR: Int = 0

    init {
        this.imageView = imageView
        this.imageView!!.layoutParams = params
        parentLayout.addView(imageView)
        setOriginalParams(params=params)
        setShrunkParams()
    }

    companion object {
        var stopRotation:Boolean = false
    }

    fun loadImages(lightImageR:Int, darkImageR:Int) {
        this.lightImageR = lightImageR
        this.darkImageR = darkImageR
        setStyle()
    }

    fun getThis(): ImageView {
        return imageView!!
    }

    fun getContext(): Context {
        return imageView!!.context
    }

    fun setOriginalParams(params: AbsoluteLayout.LayoutParams) {
        originalParams = params
    }

    fun getOriginalParams():AbsoluteLayout.LayoutParams {
        return originalParams!!
    }

    private fun setShrunkParams() {
        shrunkParams = AbsoluteLayout.LayoutParams(originalParams!!.x / 2, originalParams!!.y / 2, 1, 1)
    }

    /*
        Rotates the text around the button
     */
    var rotationAnimator:ValueAnimator? = null
    var rotation:Float = 0f
    fun rotateText() {
        // If the rotation animation is running, then cancel it
        if (rotationAnimator != null) {
            rotationAnimator!!.cancel()
            rotationAnimator = null
        }
        // Rotate the iamge counterclockwise
        rotationAnimator = ValueAnimator.ofFloat(rotation, rotation + 90)
        rotationAnimator!!.addUpdateListener {
            imageView!!.rotation = (it.animatedValue as Float)
        }
        // Setup the properties for the rotation animation
        rotationAnimator!!.interpolator = LinearInterpolator()
        rotationAnimator!!.duration = 1000
        rotationAnimator!!.doOnEnd {
            rotation += 90
            rotateText()
        }
        rotationAnimator!!.start()
    }

    /*
        Disappear or appear the image view
     */
    private var fadeAnimator: ViewPropertyAnimator? = null
    private var fadeAnimatorIsRunning:Boolean = false
    fun fade(In:Boolean, Out:Boolean, Duration:Float, Delay:Float) {
        // Cancel the fade animation if its running
        if (fadeAnimator != null) {
            if (fadeAnimatorIsRunning) {
                fadeAnimator!!.cancel()
                fadeAnimatorIsRunning = false
                fadeAnimator = null
            }
        }
        // Fade in or fade out
        if (In) {
            fadeAnimator = imageView!!.animate().alpha(1.0f)
            fadeAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        }
        if (Out and !In) {
            fadeAnimator = imageView!!.animate().alpha(0.0f)
            fadeAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        }
        // Setup the properties for the animation
        fadeAnimator!!.startDelay = (1000.0f * Delay).toLong()
        fadeAnimator!!.duration = (1000.0f * Duration).toLong()
        fadeAnimator!!.withStartAction {
            fadeAnimatorIsRunning = true
        }
        fadeAnimator!!.withEndAction {
            fadeAnimator!!.cancel()
            fadeAnimatorIsRunning = false
            fadeAnimator = null
        }
        if (!fadeAnimatorIsRunning) {
            fadeAnimator!!.start()
        }
    }

    /*
        Fades out the image entirely
     */
    fun fadeOut(Duration: Float) {
        if (isCatImage) {
            AudioController.kittenMeow()
            this.fade(In = false, Out = true, Duration =(Duration), Delay = 0.0f)
        } else {
            this.fade(In = false, Out = true, Duration =(Duration), Delay = 0.0f)
        }
        stopRotation = true
    }

    /*
        Set the style of the image view based on the theme of the
        operating system
     */
    fun setStyle() {
        imageView!!.setBackgroundColor(Color.TRANSPARENT)
        imageView!!.scaleType = ImageView.ScaleType.FIT_CENTER
        fun lightDominant() {
            imageView!!.setImageResource(lightImageR)
        }
        fun darkDominant() {
            imageView!!.setImageResource(darkImageR)
        }
        if (MainActivity.isThemeDark) {
            darkDominant()
        } else {
            lightDominant()
        }
    }
}