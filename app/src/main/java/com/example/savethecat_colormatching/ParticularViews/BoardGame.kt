package com.example.savethecat_colormatching.ParticularViews

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import com.example.savethecat_colormatching.Characters.CatButton
import com.example.savethecat_colormatching.Characters.CatButtons
import com.example.savethecat_colormatching.CustomViews.CButton
import com.example.savethecat_colormatching.CustomViews.ShrinkType
import com.example.savethecat_colormatching.MainActivity

class BoardGame(boardView: View, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var boardView: View? = null
    private var originalParams: LayoutParams? = null

    private var currentStage:Int = 1
    private var gridColors: Array<IntArray>? = null

    private var catButtons:CatButtons? = null
    private var singlePlayerButton:CButton? = null
    private var twoPlayerButton:CButton? = null

    companion object {
        var rowsAndColumns = Pair(0, 0)
        var boardGameContext:Context? = null
        var boardGameLayout:AbsoluteLayout? = null
        var gridColorsCount:MutableMap<Int,Int>? = null
    }
    init {
        this.boardView = boardView
        boardGameContext = boardView.context
        boardGameLayout = AbsoluteLayout(boardGameContext)
        this.boardView!!.layoutParams = params
        parentLayout.addView(this.boardView!!)
        setOriginalParams(params = params)
        catButtons = CatButtons()
    }

    fun getThis(): View {
        return this.boardView!!
    }

    fun setOriginalParams(params: LayoutParams) {
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
        catButtons!!.loadPreviousCats()
        recordGridColorsUsed()
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
    var catButton:CatButton? = null
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
                catButton = catButtons!!.buildCatButton(button = Button(boardGameContext!!),
                    parentLayout = boardGameLayout!!, params = LayoutParams(gridButtonWidth.toInt(),
                        gridButtonHeight.toInt(), (gridButtonX + originalParams!!.x).toInt(),
                        (gridButtonY + originalParams!!.y).toInt()),
                    backgroundColor = gridColors!![rowIndex][columnIndex])
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

    var recordedColor:Int = 0
    private fun recordGridColorsUsed() {
        gridColorsCount = mutableMapOf()
        for (catButton in catButtons!!.getCurrentCatButtons()) {
            recordedColor = catButton.getOriginalBackgroundColor()
            if (gridColorsCount!![recordedColor] == null) {
                gridColorsCount!![recordedColor] = 1
            } else {
                gridColorsCount!![recordedColor] = gridColorsCount!![recordedColor]!! + 1
            }
        }
    }

    fun setupSinglePlayerButton() {
        singlePlayerButton = CButton(button = Button(boardGameContext), parentLayout = boardGameLayout!!,
            params = LayoutParams((originalParams!!.width * 0.425).toInt(), (MainActivity.dUnitHeight
                    * 1.5 * 0.8).toInt(), (originalParams!!.x + (originalParams!!.
            width * 0.05)).toInt(), (originalParams!!.y + originalParams!!.height + (-MainActivity.
            dUnitHeight * 1.5 * 0.4) + (originalParams!!.height * 0.1)).toInt()))
        singlePlayerButton!!.setCornerRadiusAndBorderWidth((singlePlayerButton!!.
        getOriginalParams().height / 5.0).toInt(), ((kotlin.math.sqrt(singlePlayerButton!!.
        getOriginalParams().width * 0.01) * 10.0) * 0.35).toInt())
        singlePlayerButton!!.setTextSize((singlePlayerButton!!.getOriginalParams().height * 0.175).
        toFloat())
        singlePlayerButton!!.setText("Single Player", false)
        singlePlayerButton!!.getThis().setOnClickListener {
            singlePlayerButton!!.targetBackgroundColor = gridColors!![0][0]
            singlePlayerButton!!.growWidth((originalParams!!.width * 0.9).toFloat())
            twoPlayerButton!!.shrink(false)
        }
    }

    fun setupTwoPlayerButton() {
        twoPlayerButton = CButton(button = Button(boardGameContext), parentLayout = boardGameLayout!!,
            params = LayoutParams((originalParams!!.width * 0.425).toInt(), (MainActivity.dUnitHeight
                    * 1.5 * 0.8).toInt(), (originalParams!!.x + (originalParams!!.width * 0.525)).
            toInt(), (originalParams!!.y + (-MainActivity.dUnitHeight * 1.5 * 0.4) +originalParams!!.
            height + (originalParams!!.height * 0.1)).toInt()))
        twoPlayerButton!!.setCornerRadiusAndBorderWidth((twoPlayerButton!!.
        getOriginalParams().height / 5.0).toInt(), ((kotlin.math.sqrt(twoPlayerButton!!.
        getOriginalParams().width * 0.01) * 10.0) * 0.35).toInt())
        twoPlayerButton!!.setTextSize((twoPlayerButton!!.getOriginalParams().height * 0.175).
        toFloat())
        twoPlayerButton!!.shrinkType = ShrinkType.right
        twoPlayerButton!!.setText("Multi Player", false)
        twoPlayerButton!!.getThis().setOnClickListener {
            Log.i("Click", "Multi Player Button")
        }
    }

    fun setStyle() {
        singlePlayerButton!!.setStyle()
        twoPlayerButton!!.setStyle()
        catButtons!!.setStyle()
    }
}
