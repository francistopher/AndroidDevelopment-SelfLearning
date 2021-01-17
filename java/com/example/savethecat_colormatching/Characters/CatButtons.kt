package com.example.savethecat_colormatching.Characters

import android.graphics.Color
import android.util.Log
import android.widget.AbsoluteLayout
import android.widget.ImageButton

class CatButtons {

    private var currentCatButtons:MutableList<CatButton>? = null
    private var previousCatButtons:MutableList<CatButton>? = null

    init {
        currentCatButtons = mutableListOf()
        previousCatButtons = mutableListOf()
    }

    /*
        Builds a cat button based on the parameters passed on and
        saves it to the current cat buttons, then returns it
     */
    private var catButton:CatButton? = null
    fun buildCatButton(imageButton: ImageButton, parentLayout: AbsoluteLayout,
                       params: AbsoluteLayout.LayoutParams, backgroundColor:Int): CatButton {
        catButton = CatButton(imageButton= imageButton, parentLayout=parentLayout, params=params,
            backgroundColor = backgroundColor)
        // If cat button is pressed fade out cat button
        currentCatButtons!!.add(catButton!!)
        return catButton!!
    }

    /*
        Removes the previous cat buttons and
        loads the current cat button to the previous cat buttons
     */
    fun loadPreviousCats() {
        previousCatButtons!!.clear()
        for (catButton in currentCatButtons!!) {
            previousCatButtons!!.add(catButton)
        }
    }

    /*
        Sets every cat button background color to transparent
     */
    fun setBackgroundTransparent() {
        for (catButton in previousCatButtons!!) {
            catButton.transitionColor(Color.TRANSPARENT)
        }
    }

    /*
        Returns a boolean to indicate whether or not
        all the cats are dead
     */
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

    /*
        Returns a random cat button that is not dead
     */
    fun randomLivingCatButton(): CatButton? {
        return if (areDead()) {
            null
        } else {
            currentCatButtons!!.filter{ catButton -> (catButton.isAlive &&
                    !catButton.isPodded)}.random()
        }
    }

    /*
        Returns the number of cat buttons that are dead
     */
    private var deadCount:Int = 0
    fun deadCount():Int {
        deadCount = 0
        for (catButton in currentCatButtons!!) {
            if (!catButton.isAlive) {
                deadCount += 1
            }
        }
        return deadCount
    }

    /*
        Returns a boolean indicating wether or not
        the cat buttons have been matched
     */
    fun areAliveAndPodded():Boolean {
        for (catButton in currentCatButtons!!) {
            if (catButton.isAlive && !catButton.isPodded) {
                return false
            }
        }
        return true
    }

    /*
        Translates all the cat buttons to the top edge
        of the screen
     */
    fun disperseVertically() {
        for (catButton in currentCatButtons!!) {
            if (catButton.isAlive) {
                catButton.disperseVertically()
            }
        }
    }

    /*
        Returns the total number of cat buttons
        that are alive in the current round
     */
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

    /*
        Returns a boolean indicating that ALL the cat buttons survived
     */
    fun allSurvived():Boolean {
        for (catButton in currentCatButtons!!) {
            if (!catButton.isAlive || !catButton.isPodded) {
                return false
            }
        }
        return true
    }

    /*
        Returns a row of cat buttons as a mutable list,
        the row is passed in as an int
     */
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

    /*
        Sets all the cat buttons of the current round
        as DEAD
     */
    fun setAllCatButtonsDead() {
        for (catButton in currentCatButtons!!) {
            if (catButton.isAlive) {
                catButton.disperseRadially()
            }
        }
    }

    /*
        Sets all the cat buttons that are alive and not matched as matched
        Disperses all the cat buttons that are alive to the top edge of the screen
     */
    fun saveAllCats() {
        for (catButton in currentCatButtons!!) {
            if (catButton.isAlive && !catButton.isPodded) {
                catButton.pod()
            }
            if (catButton.isAlive && catButton.cat != CatState.CHEERING) {
                catButton.disperseVertically()
            }
        }
    }

    /*
        Updates the cat type of all the current cat buttons
     */
    fun updateCatType(cat: Cat) {
        for (catButton in currentCatButtons!!) {
            Log.i("GATO", cat.toString())
            catButton.cat = cat
            catButton.setStyle()
        }
    }

    fun removeAll() {
        currentCatButtons!!.clear()
    }

    /*
        Returns the count of all the cats that are alive in a row
        given a row index
     */
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