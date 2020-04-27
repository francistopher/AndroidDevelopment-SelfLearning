package com.example.savethecat_colormatching.HeaderViews

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageButton
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.Controllers.AudioController
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.ParticularViews.MCView
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

        fun looseMouseCoin() {
            if (MCView.mouseCoinCount > 0) {
                val mcButton: LayoutParams =
                    mouseCoinButton!!.getThis().layoutParams as LayoutParams
                val x: Int = if ((0..1).random() == 0) {
                    (0..(MainActivity.dWidth * 0.5).toInt()).random()
                } else {
                    ((MainActivity.dWidth * 0.5).toInt()..(MainActivity.dWidth.toInt() -
                            mcButton.width)).random()
                }
                MainActivity.mouseCoinView!!.updateCount(MCView.mouseCoinCount - 1)
                com.example.savethecat_colormatching.Characters.MouseCoin(
                    spawnParams = mcButton,
                    targetParams = LayoutParams(
                        mcButton.width, mcButton.height,
                        x, MainActivity.dHeight.toInt()
                    ),
                    isEarned = false
                )
            }
        }
    }

    private var spaceBetween:Float = 0f

    init {
        this.menuView = view
        this.menuView!!.layoutParams = params
        this.parentLayout = MainActivity.rootLayout!!
        parentLayout.addView(view)
        setOriginalParams(params = params)
        setStyle()
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
        setContractedParams()
        menuView!!.layoutParams = contractedParams!!
    }

    fun getThis():View {
        return menuView!!
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

    var transformingAnimator:ValueAnimator? = null
    var isTransforming:Boolean = false
    fun expandOrContract() {
        if (isTransforming) {
            return
        } else {
            if (transformingAnimator != null) {
                transformingAnimator!!.cancel()
                transformingAnimator = null
            }
        }
        AudioController.gearSpinning()
        if (isExpanded) {
            transformingAnimator = ValueAnimator.ofInt((menuView!!.layoutParams as LayoutParams).
            width, contractedParams!!.width)
        } else {
            transformingAnimator = ValueAnimator.ofInt((menuView!!.layoutParams as LayoutParams).
            width, expandedParams!!.width)
        }

        transformingAnimator!!.addUpdateListener {
            menuView!!.layoutParams = LayoutParams((it.animatedValue as Int),
            expandedParams!!.height, expandedParams!!.x, expandedParams!!.y)
        }
        transformingAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        transformingAnimator!!.duration = 1000
        transformingAnimator!!.start()
        isTransforming = true
        transformingAnimator!!.doOnEnd {
            isTransforming = false
            isExpanded = !isExpanded
        }
    }

    private fun setupAdsButton() {
        adsButton = Ads(imageButton = ImageButton(menuView!!.context), parentLayout = parentLayout!!,
            params = LayoutParams(expandedParams!!.height, expandedParams!!.height, 0,
            getOriginalParams().y))
        if (MainActivity.dAspectRatio >= 2.09) {
            adsButton!!.getThis().scaleX = 0.55f
            adsButton!!.getThis().scaleY = 0.55f
        }
        else if (MainActivity.dAspectRatio >= 1.7) {
            adsButton!!.getThis().scaleX = 0.55f
            adsButton!!.getThis().scaleY = 0.55f
        }
        else {
            adsButton!!.getThis().scaleX = 0.75f
            adsButton!!.getThis().scaleY = 0.75f
        }
        adsButton!!.setExpandedParams(params = adsButton!!.getThis().layoutParams as LayoutParams)
        adsButton!!.setContractedParams(LayoutParams((expandedParams!!.height * 0.5).toInt(),
            (expandedParams!!.height * 0.5).toInt(), (getOriginalParams().x +
                    getOriginalParams().height * 0.25).toInt(), (getOriginalParams().y +
                    getOriginalParams().height * 0.25).toInt()))
        adsButton!!.getThis().alpha = 0f
    }

    private fun setupMouseCoinButton() {
        mouseCoinButton = MouseCoin(imageButton = ImageButton(menuView!!.context), parentLayout = parentLayout!!,
            params = LayoutParams(expandedParams!!.height, expandedParams!!.height,
                (getOriginalParams().width - (getOriginalParams().height * 0.7)).toInt(),
                getOriginalParams().y))
        mouseCoinButton!!.getThis().scaleX = 0.75f
        mouseCoinButton!!.getThis().scaleY = 0.75f
        mouseCoinButton!!.setExpandedParams(params = mouseCoinButton!!.getThis().layoutParams
                as LayoutParams)
        mouseCoinButton!!.setContractedParams(LayoutParams(expandedParams!!.height,
            expandedParams!!.height, (expandedParams!!.height + (borderWidth * 2.5)).toInt(),
            getOriginalParams().y))
        mouseCoinButton!!.getThis().layoutParams = mouseCoinButton!!.getExpandedParams()
        mouseCoinButton!!.getThis().alpha = 0f
        mouseCoinButton!!.getThis().setOnClickListener {
            AudioController.coinEarned()
        }
    }

    private fun setupLeaderBoardButton() {
        leaderBoardButton = LeaderBoard(imageButton = ImageButton(menuView!!.context), parentLayout = parentLayout!!,
            params = LayoutParams(expandedParams!!.height, expandedParams!!.height, 0,
                getOriginalParams().y))
        if (MainActivity.dAspectRatio >= 2.09) {
            leaderBoardButton!!.getThis().scaleX = 0.55f
            leaderBoardButton!!.getThis().scaleY = 0.55f
        }
        else if (MainActivity.dAspectRatio >= 1.7) {
            leaderBoardButton!!.getThis().scaleX = 0.55f
            leaderBoardButton!!.getThis().scaleY = 0.55f
        }
        else {
            leaderBoardButton!!.getThis().scaleX = 0.75f
            leaderBoardButton!!.getThis().scaleY = 0.75f
        }
        leaderBoardButton!!.setExpandedParams(params = leaderBoardButton!!.getThis().layoutParams as LayoutParams)
        leaderBoardButton!!.setContractedParams(LayoutParams((expandedParams!!.height * 0.5).toInt(),
            (expandedParams!!.height * 0.5).toInt(), (getOriginalParams().x +
                    getOriginalParams().height * 0.25).toInt(), (getOriginalParams().y +
                    getOriginalParams().height * 0.25).toInt()))
        leaderBoardButton!!.getThis().alpha = 0f
    }

    private fun setupVolumeButton() {
        volumeButton = Volume(imageButton = ImageButton(menuView!!.context), parentLayout = parentLayout!!,
            params = LayoutParams(expandedParams!!.height, expandedParams!!.height, 0,
                getOriginalParams().y))
        if (MainActivity.dAspectRatio >= 2.09) {
            volumeButton!!.getThis().scaleX = 0.55f
            volumeButton!!.getThis().scaleY = 0.55f
        }
        else if (MainActivity.dAspectRatio >= 1.7) {
            volumeButton!!.getThis().scaleX = 0.55f
            volumeButton!!.getThis().scaleY = 0.55f
        }
        else {
            volumeButton!!.getThis().scaleX = 0.75f
            volumeButton!!.getThis().scaleY = 0.75f
        }
        volumeButton!!.setExpandedParams(params = volumeButton!!.getThis().layoutParams as LayoutParams)
        volumeButton!!.setContractedParams(LayoutParams((expandedParams!!.height * 0.5).toInt(),
            (expandedParams!!.height * 0.5).toInt(), (getOriginalParams().x +
                    getOriginalParams().height * 0.25).toInt(), (getOriginalParams().y +
                    getOriginalParams().height * 0.25).toInt()))
        volumeButton!!.getThis().alpha = 0f
    }


    private fun setupMoreCatsButton() {
        moreCatsButton = MoreCats(imageButton = ImageButton(MainActivity.rootView!!.context), parentLayout = parentLayout!!,
            params = LayoutParams(expandedParams!!.height, expandedParams!!.height, 0,
                getOriginalParams().y))
        if (MainActivity.dAspectRatio >= 2.09) {
            moreCatsButton!!.getThis().scaleX = 0.55f
            moreCatsButton!!.getThis().scaleY = 0.55f
        }
        else if (MainActivity.dAspectRatio >= 1.7) {
            moreCatsButton!!.getThis().scaleX = 0.55f
            moreCatsButton!!.getThis().scaleY = 0.55f
        }
        else {
            moreCatsButton!!.getThis().scaleX = 0.75f
            moreCatsButton!!.getThis().scaleY = 0.75f
        }
        moreCatsButton!!.setExpandedParams(params = moreCatsButton!!.getThis().layoutParams as LayoutParams)
        moreCatsButton!!.setContractedParams(LayoutParams((expandedParams!!.height * 0.5).toInt(),
            (expandedParams!!.height * 0.5).toInt(),(getOriginalParams().x +
                    getOriginalParams().height * 0.25).toInt(), (getOriginalParams().y +
                    getOriginalParams().height * 0.25).toInt()))
        moreCatsButton!!.getThis().alpha = 0f
    }

    private fun repositionMenuButtons() {
        // Reposition ad button
        adsButton!!.setExpandedParams(LayoutParams(
            adsButton!!.getExpandedParams().height,
        adsButton!!.getExpandedParams().height, ((expandedParams!!.height * 1.25) +
                    (borderWidth) + spaceBetween).toInt(), adsButton!!.getExpandedParams().y))
        adsButton!!.getThis().layoutParams = adsButton!!.getExpandedParams()
        // Reposition leader board button
        leaderBoardButton!!.setExpandedParams(LayoutParams(
            leaderBoardButton!!.getExpandedParams().height,
            leaderBoardButton!!.getExpandedParams().height, ((adsButton!!.getExpandedParams().x +
                    adsButton!!.getExpandedParams().width) + (-borderWidth * 0.5) +
                    spaceBetween).toInt(), leaderBoardButton!!.getExpandedParams().y))
        leaderBoardButton!!.getThis().layoutParams = leaderBoardButton!!.getExpandedParams()
        // Reposition volume button
        volumeButton!!.setExpandedParams(LayoutParams(
            volumeButton!!.getExpandedParams().height,
            volumeButton!!.getExpandedParams().height, ((leaderBoardButton!!.getExpandedParams().x +
                    leaderBoardButton!!.getExpandedParams().width) + (-borderWidth * 0.5) +
                    spaceBetween).toInt(), volumeButton!!.getExpandedParams().y))
        volumeButton!!.getThis().layoutParams = volumeButton!!.getExpandedParams()
        // Reposition more cats button
        moreCatsButton!!.setExpandedParams(LayoutParams(
            moreCatsButton!!.getExpandedParams().height,
            moreCatsButton!!.getExpandedParams().height, ((volumeButton!!.getExpandedParams().x +
                    volumeButton!!.getExpandedParams().width) + (-borderWidth * 1.25) +
                    spaceBetween).toInt(), moreCatsButton!!.getExpandedParams().y))
        moreCatsButton!!.getThis().layoutParams = moreCatsButton!!.getExpandedParams()
    }

    private fun setContractedParams() {
        contractedParams = LayoutParams((expandedParams!!.height * 1.9).toInt(), expandedParams!!.height,
        expandedParams!!.x, expandedParams!!.y)
        menuView!!.layoutParams = contractedParams!!
        // Contract no ads button
        adsButton!!.getThis().layoutParams = adsButton!!.getContractedParams()
        leaderBoardButton!!.getThis().layoutParams = leaderBoardButton!!.getContractedParams()
        volumeButton!!.getThis().layoutParams = volumeButton!!.getContractedParams()
        moreCatsButton!!.getThis().layoutParams = moreCatsButton!!.getContractedParams()
        mouseCoinButton!!.getThis().layoutParams = mouseCoinButton!!.getContractedParams()
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
        setCornerRadiusAndBorderWidth(radius = getOriginalParams().height / 2,
            borderWidth = getOriginalParams().height / 12)
    }

}