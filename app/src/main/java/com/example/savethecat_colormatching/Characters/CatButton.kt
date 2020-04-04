package com.example.savethecat_colormatching.Characters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import com.example.savethecat_colormatching.MainActivity

class CatButton(button: Button, parentLayout: AbsoluteLayout, params: AbsoluteLayout.LayoutParams, backgroundColor:Int) {

    private var originalParams: LayoutParams? = null
    private var shrunkParams: LayoutParams? = null

    private var button: Button? = null
    private var originalBackgroundColor:Int = 0

    init {
        this.button = button
        this.button!!.layoutParams = params
        this.originalBackgroundColor = backgroundColor
        parentLayout.addView(button)
        setOriginalParams(params=params)
        setShrunkParams()
        this.button!!.setBackgroundResource(backgroundColor)
    }

    private var shape: GradientDrawable? = null
    fun setCornerRadiusAndBorderWidth(radius:Int, borderWidth:Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        shape!!.setColor((button!!.background as ColorDrawable).color)
        if (borderWidth > 0) {
            if (MainActivity.isThemeDark) {
                shape!!.setStroke(borderWidth, Color.WHITE)

            } else {
                shape!!.setStroke(borderWidth, Color.BLACK)
            }
        }
        shape!!.cornerRadius = radius.toFloat()
        button!!.setBackgroundDrawable(shape)
    }

    private fun setOriginalParams(params:LayoutParams) {
        originalParams = params
    }

    private fun setShrunkParams() {
        shrunkParams = AbsoluteLayout.LayoutParams(originalParams!!.x / 2, originalParams!!.y / 2, 1, 1)
    }

    fun getOriginalBackgroundColor():Int {
        return originalBackgroundColor
    }

    fun getThis():Button {
        return button!!
    }

    fun setStyle() {
        if (MainActivity.isThemeDark) {
            button!!.setBackgroundColor(Color.WHITE)
        } else {
            button!!.setBackgroundColor(Color.BLACK)
        }
    }
}