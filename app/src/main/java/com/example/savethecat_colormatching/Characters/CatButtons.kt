package com.example.savethecat_colormatching.Characters

import android.animation.ValueAnimator
import android.graphics.Color
import android.util.Log
import android.widget.AbsoluteLayout
import android.widget.Button
import android.widget.ImageButton
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import java.lang.Math.sqrt

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
        catButton = CatButton(imageButton=imageButton, parentLayout=parentLayout, params=params,
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

    fun setBackgroundTransparent() {
        for (catButton in previousCatButtons!!) {
            catButton.transitionColor(Color.TRANSPARENT)
        }
    }

    fun getCurrentCatButtons() :MutableList<CatButton> {
        return currentCatButtons!!
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

    fun allSurvived():Boolean {
        for (catButton in currentCatButtons!!) {
            if (!catButton.isAlive) {
                return false
            }
        }
        return true
    }
}