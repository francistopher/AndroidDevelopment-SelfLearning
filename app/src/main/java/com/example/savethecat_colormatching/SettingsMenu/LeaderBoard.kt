package com.example.savethecat_colormatching.SettingsMenu

import android.graphics.Color
import android.widget.AbsoluteLayout
import android.widget.Button
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R

class LeaderBoard (button: Button, parentLayout: AbsoluteLayout, params: AbsoluteLayout.LayoutParams) {

    private var expandedParams: AbsoluteLayout.LayoutParams? = null
    private var contractedParams: AbsoluteLayout.LayoutParams? = null

    private var leaderBoardButton: Button? = null

    init {
        this.leaderBoardButton = button
        this.leaderBoardButton!!.layoutParams = params
        parentLayout.addView(button)
        this.leaderBoardButton!!.setBackgroundColor(Color.TRANSPARENT)
        setStyle()
    }

    fun getThis(): Button {
        return leaderBoardButton!!
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
        leaderBoardButton!!.setBackgroundResource(R.drawable.lightleaderboard)
    }

    private fun darkDominant() {
        leaderBoardButton!!.setBackgroundResource(R.drawable.darkleaderboard)
    }

    fun setStyle() {
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
    }
}