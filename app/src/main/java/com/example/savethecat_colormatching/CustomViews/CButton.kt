package com.example.savethecat_colormatching.CustomViews

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.AbsoluteLayout
import android.widget.Button
import com.example.savethecat_colormatching.MainActivity

class CButton(button:Button, parentLayout:AbsoluteLayout, params:LayoutParams) {

    private var isInverted:Boolean = false

    private var originalParams:LayoutParams? = null
    private var shrunkParams:LayoutParams? = null

    private var button:Button? = null

    init {
        this.button = button
        this.button!!.layoutParams = params
        parentLayout.addView(button)
        setOriginalParams(params=params)
        setShrunkParams()
        setStyle()
    }

    fun setTextSize(size:Float) {
        button!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    }

    fun setText(text:String) {
        button!!.text = text
    }

    private var shape: GradientDrawable? = null
    fun setCornerRadiusAndBorderWidth(radius:Int, borderWidth:Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        shape!!.setColor((button!!.background as ColorDrawable).color)
        if (borderWidth > 0) {
            shape!!.setStroke(borderWidth, Color.BLUE)
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

    fun setStyle() {
        fun lightDominant() {
            button!!.setBackgroundColor(Color.BLACK)
            button!!.setTextColor(Color.WHITE)
        }

        fun darkDominant() {
            button!!.setBackgroundColor(Color.WHITE)
            button!!.setTextColor(Color.BLACK)
        }

        if (MainActivity.isThemeDark) {
            if (isInverted) {
                lightDominant()
            } else {
                darkDominant()
            }
        } else {
            if (isInverted) {
                darkDominant()
            } else {
                lightDominant()
            }
        }
    }
}
