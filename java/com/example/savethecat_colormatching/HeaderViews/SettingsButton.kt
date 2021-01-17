package com.example.savethecat_colormatching.HeaderViews

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageButton
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R

class SettingsButton(imageButton: ImageButton, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var settingsButton:ImageButton? = null

    private var originalParams:LayoutParams? = null
    private var shrunkParams:LayoutParams? = null
    private var parentLayout:AbsoluteLayout? = null

    companion object {
        private var settingsMenu: SettingsMenu? = null
        var borderImageView:ImageButton? = null
    }

    init {
        settingsButton = imageButton
        settingsButton!!.layoutParams = params
        this.parentLayout = MainActivity.rootLayout!!
        parentLayout.addView(settingsButton!!)
        setOriginalParams(params)
        setShrunkParams()
        setupSettingsMenu()
        setupListener()
        // Setup border image view
        borderImageView = ImageButton(settingsButton!!.context)
        borderImageView!!.layoutParams = params
        parentLayout.addView(borderImageView!!)
        // Stop border image view setup
        setStyle()
        settingsButton!!.bringToFront()
        settingsButton!!.alpha = 0f
        settingsMenu!!.getThis().alpha = 0f
        borderImageView!!.alpha = 0f
    }

    fun closeTheSettingsMenu() {
        if (SettingsMenu.isExpanded) {
            clickSettingsButton()
        }
    }

    fun openTheSettingsMenu() {
        if (!SettingsMenu.isExpanded) {
            clickSettingsButton()
        }
        SettingsMenu.mouseCoinButton!!.getThis().performClick()
    }

    /*
        Update the transparency of the settings button
     */
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
        // Fade the settings button in or out
        if (In) {
            fadeAnimator = ValueAnimator.ofFloat(0f, 1f)
        }
        if (Out and !In) {
            fadeAnimator = ValueAnimator.ofFloat(1f, 0f)

        }
        fadeAnimator!!.addUpdateListener {
            val alpha:Float =  (it.animatedValue as Float)
            borderImageView!!.alpha = alpha
            settingsButton!!.alpha = alpha
            settingsMenu!!.getThis().alpha = alpha
            SettingsMenu.adsButton!!.getThis().alpha = alpha
            SettingsMenu.leaderBoardButton!!.getThis().alpha = alpha
            SettingsMenu.volumeButton!!.getThis().alpha = alpha
            SettingsMenu.moreCatsButton!!.getThis().alpha = alpha
            SettingsMenu.mouseCoinButton!!.getThis().alpha = alpha
        }
        // Setup the properties for the fading animation
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
        settingsMenu =
            SettingsMenu(
                view = View(settingsButton!!.context), parentLayout =
                parentLayout!!, params = LayoutParams(
                    width.toInt(), originalParams!!.height,
                    originalParams!!.x, originalParams!!.y
                )
            )
    }

    fun getThis():ImageButton {
        return settingsButton!!
    }

    fun bringContentsForward() {
        settingsMenu!!.getThis().bringToFront()
        SettingsMenu.adsButton!!.getThis().bringToFront()
        SettingsMenu.leaderBoardButton!!.getThis().bringToFront()
        SettingsMenu.volumeButton!!.getThis().bringToFront()
        SettingsMenu.moreCatsButton!!.getThis().bringToFront()
        SettingsMenu.mouseCoinButton!!.getThis().bringToFront()
        borderImageView!!.bringToFront()
        settingsButton!!.bringToFront()
    }

    private fun setupListener() {
        settingsButton!!.setOnClickListener {
            clickSettingsButton()
        }
    }

    /*
        Close or open the settings button based on
        its current state
     */
    private fun clickSettingsButton() {
        if (MainActivity.dAspectRatio < 1.8) {
            if (SettingsMenu.isExpanded) {
                MainActivity.mouseCoinView!!.fadeIn()
            } else {
                MainActivity.mouseCoinView!!.fadeOut()
            }
        }
        settingsMenu!!.expandOrContract()
        SettingsMenu.adsButton!!.expandOrContract()
        SettingsMenu.leaderBoardButton!!.expandOrContract()
        SettingsMenu.volumeButton!!.expandOrContract()
        SettingsMenu.moreCatsButton!!.expandOrContract()
        SettingsMenu.mouseCoinButton!!.expandOrContract()
        rotateGear()
    }

    /*
        Rotate the gear on the settings button
     */
    private var rotateGearAnimator: ValueAnimator? = null
    private var isGearRotating:Boolean = false
    private fun rotateGear() {
        // If the gear is rotating cancel it
        if (isGearRotating) {
            return
        } else {
            if (rotateGearAnimator != null) {
                rotateGearAnimator!!.cancel()
                rotateGearAnimator = null
            }
        }
        // Rotate the gear counter clockwise if the gear is closed
        rotateGearAnimator = if (SettingsMenu.isExpanded) {
            ValueAnimator.ofFloat(225f, 0f)
        } else {
            // Rotate the gear clockwise to open
            ValueAnimator.ofFloat(settingsButton!!.rotation, 225f)
        }
        rotateGearAnimator!!.addUpdateListener {
            settingsButton!!.rotation = (it.animatedValue as Float)
        }
        // Setup the properties for the gear animator
        rotateGearAnimator!!.interpolator = LinearInterpolator()
        rotateGearAnimator!!.duration = 1000
        rotateGearAnimator!!.start()
        isGearRotating = true
        rotateGearAnimator!!.doOnEnd {
            isGearRotating = false
        }
    }

    /*
        Draw the corner radius and the border width
        of the settings button
     */
    private var shape: GradientDrawable? = null
    var borderWidth:Int = 0
    private var cornerRadius:Int = 0
    fun setCornerRadiusAndBorderWidth(radius: Int, borderWidth: Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        // Draw the border
        if (borderWidth > 0) {
            this.borderWidth = borderWidth
            // Draw the background based on the theme
            if (MainActivity.isThemeDark) {
                shape!!.setColor(Color.BLACK)
                shape!!.setStroke(borderWidth, Color.WHITE)
            } else {
                shape!!.setColor(Color.WHITE)
                shape!!.setStroke(borderWidth, Color.BLACK)
            }
        }
        // Draw the corner radius
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

    /*
        Set the theme of all the elments of the settings button,
        and the settings menu based on the current theme of the OS
     */
    fun setCompiledStyle() {
        setStyle()
        settingsMenu!!.setStyle()
        SettingsMenu.adsButton!!.setStyle()
        SettingsMenu.leaderBoardButton!!.setStyle()
        SettingsMenu.volumeButton!!.setStyle()
        SettingsMenu.moreCatsButton!!.setStyle()
    }

    private fun setStyle() {
        setCornerRadiusAndBorderWidth((getOriginalParams().height / 2.0).toInt(),
            (getOriginalParams().height / 12.0).toInt())
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
    }
}