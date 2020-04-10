package com.example.savethecat_colormatching.ParticularViews

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams

class LivesMeter(meterView: View, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var originalParams:LayoutParams? = null
    private var meterContext: Context? = null
    private var meterLayout:AbsoluteLayout? = null
    private var meterView:View? = null

    init {
        this.meterView = meterView
        meterContext = meterView.context
        meterLayout = AbsoluteLayout(meterContext)
        this.meterView!!.layoutParams = params
        parentLayout.addView(this.meterView!!)
        setOriginalParams(params = params)
        this.meterView!!.setBackgroundColor(Color.RED)
    }

    fun setOriginalParams(params: LayoutParams) {
        originalParams = params
    }

    fun getOriginalParams():LayoutParams {
        return originalParams!!
    }

    fun getThis():View {
        return this.meterView!!
    }
}