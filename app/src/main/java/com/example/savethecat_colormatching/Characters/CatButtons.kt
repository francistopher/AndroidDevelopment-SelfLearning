package com.example.savethecat_colormatching.Characters

import android.widget.AbsoluteLayout
import android.widget.Button
import java.lang.Math.sqrt

class CatButtons {

    private var currentCatButtons:MutableList<CatButton>? = null
    private var previousCatButtons:MutableList<CatButton>? = null

    init {
        currentCatButtons = mutableListOf()
        previousCatButtons = mutableListOf()
    }

    private var catButton:CatButton? = null
    fun buildCatButton(button: Button, parentLayout: AbsoluteLayout,
                       params: AbsoluteLayout.LayoutParams, backgroundColor:Int): CatButton {
        catButton = CatButton(button=button, parentLayout=parentLayout, params=params,
            backgroundColor = backgroundColor)
        catButton!!.setCornerRadiusAndBorderWidth((params.height.toDouble() / 5.0).toInt(),
            ((kotlin.math.sqrt(params.width * 0.01) * 10.0) * 0.35).toInt())
        // If cat button is pressed fade out cat button
        currentCatButtons!!.add(catButton!!)
        return catButton!!
    }

    fun loadPreviousCats() {
        for (catButton in currentCatButtons!!) {
            previousCatButtons!!.add(catButton)
        }
    }

    fun setStyle() {
        for (catButton in currentCatButtons!!) {
            catButton.setStyle()
        }
    }

    fun getCurrentCatButtons() :MutableList<CatButton> {
        return currentCatButtons!!
    }
}