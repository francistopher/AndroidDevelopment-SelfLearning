package com.example.savethecat_colormatching.ParticularViews

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import com.example.savethecat_colormatching.MainActivity

class GameResults(resultsView: View,
                  parentLayout: AbsoluteLayout,
                  params: LayoutParams) {

    private var originalParams:LayoutParams? = null
    private var resultsView:View? = null
    private var resultsContext: Context? = null

    private var parentLayout:AbsoluteLayout? = null

    companion object {
        var savedCatButtonsCount:Int = 0
        var deadCatButtonsCount:Int = 0
    }

    init {
        this.resultsView = resultsView
        this.resultsContext = resultsView.context
        this.resultsView!!.layoutParams = params
        setupOriginalParams(params = params)
        this.parentLayout = parentLayout
        parentLayout.addView(resultsView)
        setStyle()
        setCornerRadiusAndBorderWidth(radius = params.height / 2,
            borderWidth = params.height / 36)
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
        shape!!.cornerRadii = floatArrayOf(radius.toFloat(), radius.toFloat(),
            radius.toFloat(), radius.toFloat(), (MainActivity.dUnitHeight * 8.0 / 7.0).toFloat(),
            (MainActivity.dUnitHeight * 8.0 / 7.0).toFloat(),
            (MainActivity.dUnitHeight * 8.0 / 7.0).toFloat(),
            (MainActivity.dUnitHeight * 8.0 / 7.0).toFloat())
        this.resultsView!!.setBackgroundDrawable(shape)
    }

    fun getThis():View {
        return resultsView!!
    }

    private fun setupOriginalParams(params:LayoutParams) {
        originalParams = params
    }

    fun getOriginalParams():LayoutParams {
        return originalParams!!
    }

    private fun lightDominant() {
        resultsView!!.setBackgroundColor(Color.BLACK)
    }

    private fun darkDominant() {
        resultsView!!.setBackgroundColor(Color.WHITE)
    }

    fun setStyle() {
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
    }

    private fun setGameResults() {

    }

    private fun resetGameResults() {

    }
}