package com.example.savethecat_colormatching.ParticularViews

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.Controllers.AspectRatio
import com.example.savethecat_colormatching.Controllers.AudioController
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.SettingsMenu.*

class SettingsMenu(view: View, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var menuView:View? = null

    private var expandedParams:LayoutParams? = null
    private var contractedParams:LayoutParams? = null

    private var parentLayout:AbsoluteLayout? = null

    companion object {
        var isExpanded: Boolean = false
        var adsButton: Ads? = null
        var mouseCoinButton: MouseCoin? = null
        var leaderBoardButton: LeaderBoard? = null
        var volumeButton: Volume? = null
        var moreCatsButton: MoreCats? = null
    }

    private var spaceBetween:Float = 0f

    init {
        this.menuView = view
        this.menuView!!.layoutParams = params
        this.parentLayout = parentLayout
        parentLayout.addView(view)
        setOriginalParams(params = params)
        setStyle()
        setCornerRadiusAndBorderWidth(radius = params.height / 2,
            borderWidth = params.height / 12)
        setupAdsButton()
        setupMouseCoinButton()
        setupLeaderBoardButton()
        setupVolumeButton()
        setupMoreCatsButton()
        val unavailableSpace:Float = ((expandedParams!!.width - mouseCoinButton!!.getExpandedParams().x) +
                (adsButton!!.getExpandedParams().width * 4.0) + getOriginalParams().height).toFloat()
        val availableSpace:Float = getOriginalParams().width - unavailableSpace
        spaceBetween = (availableSpace / 5.0).toFloat()
        repositionMenuButtons()
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

    private fun setupAdsButton() {
        adsButton = Ads(button = Button(menuView!!.context), parentLayout = parentLayout!!,
            params = LayoutParams(expandedParams!!.height, expandedParams!!.height, 0,
            getOriginalParams().y))
        if (AspectRatio.dAspectRatio >= 2.09) {
            adsButton!!.getThis().scaleX = 0.5f
            adsButton!!.getThis().scaleY = 0.5f
        }
        else if (AspectRatio.dAspectRatio >= 1.7) {
            adsButton!!.getThis().scaleX = 0.55f
            adsButton!!.getThis().scaleY = 0.55f
        }
        else {
            adsButton!!.getThis().scaleX = 0.6f
            adsButton!!.getThis().scaleY = 0.6f
        }
        adsButton!!.setExpandedParams(params = adsButton!!.getThis().layoutParams as LayoutParams)
        adsButton!!.setContractedParams(LayoutParams((expandedParams!!.height * 0.5).toInt(),
            (expandedParams!!.height * 0.5).toInt(), 0, 0))
    }

    private fun setupMouseCoinButton() {
        mouseCoinButton = MouseCoin(button = Button(menuView!!.context), parentLayout = parentLayout!!,
            params = LayoutParams(expandedParams!!.height, expandedParams!!.height,
                (getOriginalParams().width - (getOriginalParams().height * 0.7)).toInt(),
                getOriginalParams().y))
        mouseCoinButton!!.getThis().scaleX = 0.75f
        mouseCoinButton!!.getThis().scaleY = 0.75f
        mouseCoinButton!!.setExpandedParams(params = mouseCoinButton!!.getThis().layoutParams
                as LayoutParams)
        mouseCoinButton!!.setContractedParams(LayoutParams(expandedParams!!.height,
            expandedParams!!.height, (expandedParams!!.height + (borderWidth * 0.5)).toInt(), 0))
        mouseCoinButton!!.getThis().layoutParams = mouseCoinButton!!.getExpandedParams()
    }

    private fun setupLeaderBoardButton() {
        leaderBoardButton = LeaderBoard(button = Button(menuView!!.context), parentLayout = parentLayout!!,
            params = LayoutParams(expandedParams!!.height, expandedParams!!.height, 0,
                getOriginalParams().y))
        if (AspectRatio.dAspectRatio >= 2.09) {
            leaderBoardButton!!.getThis().scaleX = 0.5f
            leaderBoardButton!!.getThis().scaleY = 0.5f
        }
        else if (AspectRatio.dAspectRatio >= 1.7) {
            leaderBoardButton!!.getThis().scaleX = 0.55f
            leaderBoardButton!!.getThis().scaleY = 0.55f
        }
        else {
            leaderBoardButton!!.getThis().scaleX = 0.6f
            leaderBoardButton!!.getThis().scaleY = 0.6f
        }
        leaderBoardButton!!.setExpandedParams(params = leaderBoardButton!!.getThis().layoutParams as LayoutParams)
        leaderBoardButton!!.setContractedParams(LayoutParams((expandedParams!!.height * 0.5).toInt(),
            (expandedParams!!.height * 0.5).toInt(), 0, 0))
    }

    private fun setupVolumeButton() {
        volumeButton = Volume(button = Button(menuView!!.context), parentLayout = parentLayout!!,
            params = LayoutParams(expandedParams!!.height, expandedParams!!.height, 0,
                getOriginalParams().y))
        if (AspectRatio.dAspectRatio >= 2.09) {
            volumeButton!!.getThis().scaleX = 0.4f
            volumeButton!!.getThis().scaleY = 0.4f
        }
        else if (AspectRatio.dAspectRatio >= 1.7) {
            volumeButton!!.getThis().scaleX = 0.55f
            volumeButton!!.getThis().scaleY = 0.55f
        }
        else {
            volumeButton!!.getThis().scaleX = 0.6f
            volumeButton!!.getThis().scaleY = 0.6f
        }
        volumeButton!!.setExpandedParams(params = volumeButton!!.getThis().layoutParams as LayoutParams)
        volumeButton!!.setContractedParams(LayoutParams((expandedParams!!.height * 0.5).toInt(),
            (expandedParams!!.height * 0.5).toInt(), 0, 0))
    }


    private fun setupMoreCatsButton() {
        moreCatsButton = MoreCats(button = Button(menuView!!.context), parentLayout = parentLayout!!,
            params = LayoutParams(expandedParams!!.height, expandedParams!!.height, 0,
                getOriginalParams().y))
        if (AspectRatio.dAspectRatio >= 2.09) {
            moreCatsButton!!.getThis().scaleX = 0.4f
            moreCatsButton!!.getThis().scaleY = 0.4f
        }
        else if (AspectRatio.dAspectRatio >= 1.7) {
            moreCatsButton!!.getThis().scaleX = 0.55f
            moreCatsButton!!.getThis().scaleY = 0.55f
        }
        else {
            moreCatsButton!!.getThis().scaleX = 0.6f
            moreCatsButton!!.getThis().scaleY = 0.6f
        }
        moreCatsButton!!.setExpandedParams(params = moreCatsButton!!.getThis().layoutParams as LayoutParams)
        moreCatsButton!!.setContractedParams(LayoutParams((expandedParams!!.height * 0.5).toInt(),
            (expandedParams!!.height * 0.5).toInt(), 0, 0))
    }

    private fun repositionMenuButtons() {
        // Reposition ad button
        adsButton!!.setExpandedParams(LayoutParams(adsButton!!.getExpandedParams().height,
        adsButton!!.getExpandedParams().height, ((expandedParams!!.height * 1.25) +
                    (borderWidth) + spaceBetween).toInt(), adsButton!!.getExpandedParams().y))
        adsButton!!.getThis().layoutParams = adsButton!!.getExpandedParams()
        // Reposition leader board button
        leaderBoardButton!!.setExpandedParams(LayoutParams(leaderBoardButton!!.getExpandedParams().height,
            leaderBoardButton!!.getExpandedParams().height, ((adsButton!!.getExpandedParams().x +
                    adsButton!!.getExpandedParams().width) + (-borderWidth * 0.5) +
                    spaceBetween).toInt(), leaderBoardButton!!.getExpandedParams().y))
        leaderBoardButton!!.getThis().layoutParams = leaderBoardButton!!.getExpandedParams()
        // Reposition volume button
        volumeButton!!.setExpandedParams(LayoutParams(volumeButton!!.getExpandedParams().height,
            volumeButton!!.getExpandedParams().height, ((leaderBoardButton!!.getExpandedParams().x +
                    leaderBoardButton!!.getExpandedParams().width) + (-borderWidth * 0.5) +
                    spaceBetween).toInt(), volumeButton!!.getExpandedParams().y))
        volumeButton!!.getThis().layoutParams = volumeButton!!.getExpandedParams()
        // Reposition more cats button
        moreCatsButton!!.setExpandedParams(LayoutParams(moreCatsButton!!.getExpandedParams().height,
            moreCatsButton!!.getExpandedParams().height, ((volumeButton!!.getExpandedParams().x +
                    volumeButton!!.getExpandedParams().width) + (-borderWidth * 1.25) +
                    spaceBetween).toInt(), moreCatsButton!!.getExpandedParams().y))
        moreCatsButton!!.getThis().layoutParams = moreCatsButton!!.getExpandedParams()
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