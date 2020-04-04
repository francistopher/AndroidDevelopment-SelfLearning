package com.example.savethecat_colormatching.CustomViews

import android.app.ActionBar
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.AbsoluteLayout
import android.widget.Button
import com.example.savethecat_colormatching.MainActivity
import com.google.android.material.shape.AbsoluteCornerSize

class CButton(button:Button, parentLayout:AbsoluteLayout, params:LayoutParams) {

    private var isInverted:Boolean = false

    private var originalParams:LayoutParams? = null
    private var shrunkParams:LayoutParams? = null

    private var button:Button? = null
    private var backgrounColor:Int? = null

    init {
        this.button = button
        this.button!!.layoutParams = params
        parentLayout.addView(button)
        setOriginalParams(params)
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

    fun getOriginalParams():LayoutParams {
        return originalParams!!
    }

    private fun setShrunkParams() {
        shrunkParams = LayoutParams(originalParams!!.x / 2, originalParams!!.y / 2, 1, 1)
    }

    fun setStyle() {
        fun lightDominant() {
            if (backgrounColor == null) {
                button!!.setBackgroundColor(Color.BLACK)
            } else {
                button!!.setBackgroundColor(backgrounColor!!)
                button!!.setBackgroundResource(backgrounColor!!)
            }
            button!!.setTextColor(Color.WHITE)
        }

        fun darkDominant() {
            if (backgrounColor == null) {
                button!!.setBackgroundColor(Color.WHITE)
            } else {
                button!!.setBackgroundColor(backgrounColor!!)
                button!!.setBackgroundResource(backgrounColor!!)
            }
            button!!.setTextColor(Color.BLACK)
        }
        if (MainActivity.isThemeDark) {
            if (isInverted) {
                darkDominant()
            } else {
                lightDominant()
            }
        } else {
            if (isInverted) {
                lightDominant()
            } else {
                darkDominant()
            }
        }
    }
}
