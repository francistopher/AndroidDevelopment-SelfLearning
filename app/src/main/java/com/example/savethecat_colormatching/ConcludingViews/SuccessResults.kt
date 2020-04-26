package com.example.savethecat_colormatching.ConcludingViews

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.TextView
import com.example.savethecat_colormatching.CustomViews.CLabel
import com.example.savethecat_colormatching.MainActivity

class SuccessResults(successView: View,
                     parentLayout: AbsoluteLayout,
                     params: LayoutParams) {

    private var successView:View? = null
    private var context: Context? = null
    private var parentLayout:AbsoluteLayout? = null

    private var unitHeight:Int = 0

    private var originalParams:LayoutParams? = null

    private var successLabel:CLabel? = null

    init {
        setupView(successView)
        setupOriginalParams(params)
        setupParentLayout(parentLayout)
        setStyle()
        setCornerRadiusAndBorderWidth(radius = params.height / 5,
            borderWidth = params.height / 60)
    }

    fun setupContents() {
        setupTitleView()
    }

    private fun setupTitleView() {
        successLabel = CLabel(textView = TextView(context!!), parentLayout = parentLayout!!,
            params = LayoutParams((getOriginalParams().width * 0.5).toInt(), (unitHeight * 2.0).toInt(),
                (getOriginalParams().width * 0.25).toInt() + getOriginalParams().x,
                getOriginalParams().y + (unitHeight * 0.5).toInt()))
        successLabel!!.setTextSize(successLabel!!.getOriginalParams().height * 0.1875f)
        successLabel!!.setText("You Won!!!")
        successLabel!!.isInverted = true
        successLabel!!.setStyle()
        successLabel!!.setCornerRadiusAndBorderWidth((getOriginalParams().height / 2.0).toInt(),
            0)
    }

    private var shape: GradientDrawable? = null
    private var borderWidth:Int = 0
    private var cornerRadius:Int = 0
    private fun setCornerRadiusAndBorderWidth(radius: Int, borderWidth: Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        if (MainActivity.isThemeDark) {
            shape!!.setColor(Color.BLACK)
        } else {
            shape!!.setColor(Color.WHITE)
        }
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
        this.successView!!.setBackgroundDrawable(shape)
    }

    private fun setupParentLayout(parentLayout: AbsoluteLayout) {
        this.parentLayout = parentLayout
        parentLayout.addView(successView)
    }

    private fun setupView(view:View) {
        this.successView = view
        context = view.context
    }

    fun setupOriginalParams(params: LayoutParams) {
        unitHeight = (params.height / 8.0).toInt()
        successView!!.layoutParams = params
        originalParams = params
    }

    fun getThis():View {
        return successView!!
    }

    fun getOriginalParams():LayoutParams {
        return originalParams!!
    }

    private fun lightDominant() {
        successView!!.setBackgroundColor(Color.BLACK)
    }

    private fun darkDominant() {
        successView!!.setBackgroundColor(Color.WHITE)
    }

    fun setStyle() {
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
    }
}