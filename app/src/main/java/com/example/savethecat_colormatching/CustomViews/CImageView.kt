package com.example.savethecat_colormatching.CustomViews

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.media.Image
import android.util.TypedValue
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.Button
import android.widget.ImageView
import com.example.savethecat_colormatching.MainActivity

class CImageView(imageView: ImageView, parentLayout: AbsoluteLayout, params: AbsoluteLayout.LayoutParams) {

    var isInverted:Boolean = false

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

    fun setupImage(lightImageR:Int, darkImageR:Int) {
        this.lightImageR = lightImageR
        this.darkImageR = darkImageR
        setStyle()
    }

    private fun setOriginalParams(params: AbsoluteLayout.LayoutParams) {
        originalParams = params
    }

    private fun setShrunkParams() {
        shrunkParams = AbsoluteLayout.LayoutParams(originalParams!!.x / 2, originalParams!!.y / 2, 1, 1)
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