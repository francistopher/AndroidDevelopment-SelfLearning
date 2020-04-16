package com.example.savethecat_colormatching.ParticularViews

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import com.example.savethecat_colormatching.CustomViews.CButton
import com.example.savethecat_colormatching.CustomViews.ShrinkType
import com.example.savethecat_colormatching.MainActivity

class ColorOptions(view: View, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var view:View? = null

    private var originalParams:LayoutParams? = null
    private var shrunkParams:LayoutParams? = null

    private var selectionButtons:MutableSet<CButton>? = null
    private var selectedButtons:MutableSet<CButton>? = null

    private var selectedColor:Int = Color.LTGRAY

    companion object {
        var selectionColors:MutableList<Int>? = null

        var green = Color.rgb(48, 209, 88)
        var yellow = Color.rgb(255, 214, 10)
        var orange = Color.rgb(255, 159, 10)
        var red = Color.rgb(255, 69, 58)
        var purple = Color.rgb(191, 90, 242)
        var blue = Color.rgb(10, 132, 255)
        var pink = Color.rgb(255, 55, 95)

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
    private var newParams:LayoutParams? = null
    private var index:Int = 0
    private var count:Int = 0
    fun buildColorOptionButtons(setup:Boolean) {
        numOfUniqueColors = MainActivity.boardGame!!.nonZeroGridColorsCount()
        columnGap = (originalParams!!.width * 0.1f) / (numOfUniqueColors + 1).toFloat()
        rowGap = originalParams!!.height * 0.1f
        buttonWidth = (originalParams!!.width - (columnGap * (numOfUniqueColors + 1).toFloat())) /
                numOfUniqueColors.toFloat()
        buttonHeight = (originalParams!!.height -  (rowGap * 2.0)).toFloat()
        button = null
        x = originalParams!!.x.toFloat()
        // Rebuilding
        index = 0
        count = 0
        for ((color, colorCount) in MainActivity.boardGame!!.getGridColorsCount()) {
            if (setup) {
                x += columnGap
                button = CButton(button = Button(colorOptionsContext!!), parentLayout =
                colorOptionsLayout!!, params = LayoutParams(buttonWidth.toInt(), buttonHeight.toInt(),
                    x.toInt(), (originalParams!!.y + rowGap * 0.9).toInt()))
                if (numOfUniqueColors != 1 || BoardGame.singlePlayerButton!!.getThis().alpha == 0f) {
                    button!!.shrunk()
                    button!!.grow()
                    button!!.fade(In = true, Out = false, Duration = 0.0f, Delay = 0.125f)
                } else {
                    button!!.getThis().alpha = 1f
                }
                button!!.backgroundColor = color
                button!!.setStyle()
                button!!.setCornerRadiusAndBorderWidth((button!!.getOriginalParams().height / 5.0f).toInt(),
                    (kotlin.math.sqrt(button!!.getOriginalParams().width * 0.01) * 4.5).toInt())
                selectionButtons!!.add(button!!)
                x += buttonWidth
                button!!.getThis().setOnClickListener {
                    colorOptionSelector(color = color)
                }
            } else {
                button = selectionButtons!!.elementAt(index)
                if (colorCount != 0) {
                    x += columnGap
                    if (button!!.isSelected) {
                        Log.i("Color $color", "Count $colorCount")
                        newParams = LayoutParams(buttonWidth.toInt(), (buttonHeight * 1.275).toInt(),
                            x.toInt(), (originalParams!!.y + (rowGap * 0.9) - (buttonHeight * 0.1375)).toInt())
                        button!!.setOriginalParams(newParams!!)
                        button!!.select(targetX = x, targetWidth = buttonWidth)
                    } else {
                        newParams = LayoutParams(buttonWidth.toInt(), buttonHeight.toInt(),
                            x.toInt(), (originalParams!!.y + rowGap * 0.9).toInt())
                        button!!.setOriginalParams(newParams!!)
                        button!!.unSelect()
                    }
                    x += buttonWidth
                } else {
                    if (button!!.isSelected) {
                        button!!.willBeShrunk = true
                        for (selectionButton in selectionButtons!!) {
                            if (!selectionButton.willBeShrunk) {
                                // Select the first element
                                selectedColor = selectionButton.backgroundColor!!
                                selectionButton.isSelected = true
                                selectionButton.select(targetX = -1f, targetWidth = -1f)
                                break
                            }
                        }
                    }
                    this.count += 1
                    if (numOfUniqueColors + 1 == 1) {
                        button!!.shrinkType = ShrinkType.mid
                    } else if (numOfUniqueColors + 1 == 2) {
                        if (this.count > index) {
                            button!!.shrinkType = ShrinkType.left
                        } else {
                            button!!.shrinkType = ShrinkType.right
                        }
                    } else {
                        if (this.count > index) {
                            button!!.shrinkType = ShrinkType.left
                        } else if (index <= numOfUniqueColors - 1) {
                            button!!.shrinkType = ShrinkType.mid
                        } else {
                            button!!.shrinkType = ShrinkType.right
                        }
                    }
                    button!!.shrink()
                }
                index += 1
            }
        }
    }

    private fun colorOptionSelector(color: Int) {
        val catButtonParams:LayoutParams = MainActivity.boardGame!!.getCatButtons().
        getCurrentCatButtons()[0].getOriginalParams()
        val x:Int = catButtonParams.x + (catButtonParams.width * 0.35).toInt()
        val y:Int = catButtonParams.y + (catButtonParams.height * 0.35).toInt()
        MainActivity.glovePointer!!.translate(x, y)
        clearBoardGameGridButtonsColorIndicator()
        if (color != selectedColor) {
            for (selectionButton in selectionButtons!!) {
                if (color == selectionButton.backgroundColor) {
                    selectedColor = color
                    selectionButton.isSelected = true
                    selectionButton.select(targetX = -1f, targetWidth = -1f)
                } else {
                    selectedColor = color
                    selectionButton.isSelected = false
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