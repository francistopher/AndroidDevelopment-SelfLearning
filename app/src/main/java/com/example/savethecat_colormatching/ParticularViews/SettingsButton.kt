package com.example.savethecat_colormatching.ParticularViews

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.CustomViews.CImageView
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R

class SettingsButton(button: Button, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var settingsButton:Button? = null

    private var originalParams:LayoutParams? = null
    private var shrunkParams:LayoutParams? = null

    private var parentLayout:AbsoluteLayout? = null
    private var gearImage:CImageView? = null

    companion object {
        private var settingsMenu:SettingsMenu? = null
        var settingsButtonLayout:AbsoluteLayout? = null
    }

    var borderImageView:Button? = null

    init {
        settingsButton = button
        settingsButton!!.layoutParams = params
        settingsButtonLayout = AbsoluteLayout(button.context)
        this.parentLayout = parentLayout
        parentLayout.addView(settingsButton!!)
        setOriginalParams(params)
        setShrunkParams()
        setupSettingsMenu()
        setupListener()
        // Setup border image view
        borderImageView = Button(settingsButton!!.context)
        borderImageView!!.layoutParams = params
        parentLayout.addView(borderImageView!!)

        // Stop border image view setup
        setCornerRadiusAndBorderWidth((params.height / 2.0).toInt(),
            (params.height / 12.0).toInt())
        setStyle()
        settingsButton!!.bringToFront()
        settingsButton!!.alpha = 0f
        settingsMenu!!.getThis().alpha = 0f
    }

    private var fadeAnimator: ValueAnimator? = null
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
            fadeAnimator = ValueAnimator.ofFloat(0f, 1f)
        }
        if (Out and !In) {
            fadeAnimator = ValueAnimator.ofFloat(1f, 0f)

        }
        fadeAnimator!!.addUpdateListener {
            val alpha:Float =  (it.animatedValue as Float)
            settingsButton!!.alpha = alpha
            settingsMenu!!.getThis().alpha = alpha
            SettingsMenu.adsButton!!.getThis().alpha = alpha
            SettingsMenu.leaderBoardButton!!.getThis().alpha = alpha
            SettingsMenu.volumeButton!!.getThis().alpha = alpha
            SettingsMenu.moreCatsButton!!.getThis().alpha = alpha
            SettingsMenu.mouseCoinButton!!.getThis().alpha = alpha
        }
        fadeAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        fadeAnimator!!.startDelay = (1000.0f * Delay).toLong()
        fadeAnimator!!.duration = (1000.0f * Duration).toLong()
        fadeAnimator!!.doOnEnd {
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
        fadeAnimatorIsRunning = true
    }

    fun fadeIn() {
        fade(true, false, 1f, 0.125f)
    }

    private fun setupSettingsMenu() {
        val width:Float = (MainActivity.dWidth - (originalParams!!.x * 2)).toFloat()
        settingsMenu = SettingsMenu(view = View(settingsButton!!.context), parentLayout =
        parentLayout!!, params = LayoutParams(width.toInt(), originalParams!!.height,
            originalParams!!.x, originalParams!!.y))
    }

    private fun setupListener() {
        settingsButton!!.setOnClickListener {
            settingsMenu!!.expandOrContract()
            SettingsMenu.adsButton!!.expandOrContract()
            SettingsMenu.leaderBoardButton!!.expandOrContract()
            SettingsMenu.volumeButton!!.expandOrContract()
            SettingsMenu.moreCatsButton!!.expandOrContract()
            SettingsMenu.mouseCoinButton!!.expandOrContract()
            rotateGear()
        }
    }

    private var rotateGearAnimator: ValueAnimator? = null
    private var isGearRotating:Boolean = false
    private fun rotateGear() {
        if (isGearRotating) {
            return
        } else {
            if (rotateGearAnimator != null) {
                rotateGearAnimator!!.cancel()
                rotateGearAnimator = null
            }
        }
        rotateGearAnimator = if (SettingsMenu.isExpanded) {
            ValueAnimator.ofFloat(225f, 0f)
        } else {
            ValueAnimator.ofFloat(settingsButton!!.rotation, 225f)
        }
        rotateGearAnimator!!.addUpdateListener {
            settingsButton!!.rotation = (it.animatedValue as Float)
        }
        rotateGearAnimator!!.interpolator = LinearInterpolator()
        rotateGearAnimator!!.duration = 1000
        rotateGearAnimator!!.start()
        isGearRotating = true
        rotateGearAnimator!!.doOnEnd {
            isGearRotating = false
        }
    }

    private var shape: GradientDrawable? = null
    var borderWidth:Int = 0
    private var cornerRadius:Int = 0
    fun setCornerRadiusAndBorderWidth(radius: Int, borderWidth: Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        if (borderWidth > 0) {
            this.borderWidth = borderWidth
            if (MainActivity.isThemeDark) {
                shape!!.setColor(Color.BLACK)
                shape!!.setStroke(borderWidth, Color.WHITE)
            } else {
                shape!!.setColor(Color.WHITE)
                shape!!.setStroke(borderWidth, Color.BLACK)
            }
        }
        cornerRadius = radius
        shape!!.cornerRadius = radius.toFloat()
        settingsButton!!.setBackgroundDrawable(shape)
        borderImageView!!.setBackgroundDrawable(shape)
    }

    private fun setShrunkParams() {
        shrunkParams = LayoutParams(originalParams!!.x / 2, originalParams!!.y / 2, 1, 1)
    }

    fun setOriginalParams(params: LayoutParams) {
        originalParams = params
    }

    fun getOriginalParams():LayoutParams {
        return originalParams!!
    }

    private fun lightDominant() {
        settingsButton!!.setBackgroundResource(R.drawable.lightgear)
    }

    private fun darkDominant() {
        settingsButton!!.setBackgroundResource(R.drawable.darkgear)
    }

    private fun setStyle() {
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
    }
}