package com.example.savethecat_colormatching.ParticularViews

import android.util.Log
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams

class BoardGame(boardView: View, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var boardView: View? = null
    private var originalParams: LayoutParams? = null

    private var currentStage:Int = 1

    companion object {
        var rowsAndColumns = Pair(0, 0)
    }
    init {
        this.boardView = boardView
        this.boardView!!.layoutParams = params
        parentLayout.addView(this.boardView!!)
        setOriginalParams(params = params)
    }

    fun getThis(): View {
        return this.boardView!!
    }

    private fun setOriginalParams(params: LayoutParams) {
        originalParams = params
    }

    fun getOriginalParams():LayoutParams {
        return originalParams!!
    }

    fun buildGame() {
        rowsAndColumns = getRowsAndColumns(currentStage = currentStage)
        ColorOptions.setSelectionColors()

        Log.i("Selected color", ColorOptions.selectionColors.toString())
    }

    private var initialStage:Int = 0
    private var rows:Int = 0
    private var columns:Int = 0
    private fun getRowsAndColumns(currentStage:Int): Pair<Int,Int> {
        initialStage = 2
        rows = 1
        columns = 1
        while (currentStage >= initialStage) {
            if (initialStage % 2 == 0) {
                rows += 1
            } else {
                columns += 1
            }
            initialStage += 1
        }
        return Pair(rows, columns)
    }
}
