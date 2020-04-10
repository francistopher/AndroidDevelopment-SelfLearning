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
        setStyle()
        setCornerRadiusAndBorderWidth((params.height / 2.0).toInt(),
            (params.height / 12.0).toInt())
        setGearImage()
        gearImage!!.getThis().bringToFront()
        settingsButton!!.bringToFront()
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
            ValueAnimator.ofFloat(240f, 0f)
        } else {
            ValueAnimator.ofFloat(gearImage!!.getThis().rotation, 240f)
        }
        rotateGearAnimator!!.addUpdateListener {
            gearImage!!.getThis().rotation = (it.animatedValue as Float)
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
    private var borderWidth:Int = 0
    private var cornerRadius:Int = 0
    fun setCornerRadiusAndBorderWidth(radius: Int, borderWidth: Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        shape!!.setColor(Color.TRANSPARENT)
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
        settingsButton!!.setBackgroundDrawable(shape)
    }

    private fun setShrunkParams() {
        shrunkParams = LayoutParams(originalParams!!.x / 2, originalParams!!.y / 2, 1, 1)
    }

    fun setOriginalParams(params: LayoutParams) {
        originalParams = params
    }

    private fun lightDominant() {
        settingsButton!!.setBackgroundColor(Color.BLACK)
    }

    private fun darkDominant() {
        settingsButton!!.setBackgroundColor(Color.WHITE)
    }

    private fun setGearImage() {
        gearImage = CImageView(imageView = ImageView(settingsButton!!.context), parentLayout =
        parentLayout!!, params = originalParams!!)
        gearImage!!.loadImages(lightImageR = R.drawable.darkgear, darkImageR = R.drawable.lightgear)
    }

    private fun setStyle() {
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
    }
}