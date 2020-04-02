package com.example.savethecat_colormatching.CustomViews

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.GradientDrawable.*
import android.view.ViewGroup
import android.widget.AbsoluteLayout
import android.widget.TextView
import android.widget.AbsoluteLayout.LayoutParams
import com.example.savethecat_colormatching.MainActivity

class CLabel(textView: TextView, parentLayout: AbsoluteLayout, params:ViewGroup.LayoutParams) {

    private var isInverted:Boolean = false

    private var originalParams:LayoutParams? = null
    private var shrunkParams:LayoutParams? = null

    private var textView:TextView? = null

    init {
        this.textView = textView
        this.textView!!.layoutParams = params
        parentLayout.addView(this.textView)
        setOriginalParams(params = params)
        setStyle()
    }

    fun getThis():TextView {
        return textView!!
    }

    private fun setOriginalParams(params:ViewGroup.LayoutParams) {
        originalParams = params as LayoutParams?
    }

    private fun setShrunkParams() {
        shrunkParams = AbsoluteLayout.LayoutParams(originalParams!!.x / 2, originalParams!!.y / 2, 1, 1)
    }

    fun shrink(removeFromViewParent:Boolean) {

    }

    fun shrunk() {

    }

    fun fadeIn() {

    }

    fun fadeOut() {

    }

    fun fadeInAndOut() {

    }

    var shape:GradientDrawable? = null
    fun setCornerRadius(radius:Int) {
        shape = GradientDrawable()
        shape!!.shape = RECTANGLE
        shape!!.setColor((textView!!.background as ColorDrawable).color)
        shape!!.setStroke(3, Color.BLUE)
        shape!!.cornerRadius = radius.toFloat()
        textView!!.setBackgroundDrawable(shape)
    }

    fun setStyle() {
        if (MainActivity.isThemeDark) {
            textView!!.setBackgroundColor(Color.WHITE)
            textView!!.setTextColor(Color.BLACK)
        } else {
            textView!!.setBackgroundColor(Color.BLACK)
            textView!!.setTextColor(Color.WHITE)
        }
    }

}