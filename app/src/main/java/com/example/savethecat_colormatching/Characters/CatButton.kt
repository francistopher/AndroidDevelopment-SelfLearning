package com.example.savethecat_colormatching.Characters

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.Animatable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.ViewPropertyAnimator
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.CustomViews.CImageView
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R

class CatButton(imageButton: ImageButton, parentLayout: AbsoluteLayout, params: AbsoluteLayout.LayoutParams, backgroundColor:Int) {

    private var originalParams: LayoutParams? = null
    private var shrunkParams: LayoutParams? = null

    private var imageButton: ImageButton? = null
    private var originalBackgroundColor:Int = 0
    private var imageView:CImageView? = null

    private var buttonContext:Context? = null

    private var buttonLayout:AbsoluteLayout? = null
    private var parentLayout:AbsoluteLayout? = null

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
    }

    private var shape: GradientDrawable? = null
    private var borderWidth:Int = 0
    fun setCornerRadiusAndBorderWidth(radius:Int, borderWidth:Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        shape!!.setColor((imageButton!!.background as ColorDrawable).color)
        if (borderWidth > 0) {
            this.borderWidth = borderWidth
            if (MainActivity.isThemeDark) {
                shape!!.setStroke(borderWidth, Color.WHITE)

            } else {
                shape!!.setStroke(borderWidth, Color.BLACK)
            }
        }
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
        transitionColorAnimator = ValueAnimator.ofArgb(originalBackgroundColor, targetColor)
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

//        imageRotationAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)

    }

}