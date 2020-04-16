package com.example.savethecat_colormatching.SettingsMenu

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageButton
import android.widget.PopupWindow
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.ParticularViews.SettingsMenu
import com.example.savethecat_colormatching.R

class MoreCats (imageButton: ImageButton, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var expandedParams: LayoutParams? = null
    private var contractedParams: LayoutParams? = null

    private var moreCatsButton: ImageButton? = null

    init {
        this.moreCatsButton = imageButton
        this.moreCatsButton!!.layoutParams = params
        parentLayout.addView(imageButton)
        this.moreCatsButton!!.setBackgroundColor(Color.TRANSPARENT)
        setupPopupView()
        setupPopupWindow()
        setupSelector()
        setStyle()
    }

    private fun setupPopupView() {
        popupContainerView = View(MainActivity.rootView!!.context)
        popupContainerView!!.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun setupPopupWindow() {
        popupWindow = PopupWindow(MainActivity.rootView!!.context)
        popupWindow!!.contentView = popupContainerView!!
        popupWindow!!.height = (MainActivity.dUnitHeight * 14.125).toInt()
        popupWindow!!.width = (MainActivity.dWidth - (MainActivity.dWidth * 0.025 * 2)).toInt()

        var shape: GradientDrawable?
        fun setCornerRadiusAndBorderWidth(radius: Int, borderWidth: Int) {
            shape = null
            shape = GradientDrawable()
            shape!!.shape = GradientDrawable.RECTANGLE
            if (MainActivity.isThemeDark) {
                shape!!.setColor(Color.BLACK)
            } else {
                shape!!.setColor(Color.WHITE)
            }
            if (borderWidth > 0) {
                if (MainActivity.isThemeDark) {
                    shape!!.setStroke(borderWidth, Color.WHITE)
                } else {
                    shape!!.setStroke(borderWidth, Color.BLACK)
                }
            }
            shape!!.cornerRadii = floatArrayOf(radius.toFloat(), radius.toFloat(), radius.toFloat(),
                radius.toFloat(), 0f, 0f, 0f, 0f)
            popupWindow!!.setBackgroundDrawable(shape)
        }
        setCornerRadiusAndBorderWidth(MainActivity.dUnitWidth.toInt(),
            (MainActivity.dUnitWidth / 3).toInt())
    }

    private var popupWindow:PopupWindow? = null
    private var popupContainerView: View? = null
    private fun setupSelector() {
        moreCatsButton!!.setOnClickListener {
            popupWindow!!.showAsDropDown(MainActivity.rootView!!,
                (MainActivity.dWidth * 0.025).toInt(),
                (MainActivity.dWidth * 0.025).toInt())
            popupWindow!!.isFocusable = true
        }
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
            moreCatsButton!!.layoutParams = AbsoluteLayout.LayoutParams(width, height, x, y)
        }
        transformY!!.addUpdateListener {
            y = it.animatedValue as Int
            moreCatsButton!!.layoutParams = AbsoluteLayout.LayoutParams(width, height, x, y)
        }
        transformWidth!!.addUpdateListener {
            width = it.animatedValue as Int
            moreCatsButton!!.layoutParams = AbsoluteLayout.LayoutParams(width, height, x, y)
        }
        transformHeight!!.addUpdateListener {
            height = it.animatedValue as Int
            moreCatsButton!!.layoutParams = AbsoluteLayout.LayoutParams(width, height, x, y)
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
        return moreCatsButton!!
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