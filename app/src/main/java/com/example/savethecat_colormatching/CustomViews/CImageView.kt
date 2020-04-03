package com.example.savethecat_colormatching.CustomViews

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.media.Image
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.LinearInterpolator
import android.widget.AbsoluteLayout
import android.widget.Button
import android.widget.ImageView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.example.savethecat_colormatching.Controllers.AudioController
import com.example.savethecat_colormatching.MainActivity

class CImageView(imageView: ImageView, parentLayout: AbsoluteLayout, params: AbsoluteLayout.LayoutParams) {

    var isInverted:Boolean = false
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

    private fun setOriginalParams(params: AbsoluteLayout.LayoutParams) {
        originalParams = params
    }

    fun getOriginalParams():AbsoluteLayout.LayoutParams {
        return originalParams!!
    }

    private fun setShrunkParams() {
        shrunkParams = AbsoluteLayout.LayoutParams(originalParams!!.x / 2, originalParams!!.y / 2, 1, 1)
    }

    var startRotation:Boolean = true
    var rotationAnimator:ViewPropertyAnimator? = null
    var rotation:Float = 120f

    fun rotateText() {
        if (rotationAnimator != null) {
            rotationAnimator!!.cancel()
            rotationAnimator = null
        }

        Log.i("Animation", "Set rotation true")
        if (stopRotation) {
            rotationAnimator = imageView!!.animate().rotation(rotation).alpha(0.0f)
        } else {
            rotationAnimator = imageView!!.animate().rotation(rotation)
        }
        rotationAnimator!!.interpolator = LinearInterpolator()
        if (startRotation) {
            rotationAnimator!!.startDelay = 500
            startRotation = false
        } else {
            rotationAnimator!!.startDelay = 0
        }
        rotationAnimator!!.duration = 1000
        rotationAnimator!!.withEndAction {
            rotation += 120
            rotateText()
        }
        rotationAnimator!!.start()
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
            fadeAnimator = imageView!!.animate().alpha(1.0f)
            fadeAnimator!!.interpolator = FastOutSlowInInterpolator()
        }
        if (Out and !In) {
            fadeAnimator = imageView!!.animate().alpha(0.0f)
            fadeAnimator!!.interpolator = LinearOutSlowInInterpolator()
        }
        fadeAnimator!!.startDelay = (1000.0f * Delay).toLong()
        fadeAnimator!!.duration = (1000.0f * Duration).toLong()
        fadeAnimator!!.withStartAction {
            fadeAnimatorIsRunning = true
        }
        fadeAnimator!!.withEndAction {
            if (In and Out) {
                if (isCatImage) {
                    AudioController.kittenMeow()
                }
                stopRotation = true
                this.fade(In = false, Out = true, Duration =(Duration * 0.75f), Delay = 0.0f)
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

    fun setStyle() {
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