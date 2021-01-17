package com.example.savethecat_colormatching.CustomViews

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import com.example.savethecat_colormatching.MainActivity

class CView(view: View, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var view: View? = null

    private var originalParams:LayoutParams? = null

    init {
        this.view = view
        this.view!!.layoutParams = params
        parentLayout.addView(view)
        setOriginalParams(params)
        setStyle()
    }

    /*
        Set the corner radius and the border width of the view
     */
    private var shape: GradientDrawable? = null
    private var borderWidth:Int = 0
    private var cornerRadius:Int = 0
    fun setCornerRadiusAndBorderWidth(radius: Int, borderWidth: Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        // Draw the background of the view
        if (MainActivity.isThemeDark) {
            shape!!.setColor(Color.BLACK)
        } else {
            shape!!.setColor(Color.WHITE)
        }
        // Draw the border width of the view
        if (borderWidth > 0) {
            this.borderWidth = borderWidth
            if (MainActivity.isThemeDark) {
                shape!!.setStroke(borderWidth, Color.WHITE)
            } else {
                shape!!.setStroke(borderWidth, Color.BLACK)
            }
        }
        // Draw the corner radius of the view
        cornerRadius = radius
        shape!!.cornerRadius = radius.toFloat()
        view!!.setBackgroundDrawable(shape)
    }

    fun setOriginalParams(params: LayoutParams) {
        originalParams = params
    }

    fun getOriginalParams(): LayoutParams {
        return originalParams!!
    }

    private fun lightDominant() {
        view!!.setBackgroundColor(Color.BLACK)
    }

    private fun darkDominant() {
        view!!.setBackgroundColor(Color.WHITE)
    }

    fun getThis():View {
        return view!!
    }

    /*
        Set the appearance of the view based on the theme
        of the operating system
     */
    fun setStyle() {
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
    }
}