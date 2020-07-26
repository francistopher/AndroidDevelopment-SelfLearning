package com.example.savethecat_colormatching.SettingsMenu

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageButton
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.HeaderViews.SettingsMenu
import com.example.savethecat_colormatching.R

class MouseCoin(imageButton: ImageButton, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var expandedParams:LayoutParams? = null
    private var contractedParams:LayoutParams? = null

    private var mouseCoinButton:ImageButton? = null

    init {
        this.mouseCoinButton = imageButton
        this.mouseCoinButton!!.layoutParams = params
        parentLayout.addView(imageButton)
        setStyle()
    }

    /*
        Transforms the mouse coin button based off the
        state of the settings menu
     */
    private var transformingSet: AnimatorSet? = null
    private var transformX: ValueAnimator? = null
    private var transformY: ValueAnimator? = null
    private var transformWidth: ValueAnimator? = null
    private var transformHeight: ValueAnimator? = null
    private var isTransforming:Boolean = false
    private var x:Int = 0
    private var y:Int = 0
    private var width:Int = 0
    private var height:Int = 0
    fun expandOrContract() {
        // If the transformation animation is running, cancel it
        if (isTransforming) {
            return
        } else {
            if (transformingSet != null) {
                transformingSet!!.cancel()
                transformingSet = null
            }
        }
        // If the settings menu is expanded, contract the mouse coin button, visa versa
        if (SettingsMenu.isExpanded) {
            transformX = ValueAnimator.ofInt(getExpandedParams().x,
                getContractedParams().x)
            transformY = ValueAnimator.ofInt(getExpandedParams().y,
                getContractedParams().y)
            transformWidth = ValueAnimator.ofInt(getExpandedParams().width,
                getContractedParams().width)
            transformHeight = ValueAnimator.ofInt(getExpandedParams().height,
                getContractedParams().height)
        } else {
            transformX = ValueAnimator.ofInt(getContractedParams().x,
                getExpandedParams().x)
            transformY = ValueAnimator.ofInt(getContractedParams().y,
                getExpandedParams().y)
            transformWidth = ValueAnimator.ofInt(getContractedParams().width,
                getExpandedParams().width)
            transformHeight = ValueAnimator.ofInt(getContractedParams().height,
                getExpandedParams().height)
        }
        // Update the position and size of the mouse coin button
        transformX!!.addUpdateListener {
            x = it.animatedValue as Int
            mouseCoinButton!!.layoutParams = LayoutParams(width, height, x, y)
        }
        transformY!!.addUpdateListener {
            y = it.animatedValue as Int
            mouseCoinButton!!.layoutParams = LayoutParams(width, height, x, y)
        }
        transformWidth!!.addUpdateListener {
            width = it.animatedValue as Int
            mouseCoinButton!!.layoutParams = LayoutParams(width, height, x, y)
        }
        transformHeight!!.addUpdateListener {
            height = it.animatedValue as Int
            mouseCoinButton!!.layoutParams = LayoutParams(width, height, x, y)
        }
        // Setup the properties of the transformation animation
        transformingSet = AnimatorSet()
        transformingSet!!.play(transformX!!).with(transformY!!).with(transformWidth!!).with(transformHeight!!)
        transformingSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        transformingSet!!.duration = 1000
        transformingSet!!.start()
        isTransforming = true
        transformingSet!!.doOnEnd {
            isTransforming = false
        }
    }

    fun getThis():ImageButton {
        return mouseCoinButton!!
    }

    fun setContractedParams(params: LayoutParams) {
        contractedParams = params
    }

    fun getContractedParams():LayoutParams {
        return contractedParams!!
    }

    fun setExpandedParams(params: LayoutParams) {
        expandedParams = params
    }

    fun getExpandedParams(): LayoutParams {
        return expandedParams!!
    }

    /*
        Draw the image of the mouse coin
     */
    fun setStyle() {
        mouseCoinButton!!.setBackgroundResource(R.drawable.mousecoin)
    }
}