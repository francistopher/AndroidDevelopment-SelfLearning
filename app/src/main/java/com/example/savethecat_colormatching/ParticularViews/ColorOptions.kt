package com.example.savethecat_colormatching.ParticularViews

import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import com.example.savethecat_colormatching.R

class ColorOptions(colorOptionView: View, parentLayout: AbsoluteLayout, params: LayoutParams) {
    private var colors = mutableListOf(green, yellow,
        orange, red, purple, blue)

    companion object {

        var selectionColors:MutableList<Int>? = null

        var green = R.color.Green
        var yellow = R.color.Yellow
        var orange = R.color.Orange
        var red = R.color.Red
        var purple = R.color.Purple
        var blue = R.color.Blue

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