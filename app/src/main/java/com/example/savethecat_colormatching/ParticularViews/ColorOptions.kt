package com.example.savethecat_colormatching.ParticularViews

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import com.example.savethecat_colormatching.R

class ColorOptions(view: View, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var colors = mutableListOf(green, yellow, orange, red, purple, blue)

    private var view:View? = null

    private var originalParams:LayoutParams? = null
    private var shrunkParams:LayoutParams? = null

    init {
        this.view = view
        this.view!!.layoutParams = params
        parentLayout.addView(view)
        setOriginalParams(params)
        setShrunkParams()
        this.view!!.setBackgroundColor(Color.RED)
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

    fun getThis():View {
        return this.view!!
    }

    companion object {
        var selectionColors:MutableList<Int>? = null

        var green:Int = R.color.Green
        var yellow:Int = R.color.Yellow
        var orange:Int = R.color.Orange
        var red:Int = R.color.Red
        var purple:Int = R.color.Purple
        var blue:Int = R.color.Blue

        fun setSelectionColors() {
            selectionColors = mutableListOf(green, yellow,
                orange, red, purple, blue)
            do {
                selectionColors!!.removeAt((0 until selectionColors!!.size).random())
                if (selectionColors!!.size == BoardGame.rowsAndColumns.first || selectionColors!!.size == 6) {
                    break
                }
            } while (true)
        }

    }
}