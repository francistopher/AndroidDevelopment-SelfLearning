package com.example.savethecat_colormatching.SettingsMenu

import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import com.example.savethecat_colormatching.R

class MouseCoin(button: Button, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var expandedParams:LayoutParams? = null
    private var contractedParams:LayoutParams? = null

    private var mouseCoinButton:Button? = null

    init {
        this.mouseCoinButton = button
        this.mouseCoinButton!!.layoutParams = params
        parentLayout.addView(button)
        setStyle()
    }

    fun getThis():Button {
        return mouseCoinButton!!
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

    fun setStyle() {
        mouseCoinButton!!.setBackgroundResource(R.drawable.mousecoin)
    }
}