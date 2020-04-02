package com.example.savethecat_colormatching.CustomViews

import android.app.ActionBar
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.LinearLayout.LayoutParams

class CLabel(textView: TextView, parentView: View, parameter:List<Double>, backgroundColor: Color) {

    var isInverted:Boolean = false

    var originalFrame:LayoutParams? = null
    var reducedFrame:LayoutParams? = null
    var shrunkFrame:LayoutParams? = null

    var textView:TextView? = null

    init {
        this.textView = textView
        setFrameParameters()
    }

    fun setFrameParameters() {

    }

    fun setOriginalFrame() {

    }

    fun setShrunkFrame() {

    }

    fun shrink(removeFromViewParent:Boolean) {

    }

    fun shrunk() {

    }

    fun fadeIn() {

    }

    fun fadeOut() {

    }

    fun fadeInAndOut() {

    }

    fun setStyle() {

    }

}