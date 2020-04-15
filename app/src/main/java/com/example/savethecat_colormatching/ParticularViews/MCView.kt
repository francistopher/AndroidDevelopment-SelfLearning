package com.example.savethecat_colormatching.ParticularViews

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.TextView
import com.example.savethecat_colormatching.MainActivity

class MCView(textView: TextView, parentLayout: AbsoluteLayout,
             params: AbsoluteLayout.LayoutParams) {

    private var originalParams:LayoutParams? = null
    private var mouseCoinView:TextView? = null

    private var parentLayout:AbsoluteLayout? = null

    companion object {
        var mouseCoinCount:Int = 0
    }

    init {
        this.mouseCoinView = textView
        this.mouseCoinView!!.setBackgroundColor(Color.TRANSPARENT)
        setupOriginalParams(params = params)
        setupLayout(layout = parentLayout)
        setCornerRadiusAndBorderWidth((params.height / 2.0).toInt(),
            (params.height / 12.0).toInt())

    }

    private var shape: GradientDrawable? = null
    private var borderWidth:Int = 0
    private var cornerRadius:Int = 0
    private fun setCornerRadiusAndBorderWidth(radius: Int, borderWidth: Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        shape!!.setColor(Color.TRANSPARENT)
        if (borderWidth > 0) {
            this.borderWidth = borderWidth
            shape!!.setStroke(borderWidth, Color.parseColor("#ffd60a"))
        }
        cornerRadius = radius
        shape!!.cornerRadius = radius.toFloat()
        mouseCoinView!!.setBackgroundDrawable(shape)
    }

    fun displayCount() {
        setText(mouseCoinCount.toString())
    }

    fun increaseCount() {
        mouseCoinCount += 1
        displayCount()
    }

    fun decreaseCount() {
        if (mouseCoinCount > 0) {
            mouseCoinCount -= 1
            displayCount()
        }
    }

    private fun setText(text:String) {
        mouseCoinView!!.text = text
        mouseCoinView!!.typeface = Typeface.createFromAsset(
            MainActivity.rootView!!.context.assets,
            "SleepyFatCat.ttf"
        )
        mouseCoinView!!.gravity = Gravity.CENTER
    }

    fun setTextSize(size:Float) {
        mouseCoinView!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
        mouseCoinView!!.setTextColor(Color.parseColor("#ffd60a"))
    }

    private fun setupOriginalParams(params:LayoutParams) {
        this.mouseCoinView!!.layoutParams = params
        this.originalParams = params
    }

    fun getOriginalParams():LayoutParams {
        return originalParams!!
    }

    private fun setupLayout(layout:AbsoluteLayout) {
        layout.addView(this.mouseCoinView!!)
        this.parentLayout = layout
    }
}