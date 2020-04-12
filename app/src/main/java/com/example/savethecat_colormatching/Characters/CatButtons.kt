package com.example.savethecat_colormatching.Characters

import android.graphics.Color
import android.widget.AbsoluteLayout
import android.widget.ImageButton

class CatButtons {

    private var currentCatButtons:MutableList<CatButton>? = null
    private var previousCatButtons:MutableList<CatButton>? = null

    init {
        currentCatButtons = mutableListOf()
        previousCatButtons = mutableListOf()
    }

    private var catButton:CatButton? = null
    fun buildCatButton(imageButton: ImageButton, parentLayout: AbsoluteLayout,
                       params: AbsoluteLayout.LayoutParams, backgroundColor:Int): CatButton {
        catButton = CatButton(imageButton= imageButton, parentLayout=parentLayout, params=params,
            backgroundColor = backgroundColor)
        catButton!!.setCornerRadiusAndBorderWidth((params.height.toDouble() / 5.0).toInt(),
            ((kotlin.math.sqrt(params.width * 0.01) * 10.0) * 0.45).toInt(), true)
        // If cat button is pressed fade out cat button
        currentCatButtons!!.add(catButton!!)
        return catButton!!
    }

    fun loadPreviousCats() {
        previousCatButtons!!.clear()
        for (catButton in currentCatButtons!!) {
            previousCatButtons!!.add(catButton)
        }
    }

    fun setBackgroundTransparent() {
        for (catButton in previousCatButtons!!) {
            catButton.transitionColor(Color.TRANSPARENT)
        }
    }

    fun areDead(): Boolean {
        for (catButton in currentCatButtons!!) {
            if (catButton.isAlive) {
                return false
            }
        }
        return true
    }

    fun getCurrentCatButtons(): MutableList<CatButton> {
        return currentCatButtons!!
    }

    fun randomLivingCatButton(): CatButton? {
        if (areDead()) {
            return null
        } else {
            return currentCatButtons!!.filter{ catButton -> (catButton.isAlive &&
                    !catButton.isPodded)}.random()
        }
    }

    fun areAliveAndPodded():Boolean {
        for (catButton in currentCatButtons!!) {
            if (catButton.isAlive && !catButton.isPodded) {
                return false
            }
        }
        return true
    }

    fun disperseVertically() {
        for (catButton in currentCatButtons!!) {
            if (catButton.isAlive) {
                catButton.disperseVertically()
            }
        }
    }

    var count:Int = 0
    fun aliveCount():Int {
        count = 0
        for (catButton in currentCatButtons!!) {
            if (catButton.isAlive) {
                count += 1
            }
        }
        return count
    }

    fun allSurvived():Boolean {
        for (catButton in currentCatButtons!!) {
            if (!catButton.isAlive) {
                return false
            }
        }
        return true
    }

    private var rowOfAliveCats:MutableList<CatButton>? = null
    fun getRowOfAliveCats(rowIndex:Int):MutableList<CatButton> {
        rowOfAliveCats = mutableListOf()
        for (catButton in currentCatButtons!!) {
            if (catButton.rowIndex == rowIndex && catButton.isAlive) {
                rowOfAliveCats!!.add(catButton)
            }
        }
        return rowOfAliveCats!!
    }

    fun removeAll() {
        currentCatButtons!!.clear()
    }

    var indexAliveCatCountMap:MutableMap<Int, Int>? = null
    fun getRowIndexAliveCatCount():MutableMap<Int, Int> {
        indexAliveCatCountMap = mutableMapOf()
        for (catButton in currentCatButtons!!) {
            if (getRowOfAliveCats(catButton.rowIndex).size > 0) {
                if (indexAliveCatCountMap!![catButton.rowIndex] == null) {
                    indexAliveCatCountMap!![catButton.rowIndex] = 1
                } else {
                    indexAliveCatCountMap!![catButton.rowIndex] = 1 +
                            indexAliveCatCountMap!![catButton.rowIndex]!!
                }
            }
        }
        return indexAliveCatCountMap!!
    }
}