package com.example.savethecat_colormatching.ParticularViews

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import com.example.savethecat_colormatching.CustomViews.CButton
import com.example.savethecat_colormatching.MainActivity

class ColorOptions(view: View, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var colors = mutableListOf(green, yellow, orange, red, purple, blue)

    private var view:View? = null

    private var originalParams:LayoutParams? = null
    private var shrunkParams:LayoutParams? = null

    private var selectionButtons:MutableSet<CButton>? = null
    private var selectedButtons:MutableSet<CButton>? = null

    private var selectedColor:Int = Color.LTGRAY

    companion object {
        var selectionColors:MutableList<Int>? = null

        var green = Color.rgb(48, 209, 88)
        var yellow = Color.rgb(255, 114, 10)
        var orange = Color.rgb(255, 159, 10)
        var red = Color.rgb(255, 69, 58)
        var purple = Color.rgb(191, 90, 242)
        var blue = Color.rgb(10, 132, 255)

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

        var colorOptionsContext: Context? = null
        var colorOptionsLayout:AbsoluteLayout? = null
    }

    init {
        this.view = view
        colorOptionsContext = view.context
        colorOptionsLayout = AbsoluteLayout(colorOptionsContext)
        this.view!!.layoutParams = params
        parentLayout.addView(view)
        setOriginalParams(params)
        setShrunkParams()
        this.view!!.setBackgroundColor(Color.TRANSPARENT)
        selectionButtons = mutableSetOf()
        selectedButtons = mutableSetOf()
    }

    fun setOriginalParams(params:LayoutParams) {
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

    // Button parameters
    private var numOfUniqueColors:Int = 0
    private var columnGap:Float = 0f
    private var rowGap:Float = 0f
    private var buttonWidth:Float = 0f
    private var buttonHeight:Float = 0f
    // Button
    private var button:CButton? = null
    private var x:Float = 0f
    fun buildColorOptionButtons() {
        numOfUniqueColors = MainActivity.boardGame!!.nonZeroGridColorsCount()
        columnGap = (originalParams!!.width * 0.1f) / (numOfUniqueColors + 1).toFloat()
        rowGap = originalParams!!.height * 0.1f
        buttonWidth = (originalParams!!.width - (columnGap * (numOfUniqueColors + 1).toFloat())) /
                numOfUniqueColors.toFloat()
        buttonHeight = (originalParams!!.height -  (rowGap * 2.0)).toFloat()
        button = null
        x = originalParams!!.x.toFloat()
        Log.i("Number of unique colors", "$numOfUniqueColors")
        for ((color, count) in MainActivity.boardGame!!.getGridColorsCount()) {
            x += columnGap
            button = CButton(button = Button(colorOptionsContext!!), parentLayout =
            colorOptionsLayout!!, params = LayoutParams(buttonWidth.toInt(), buttonHeight.toInt(),
                x.toInt(), (originalParams!!.y + rowGap * 0.9).toInt()))
            button!!.backgroundColor = color
            button!!.setStyle()
            button!!.getThis().setOnClickListener {
                colorOptionSelector(color = color)
            }
            button!!.setCornerRadiusAndBorderWidth(((kotlin.math.sqrt(button!!.getOriginalParams().
            width * 0.01) * 10.0) * 0.75).toInt(),  (button!!.getOriginalParams().height / 20.0).toInt())
            selectionButtons!!.add(button!!)
            x += buttonWidth
        }
    }

    private fun colorOptionSelector(color: Int) {
        clearBoardGameGridButtonsColorIndicator()
        for (selectionButton in selectionButtons!!) {
            if (color != selectedColor) {
                if (color == selectionButton.backgroundColor) {
                    selectedColor = color
                    selectionButton.select()
                } else {
                    selectedColor = color
                    selectionButton.unSelect()
                }
            }
        }
    }

    fun shrinkAllColorOptionButtons() {
        for (selectionButton in selectionButtons!!) {
            selectionButton.shrink()
        }
    }

    private fun clearBoardGameGridButtonsColorIndicator() {
        if (selectedColor == Color.LTGRAY) {
            MainActivity.boardGame!!.setButtonsBackgroundColorTransparent()
        }
    }

    fun getSelectedColor():Int {
        return selectedColor
    }

    fun resetSelectedColor() {
        selectedColor = Color.LTGRAY
    }

    fun loadSelectionToSelectedButtons() {
        for (selectionButton in selectionButtons!!) {
            selectedButtons!!.add(selectionButton)
        }
        selectionButtons!!.clear()
    }
}