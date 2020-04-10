package com.example.savethecat_colormatching.ParticularViews

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import android.widget.ImageButton
import com.example.savethecat_colormatching.Characters.CatButton
import com.example.savethecat_colormatching.Characters.CatButtons
import com.example.savethecat_colormatching.Controllers.AudioController
import com.example.savethecat_colormatching.CustomViews.CButton
import com.example.savethecat_colormatching.CustomViews.ShrinkType
import com.example.savethecat_colormatching.MainActivity
import java.util.*


class BoardGame(boardView: View, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var boardView: View? = null
    private var originalParams: LayoutParams? = null

    private var currentStage:Int = 1
    private var gridColors: Array<IntArray>? = null

    private var catButtons:CatButtons? = null
    private var gridColorsCount:MutableMap<Int,Int>? = null

    companion object {
        var rowsAndColumns = Pair(0, 0)
        var boardGameContext:Context? = null
        var boardGameLayout:AbsoluteLayout? = null
        var singlePlayerButton:CButton? = null
        var multiplayerButton:CButton? = null
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
        catButtons!!.removeAll()
        buildGridColors()
        buildGridButtons()
        catButtons!!.loadPreviousCats()
        recordGridColorsUsed()
    }

    fun getGridColorsCount(): MutableMap<Int, Int> {
        return gridColorsCount!!
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

    private var gridButtonRowGap:Float = 0.0f
    private var gridButtonColumnGap:Float = 0.0f
    private var gridButtonHeight:Float = 0.0f
    private var gridButtonWidth:Float = 0.0f
    private var gridButtonX:Float = 0.0f
    private var gridButtonY:Float = 0.0f
    private var catButton:CatButton? = null
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
                catButton = catButtons!!.buildCatButton(imageButton = ImageButton(boardGameContext!!),
                    parentLayout = boardGameLayout!!, params = LayoutParams(gridButtonWidth.toInt(),
                        gridButtonHeight.toInt(), (gridButtonX + originalParams!!.x).toInt(),
                        (gridButtonY + originalParams!!.y).toInt()),
                    backgroundColor = gridColors!![rowIndex][columnIndex])
                catButton!!.rowIndex = rowIndex
                catButton!!.columnIndex = columnIndex
                catButton!!.getThis().setOnClickListener {
                    catButtonSelector(params = (it as View).layoutParams as LayoutParams)
                }
                catButton!!.shrunk()
                catButton!!.grow()
                catButton!!.fade(true, false, 0.5f, 0.125f)
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

    private fun catButtonSelector(params:LayoutParams) {
        // The user has selected a color option
        if (MainActivity.colorOptions!!.getSelectedColor() != Color.LTGRAY) {
            for (catButton in catButtons!!.getCurrentCatButtons()) {
                // The button is found and colors match
                if (catButton.getOriginalParams() == params && !catButton.isPodded) {
                    if (MainActivity.colorOptions!!.getSelectedColor() ==
                        catButton.getOriginalBackgroundColor()) {

                        catButton.transitionColor(catButton.getOriginalBackgroundColor())
                        gridColorsCount!![catButton.getOriginalBackgroundColor()] =
                            gridColorsCount!![catButton.getOriginalBackgroundColor()]!!.minus(1)
                        MainActivity.colorOptions!!.buildColorOptionButtons(setup = false)
                        catButton.pod()
                    } else {
                        attackCatButton(catButton = catButton)
                        displaceArea(catButton = catButton)
                    }
                    verifyRemainingCatsArePodded()
                    return
                } else if (catButton.getOriginalParams() == params && catButton.isPodded) {
                    AudioController.kittenMeow()
                    return
                }
            }

        }
    }

    private var rowOfAliveCats:MutableList<CatButton>? = null
    private fun displaceArea(catButton: CatButton){
        rowOfAliveCats = catButtons!!.getRowOfAliveCats(rowIndex = catButton.rowIndex)
        // Row is still occupied
        if (rowOfAliveCats!!.size > 0) {
            disperseRow(aliveCats = rowOfAliveCats!!)
        } else {
            disperseColumns()
        }
    }

    private var x:Float = 0f
    private var y:Float = 0f
    private var columnGap:Float = 0f
    private var buttonWidth:Float = 0f
    private fun disperseRow(aliveCats:MutableList<CatButton>) {
        x = 0f
        y = aliveCats[0].getOriginalParams().y.toFloat()
        columnGap = originalParams!!.width * 0.1f / (aliveCats.size + 1).toFloat()
        buttonWidth = originalParams!!.height * 0.9f / (aliveCats.size).toFloat()
        for (aliveCat in aliveCats) {
            x += columnGap
            aliveCat.transformTo(LayoutParams(buttonWidth.toInt(), aliveCat.getOriginalParams().height,
                (originalParams!!.x + x).toInt(), y.toInt()))
            x += buttonWidth
        }
    }

    private var rowIndexCatAliveCount:MutableMap<Int, Int>? = null
    private var rowsLeftCount:Int = 0
    private var maxCatsInRowCount:Int = 0
    private fun disperseColumns() {
        rowIndexCatAliveCount = catButtons!!.getRowIndexAliveCatCount()
        // No rows, cancel the operation
        rowsLeftCount = rowIndexCatAliveCount!!.size
        if (rowsLeftCount == 0) {
            return
        }
        // Max count of cats in row
        maxCatsInRowCount = rowIndexCatAliveCount!!.maxBy { it.value }!!.value
        y = originalParams!!.y.toFloat()
        gridButtonRowGap = (originalParams!!.height * 0.1f) / (rowsLeftCount.toFloat() + 1f)
        gridButtonHeight = (originalParams!!.width * 0.9f) / rowsLeftCount.toFloat()

        fun resetCatButtonsPosition(rowIndex:Int) {
            y += gridButtonRowGap
            for (catButton in catButtons!!.getRowOfAliveCats(rowIndex = rowIndex)) {
                catButton.transformTo(LayoutParams(catButton.getOriginalParams().width,
                gridButtonHeight.toInt(), catButton.getOriginalParams().x, y.toInt()))
            }
            y += gridButtonHeight
        }

        if (maxCatsInRowCount <= rowsLeftCount) {
            for (rowIndex in rowIndexCatAliveCount!!.keys.sorted()) {
                resetCatButtonsPosition(rowIndex)
            }
        } else {
            gridButtonRowGap = (originalParams!!.height * 0.1f) / (rowsLeftCount.toFloat() + 1f)
            gridButtonHeight = (originalParams!!.width * 0.9f) / rowsLeftCount.toFloat()
            for (rowIndex in rowIndexCatAliveCount!!.keys.sorted()) {
                resetCatButtonsPosition(rowIndex)
            }
        }
    }

    private fun attackCatButton(catButton: CatButton) {
        catButton.disperseRadially()
        gridColorsCount!![catButton.getOriginalBackgroundColor()] =
            gridColorsCount!![catButton.getOriginalBackgroundColor()]!!.minus(1)
        MainActivity.colorOptions!!.buildColorOptionButtons(setup = false)
    }

    private fun verifyRemainingCatsArePodded() {
        if (catButtons!!.areAliveAndPodded()) {
            AudioController.heaven()
            MainActivity.successGradientView!!.alpha = 1f
            MainActivity.colorOptions!!.resetSelectedColor()
            when {
                catButtons!!.allSurvived() -> {
                    promote()
                }
                catButtons!!.areDead() -> {
                    maintain()
                }
                else -> {
                    maintain()
                }
            }
        }
    }

    private var countOfAliveCatButtons:Int = 0
    private var newRound:Int = 0
    private var product:Int = 0
    private var newRowsAndColumns:Pair<Int,Int>? = null
    private fun maintain() {
        countOfAliveCatButtons = catButtons!!.aliveCount()
        newRound = 1
        while (true) {
            newRowsAndColumns = getRowsAndColumns(newRound)
            product = newRowsAndColumns!!.first * newRowsAndColumns!!.second
            if (countOfAliveCatButtons < product) {
                currentStage = newRound - 1
                break
            }
            newRound += 1
        }
        reset(true)
        MainActivity.colorOptions!!.shrinkAllColorOptionButtons()
        MainActivity.colorOptions!!.loadSelectionToSelectedButtons()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                MainActivity.staticSelf!!.runOnUiThread {
                    buildGame()
                    startGame()
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            MainActivity.staticSelf!!.runOnUiThread {
                                MainActivity.successGradientView!!.alpha = 0f
                            }
                        }
                    }, 1125)
                }
            }
        }, 1250)
    }

    private fun promote() {
        reset(true)
        MainActivity.colorOptions!!.shrinkAllColorOptionButtons()
        MainActivity.colorOptions!!.loadSelectionToSelectedButtons()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                MainActivity.staticSelf!!.runOnUiThread {
                    currentStage += 1
                    buildGame()
                    startGame()
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            MainActivity.staticSelf!!.runOnUiThread {
                                MainActivity.successGradientView!!.alpha = 0f
                            }
                        }
                    }, 1125)
                }
            }
        }, 1250)
    }

    private fun reset(allSurvived:Boolean) {
        if (allSurvived) {
            catButtons!!.disperseVertically()
        }
        gridColors = null
        catButtons!!.removeAll()
    }

    private var recordedColor:Int = 0
    private fun recordGridColorsUsed() {
        if (gridColorsCount == null) {
            gridColorsCount = mutableMapOf()
        } else {
           gridColorsCount!!.clear()
        }
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
            dUnitHeight * 1.5 * 0.475) + (originalParams!!.height * 0.1)).toInt()))
        singlePlayerButton!!.setCornerRadiusAndBorderWidth((singlePlayerButton!!.
        getOriginalParams().height / 5.0).toInt(), ((kotlin.math.sqrt(singlePlayerButton!!.
        getOriginalParams().width * 0.01) * 10.0) * 0.65).toInt())
        singlePlayerButton!!.setTextSize((singlePlayerButton!!.getOriginalParams().height * 0.175).
        toFloat())
        singlePlayerButton!!.setText("Single Player", false)
        singlePlayerButton!!.getThis().setOnClickListener {
            if (!singlePlayerButton!!.growWidthAndChangeColorIsRunning) {
                singlePlayerButton!!.targetBackgroundColor = gridColors!![0][0]
                singlePlayerButton!!.growWidth((originalParams!!.width * 0.9).toFloat())
                multiplayerButton!!.shrink()
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        MainActivity.staticSelf!!.runOnUiThread {
                            MainActivity.successGradientView!!.alpha = 0f
                            startGame()
                        }
                    }
                }, 1250)
            }
        }
        singlePlayerButton!!.shrunk()
        singlePlayerButton!!.grow()
        singlePlayerButton!!.fade(true, false, 0.5f, 0.125f)
    }

    fun startGame() {
        MainActivity.colorOptions!!.buildColorOptionButtons(setup = true)
    }

    fun setupTwoPlayerButton() {
        multiplayerButton = CButton(button = Button(boardGameContext), parentLayout = boardGameLayout!!,
            params = LayoutParams((originalParams!!.width * 0.425).toInt(), (MainActivity.dUnitHeight
                    * 1.5 * 0.8).toInt(), (originalParams!!.x + (originalParams!!.width * 0.525)).
            toInt(), (originalParams!!.y + (-MainActivity.dUnitHeight * 1.5 * 0.475) + originalParams!!.
            height + (originalParams!!.height * 0.1)).toInt()))
        multiplayerButton!!.setCornerRadiusAndBorderWidth((multiplayerButton!!.
        getOriginalParams().height / 5.0).toInt(), ((kotlin.math.sqrt(multiplayerButton!!.
        getOriginalParams().width * 0.01) * 10.0) * 0.65).toInt())
        multiplayerButton!!.setTextSize((multiplayerButton!!.getOriginalParams().height * 0.175).
        toFloat())
        multiplayerButton!!.shrinkType = ShrinkType.right
        multiplayerButton!!.setText("Multi Player", false)
        multiplayerButton!!.getThis().setOnClickListener {
            Log.i("Click", "Multi Player Button")
        }
        multiplayerButton!!.shrunk()
        multiplayerButton!!.grow()
        multiplayerButton!!.fade(true, false, 0.5f, 0.125f)
    }

    private var nonZeroCount:Int = 0
    fun nonZeroGridColorsCount():Int {
        nonZeroCount = 0
        for ((_, count) in gridColorsCount!!) {
            if (count > 0) {
                nonZeroCount += 1
            }
        }
        return nonZeroCount
    }

    fun setButtonsBackgroundColorTransparent() {
        catButtons!!.setBackgroundTransparent()
    }
}
