package com.example.savethecat_colormatching.ParticularViews

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.Controllers.AudioController
import com.example.savethecat_colormatching.MainActivity

class SettingsMenu(view: View, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var menuView:View? = null

    private var expandedParams:LayoutParams? = null
    private var contractedParams:LayoutParams? = null

    private var parentLayout:AbsoluteLayout? = null

    companion object {
        var isExpanded:Boolean = false
    }

    init {
        this.menuView = view
        this.menuView!!.layoutParams = params
        this.parentLayout = parentLayout
        parentLayout.addView(view)
        setOriginalParams(params = params)
        setContractedParams()
        setStyle()
        setCornerRadiusAndBorderWidth(radius = params.height / 2,
            borderWidth = params.height / 12)
    }

    private var shape: GradientDrawable? = null
    private var borderWidth:Int = 0
    private var cornerRadius:Int = 0
    fun setCornerRadiusAndBorderWidth(radius: Int, borderWidth: Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        shape!!.setColor((menuView!!.background as ColorDrawable).color)
        if (borderWidth > 0) {
            this.borderWidth = borderWidth
            if (MainActivity.isThemeDark) {
                shape!!.setStroke(borderWidth, Color.WHITE)
            } else {
                shape!!.setStroke(borderWidth, Color.BLACK)
            }

        }
        cornerRadius = radius
        shape!!.cornerRadius = radius.toFloat()
        menuView!!.setBackgroundDrawable(shape)
    }

    private var transforminAnimator:ValueAnimator? = null
    private var isTransforming:Boolean = false
    fun expandOrContract() {
        if (isTransforming) {
            return
        } else {
            if (transforminAnimator != null) {
                transforminAnimator!!.cancel()
                transforminAnimator = null
            }
        }
        AudioController.gearSpinning()
        if (isExpanded) {
            transforminAnimator = ValueAnimator.ofInt((menuView!!.layoutParams as LayoutParams).
            width, contractedParams!!.width)
        } else {
            transforminAnimator = ValueAnimator.ofInt((menuView!!.layoutParams as LayoutParams).
            width, expandedParams!!.width)
        }

        transforminAnimator!!.addUpdateListener {
            menuView!!.layoutParams = LayoutParams((it.animatedValue as Int),
            expandedParams!!.height, expandedParams!!.x, expandedParams!!.y)
        }

        transforminAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        transforminAnimator!!.duration = 1000
        transforminAnimator!!.start()
        isTransforming = true
        transforminAnimator!!.doOnEnd {
            isTransforming = false
            isExpanded = !isExpanded
        }


    }

    fun setContractedParams() {
        contractedParams = LayoutParams(expandedParams!!.height, expandedParams!!.height,
        expandedParams!!.x, expandedParams!!.y)
        menuView!!.layoutParams = contractedParams!!
    }

    fun setOriginalParams(params: LayoutParams) {
        expandedParams = params
    }

    fun getOriginalParams(): LayoutParams {
        return expandedParams!!
    }

    private fun lightDominant() {
        menuView!!.setBackgroundColor(Color.BLACK)
    }

    private fun darkDominant() {
        menuView!!.setBackgroundColor(Color.WHITE)
    }

    fun setStyle() {
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
    }

}