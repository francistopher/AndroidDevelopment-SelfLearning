package com.example.savethecat_colormatching.ParticularViews

import android.util.Log
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams

class BoardGame(boardView: View, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var boardView: View? = null
    private var originalParams: LayoutParams? = null

    private var currentStage:Int = 1
    private  var gridColors: Array<IntArray>? = null

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
        buildGridColors()
        buildGridButtons()
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

    var gridColorRowIndex:Int = 0
    var gridColorColumnIndex:Int = 0
    var randomGridColor:Int = 0
    var previousGridColumnColor:Int = 0
    var previousGridRowColor:Int = 0
    private fun buildGridColors() {
        gridColors = Array(rowsAndColumns.first){ IntArray(rowsAndColumns.second) }
        gridColorRowIndex = 0
        while (gridColorRowIndex < gridColors!!.size) {
            gridColorColumnIndex = 0
            while(gridColorColumnIndex < gridColors!![0].size) {
                randomGridColor = ColorOptions.selectionColors!!.random()
                if (gridColorRowIndex > 0) {
                    previousGridColumnColor = gridColors!![gridColorRowIndex - 1][gridColorColumnIndex]
                    if (previousGridColumnColor == randomGridColor) {
                        gridColorRowIndex -= 1
                    }
                }
                if (gridColorColumnIndex > 0) {
                    previousGridRowColor = gridColors!![gridColorRowIndex][gridColorColumnIndex - 1]
                    if (previousGridRowColor == randomGridColor && (0..1).random() == 0) {
                        gridColorColumnIndex -= 1
                    }
                }
                gridColors!![gridColorRowIndex][gridColorColumnIndex] = randomGridColor
                gridColorColumnIndex += 1
            }
            gridColorRowIndex += 1
        }
    }

    var gridButtonRowGap:Float = 0.0f
    var gridButtonColumnGap:Float = 0.0f
    var gridButtonHeight:Float = 0.0f
    var gridButtonWidth:Float = 0.0f
    var gridButtonX:Float = 0.0f
    var gridButtonY:Float = 0.0f
    private fun buildGridButtons() {
        gridButtonRowGap = originalParams!!.height * 0.1f / (rowsAndColumns.first + 1.0f)
        gridButtonColumnGap = originalParams!!.width * 0.1f / (rowsAndColumns.second + 1.0f)
        // Sizes
        gridButtonHeight = originalParams!!.width * 0.9f / rowsAndColumns.first.toFloat()
        gridButtonWidth = originalParams!!.height * 0.9f / rowsAndColumns.second.toFloat()
        // Points
        gridButtonX = 0.0f
        gridButtonY = 0.0f
        // Build the cat buttons
        for (rowIndex in (0 until rowsAndColumns.first)) {
            gridButtonY += gridButtonRowGap
            gridButtonX = 0.0f
            for (columnIndex in (0 until rowsAndColumns.second)) {
                gridButtonX += gridButtonColumnGap
                Log.i("Coordinates", " ${gridButtonX} ${gridButtonY}")

//                gridCatButton!.rowIndex = rowIndex
//                gridCatButton!.columnIndex = columnIndex
//                gridCatButton!.imageContainerButton!.backgroundColor = UIColor.clear;
//                gridCatButton!.imageContainerButton!.addTarget(self, action: #selector(selectCatImageButton), for: .touchUpInside);
//                gridCatButton!.addTarget(self, action: #selector(selectCatButton), for: .touchUpInside);
                gridButtonX += gridButtonWidth
            }
            gridButtonY += gridButtonHeight
        }
    }
}
