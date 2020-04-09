package com.example.savethecat_colormatching.SettingsMenu

import android.graphics.Color
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import android.widget.ImageButton
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R

class Ads(button: Button, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var expandedParams:LayoutParams? = null
    private var contractedParams:LayoutParams? = null

    private var adsButton:Button? = null

    init {
        this.adsButton = button
        this.adsButton!!.layoutParams = params
        parentLayout.addView(button)
        this.adsButton!!.setBackgroundColor(Color.TRANSPARENT)
        setStyle()
    }

    fun getThis():Button {
        return adsButton!!
    }

    fun setContractedParams(params: LayoutParams) {
        contractedParams = params
    }

    fun setExpandedParams(params: LayoutParams) {
        expandedParams = params
    }

    fun getExpandedParams(): LayoutParams {
        return expandedParams!!
    }

    private fun lightDominant() {
        adsButton!!.setBackgroundResource(R.drawable.lightnoads)
    }

    private fun darkDominant() {
        adsButton!!.setBackgroundResource(R.drawable.darknoads)
    }

    fun setStyle() {
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
    }
}