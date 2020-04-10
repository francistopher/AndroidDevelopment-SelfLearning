package com.example.savethecat_colormatching.SettingsMenu

import android.graphics.Color
import android.widget.AbsoluteLayout
import android.widget.Button
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R

class MoreCats (button: Button, parentLayout: AbsoluteLayout, params: AbsoluteLayout.LayoutParams) {

    private var expandedParams: AbsoluteLayout.LayoutParams? = null
    private var contractedParams: AbsoluteLayout.LayoutParams? = null

    private var moreCatsButton: Button? = null

    init {
        this.moreCatsButton = button
        this.moreCatsButton!!.layoutParams = params
        parentLayout.addView(button)
        this.moreCatsButton!!.setBackgroundColor(Color.TRANSPARENT)
        setStyle()
    }

    fun getThis(): Button {
        return moreCatsButton!!
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
        moreCatsButton!!.setBackgroundResource(R.drawable.lightmorecats)
    }

    private fun darkDominant() {
        moreCatsButton!!.setBackgroundResource(R.drawable.darkmorecats)
    }

    fun setStyle() {
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
    }
}