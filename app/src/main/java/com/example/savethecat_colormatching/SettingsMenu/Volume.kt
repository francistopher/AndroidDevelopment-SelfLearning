package com.example.savethecat_colormatching.SettingsMenu

import android.graphics.Color
import android.widget.AbsoluteLayout
import android.widget.Button
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R

class Volume(button: Button, parentLayout: AbsoluteLayout, params: AbsoluteLayout.LayoutParams) {

    private var expandedParams: AbsoluteLayout.LayoutParams? = null
    private var contractedParams: AbsoluteLayout.LayoutParams? = null

    private var volumeButton: Button? = null

    init {
        this.volumeButton = button
        this.volumeButton!!.layoutParams = params
        parentLayout.addView(button)
        this.volumeButton!!.setBackgroundColor(Color.TRANSPARENT)
        setStyle()
    }

    fun getThis(): Button {
        return volumeButton!!
    }

    fun setContractedParams(params: AbsoluteLayout.LayoutParams) {
        contractedParams = params
    }

    fun setExpandedParams(params: AbsoluteLayout.LayoutParams) {
        expandedParams = params
    }

    fun getExpandedParams(): AbsoluteLayout.LayoutParams {
        return expandedParams!!
    }

    private fun lightDominant() {
        volumeButton!!.setBackgroundResource(R.drawable.lightmusicon)
    }

    private fun darkDominant() {
        volumeButton!!.setBackgroundResource(R.drawable.darkmusicon)
    }

    fun setStyle() {
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
    }
}