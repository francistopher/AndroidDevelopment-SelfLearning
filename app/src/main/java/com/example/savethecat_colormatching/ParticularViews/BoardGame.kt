package com.example.savethecat_colormatching.ParticularViews

import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams

class BoardGame(boardView: View, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var boardView: View? = null
    private var originalParams: LayoutParams? = null

    init {
        this.boardView = boardView
        this.boardView!!.layoutParams = params
        parentLayout.addView(this.boardView!!)
        setOriginalParams(params = params)
    }

    fun getThis(): View {
        return this.boardView!!
    }

    private fun setOriginalParams(params: LayoutParams) {
        originalParams = params
    }

    fun getOriginalParams():LayoutParams {
        return originalParams!!
    }
}