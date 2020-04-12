package com.example.savethecat_colormatching.SettingsMenu

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.Color
import android.widget.AbsoluteLayout
import android.widget.ImageButton
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.ParticularViews.SettingsMenu
import com.example.savethecat_colormatching.R

class LeaderBoard (imageButton: ImageButton, parentLayout: AbsoluteLayout, params: AbsoluteLayout.LayoutParams) {

    private var expandedParams: AbsoluteLayout.LayoutParams? = null
    private var contractedParams: AbsoluteLayout.LayoutParams? = null

    private var leaderBoardButton: ImageButton? = null

    init {
        this.leaderBoardButton = imageButton
        this.leaderBoardButton!!.layoutParams = params
        parentLayout.addView(imageButton)
        this.leaderBoardButton!!.setBackgroundColor(Color.TRANSPARENT)
        setStyle()
    }

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
        if (isTransforming) {
            return
        } else {
            if (transformingSet != null) {
                transformingSet!!.cancel()
                transformingSet = null
            }
        }
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
        transformX!!.addUpdateListener {
            x = it.animatedValue as Int
            leaderBoardButton!!.layoutParams = AbsoluteLayout.LayoutParams(width, height, x, y)
        }
        transformY!!.addUpdateListener {
            y = it.animatedValue as Int
            leaderBoardButton!!.layoutParams = AbsoluteLayout.LayoutParams(width, height, x, y)
        }
        transformWidth!!.addUpdateListener {
            width = it.animatedValue as Int
            leaderBoardButton!!.layoutParams = AbsoluteLayout.LayoutParams(width, height, x, y)
        }
        transformHeight!!.addUpdateListener {
            height = it.animatedValue as Int
            leaderBoardButton!!.layoutParams = AbsoluteLayout.LayoutParams(width, height, x, y)
        }
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

    fun getThis(): ImageButton {
        return leaderBoardButton!!
    }

    fun setContractedParams(params: AbsoluteLayout.LayoutParams) {
        contractedParams = params
    }

    fun getContractedParams(): AbsoluteLayout.LayoutParams {
        return contractedParams!!
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