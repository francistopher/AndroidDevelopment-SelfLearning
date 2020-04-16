package com.example.savethecat_colormatching.SettingsMenu

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import android.widget.ImageButton
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.CustomViews.CButton
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.ParticularViews.ColorOptions
import com.example.savethecat_colormatching.ParticularViews.SettingsMenu
import com.example.savethecat_colormatching.R


class MoreCats (imageButton: ImageButton, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var expandedParams: LayoutParams? = null
    private var contractedParams: LayoutParams? = null

    private var parentLayout:AbsoluteLayout? = null

    private var moreCatsButton: ImageButton? = null

    private var contentViewParams:LayoutParams? = null
    private var infoButton:CButton? = null
    private var closeButton:CButton? = null
    private var previousButton:CButton? = null
    private var nextButton:CButton? = null

    private var popupContainerView: Button? = null

    init {
        this.moreCatsButton = imageButton
        this.moreCatsButton!!.layoutParams = params
        setupParentLayout(parentLayout)
        this.moreCatsButton!!.setBackgroundColor(Color.TRANSPARENT)
        setupPopupView()
        setupInfoButton()
//        setupCloseButton()
//        setupPreviousButton()
//        setupNextButton()
        setupSelector()
        setStyle()
    }

    private fun setupParentLayout(layout:AbsoluteLayout) {
        this.parentLayout = layout
        layout.addView(this.moreCatsButton!!)
    }

    private var shape: GradientDrawable? = null
    fun setCornerRadiusAndBorderWidth(radius: Int, borderWidth: Int, viewID:Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        if (viewID == 1) {
            if (MainActivity.isThemeDark) {
                shape!!.setColor(Color.BLACK)
            } else {
                shape!!.setColor(Color.WHITE)
            }
        } else if (viewID == 2) {
            shape!!.setColor(ColorOptions.blue)
        }
        if (borderWidth > 0) {
            if (MainActivity.isThemeDark) {
                shape!!.setStroke(borderWidth, Color.WHITE)
            } else {
                shape!!.setStroke(borderWidth, Color.BLACK)
            }
        }
        if (viewID == 1) {
            shape!!.cornerRadii = floatArrayOf(radius.toFloat(), radius.toFloat(), radius.toFloat(),
                radius.toFloat(), 0f, 0f, 0f, 0f)
            popupContainerView!!.setBackgroundDrawable(shape)
        } else if (viewID == 2) {
            shape!!.cornerRadii = floatArrayOf(radius.toFloat(), radius.toFloat(), 0f, 0f,
                radius.toFloat(), radius.toFloat(), 0f, 0f)
            infoButton!!.getThis().setBackgroundDrawable(shape)
        }
    }

    private fun setupPopupView() {
        popupContainerView = Button(MainActivity.rootView!!.context)
        popupContainerView!!.setBackgroundColor(Color.BLUE)
        popupContainerView!!.layoutParams = LayoutParams((MainActivity.dWidth -
                (MainActivity.dWidth * 0.025 * 2)).toInt(),
            (MainActivity.dUnitHeight * 16).toInt(),
            (MainActivity.dWidth * 0.025).toInt(),
            ((MainActivity.dWidth * 0.025) + (MainActivity.dNavigationBarHeight * 2.0)).toInt())
        setCornerRadiusAndBorderWidth((MainActivity.dUnitWidth * 1.5).toInt(),
            (MainActivity.dUnitWidth / 3).toInt(), 1)
        setupContentViewParams()

    }

    private fun setupInfoButton() {
        infoButton = CButton(button = Button(popupContainerView!!.context),
                            parentLayout = parentLayout!!,
                            params = LayoutParams((contentViewParams!!.width * 0.3f).toInt(),
                            (contentViewParams!!.width * 0.18f).toInt(), contentViewParams!!.x,
                                contentViewParams!!.y ))
        infoButton!!.getThis().setBackgroundColor(ColorOptions.blue)
        setCornerRadiusAndBorderWidth((MainActivity.dUnitWidth * 1.5).toInt(),
            (MainActivity.dUnitWidth / 3).toInt(), 2)
        infoButton!!.setTextSize(infoButton!!.getOriginalParams().height * 0.3f)
        infoButton!!.setText("i", false)
        parentLayout!!.removeView(infoButton!!.getThis())
        infoButton!!.getThis().alpha = 1f
    }

    private fun setupCloseButton() {

    }

    private fun setupPreviousButton() {

    }

    private fun setupNextButton() {

    }

    private fun setupContentViewParams() {
        contentViewParams = popupContainerView!!.layoutParams as LayoutParams
    }

    private fun setupSelector() {
        moreCatsButton!!.setOnClickListener {
            parentLayout!!.addView(popupContainerView!!)
            parentLayout!!.addView(infoButton!!.getThis())
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
            moreCatsButton!!.layoutParams = LayoutParams(width, height, x, y)
        }
        transformY!!.addUpdateListener {
            y = it.animatedValue as Int
            moreCatsButton!!.layoutParams = LayoutParams(width, height, x, y)
        }
        transformWidth!!.addUpdateListener {
            width = it.animatedValue as Int
            moreCatsButton!!.layoutParams = LayoutParams(width, height, x, y)
        }
        transformHeight!!.addUpdateListener {
            height = it.animatedValue as Int
            moreCatsButton!!.layoutParams = LayoutParams(width, height, x, y)
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

    fun setContractedParams(params: LayoutParams) {
        contractedParams = params
    }

    fun getContractedParams(): LayoutParams {
        return contractedParams!!
    }

    fun setExpandedParams(params: LayoutParams) {
        expandedParams = params
    }

    fun getExpandedParams(): LayoutParams {
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