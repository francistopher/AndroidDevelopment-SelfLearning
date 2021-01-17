package com.example.savethecat_colormatching.SettingsMenu

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.text.Layout
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageButton
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.Controllers.MPController
import com.example.savethecat_colormatching.HeaderViews.SettingsMenu
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R

class Ads(imageButton: ImageButton, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var expandedParams:LayoutParams? = null
    private var contractedParams:LayoutParams? = null

    private var adsButton:ImageButton? = null

    private var notEnoughAlertDialog:AlertDialog? = null
    private var removeAdsAlertDialog:AlertDialog? = null

    companion object {
        var themeState:Int = 2
    }

    init {
        this.adsButton = imageButton
        this.adsButton!!.layoutParams = params
        parentLayout.addView(imageButton)
        this.adsButton!!.setBackgroundColor(Color.TRANSPARENT)
        setupNotEnoughAlertDialog()
        setupRemoveAdsAlertDialog()
        setupSelector()
        setStyle()
    }

    /*
        Create notification that informs the user that they need
        to save more cats to remove the ads
     */
    private fun setupNotEnoughAlertDialog() {
        val dialogBuilder = AlertDialog.Builder(MainActivity.staticSelf!!)
        var spannableString = SpannableString("No Ads")
        spannableString.setSpan(
            AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
            0, spannableString.length, 0
        )
        dialogBuilder.setTitle(spannableString)
        spannableString = SpannableString("To hide ads during gameplay,\n" +
                "you must save over 90000 cats!")
        spannableString.setSpan(
            AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
            0, spannableString.length, 0
        )
        dialogBuilder.setMessage(spannableString)
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which) {
                DialogInterface.BUTTON_NEUTRAL -> notEnoughAlertDialog!!.hide()
            }
        }
        dialogBuilder.setNegativeButton("Cancel", dialogClickListener)
        notEnoughAlertDialog = dialogBuilder.create()
    }

    /*
        Creates the dialog for the user that has saved
        enough cats to remove the ads
     */
    private fun setupRemoveAdsAlertDialog() {
        val dialogBuilder = AlertDialog.Builder(MainActivity.staticSelf!!)
        var spannableString = SpannableString("No Ads")
        spannableString.setSpan(
            AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
            0, spannableString.length, 0
        )
        dialogBuilder.setTitle(spannableString)
        spannableString = SpannableString("You have saved over 90000 cats!,\n" +
                "Would you like to hide ads visible during gameplay?")
        spannableString.setSpan(
            AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
            0, spannableString.length, 0
        )
        dialogBuilder.setMessage(spannableString)
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    MainActivity.gdController!!.saveThemeState(-1)
                    MainActivity.staticSelf!!.bannerAdView!!.alpha = 0f
                    MainActivity.staticSelf!!.noInternetAdView!!.alpha = 0f
                }
                DialogInterface.BUTTON_NEUTRAL -> notEnoughAlertDialog!!.hide()
            }
        }
        dialogBuilder.setPositiveButton("Yes", dialogClickListener)
        dialogBuilder.setNegativeButton("Cancel", dialogClickListener)
        removeAdsAlertDialog = dialogBuilder.create()
    }

    /*
        Selects one of the alert views based on the number of cats they have saved
     */
    private fun setupSelector() {
        adsButton!!.setOnClickListener {
            if (MainActivity.isInternetReachable && MainActivity.isGooglePlayGameServicesAvailable) {
                if (themeState == 2) {
                    if (LeaderBoard.allGamesScore < 90000) {
                        notEnoughAlertDialog!!.show()
                    } else {
                        removeAdsAlertDialog!!.show()
                    }
                } else {
                    themeState = when (themeState) {
                        -1 -> 0
                        0 -> 1
                        else -> -1
                    }
                    MainActivity.gdController!!.saveThemeState(themeState)
                    setStyle()
                    MainActivity.staticSelf!!.setAllStyles(themeState)
                }
            } else {
                MPController.displayFailureReason()
            }
        }
    }

    /*
        Transforms the ads button to the expanded or contracted
        state of the settings menu
     */
    private var transformingSet: AnimatorSet? = null
    private var transformX:ValueAnimator? = null
    private var transformY:ValueAnimator? = null
    private var transformWidth:ValueAnimator? = null
    private var transformHeight:ValueAnimator? = null
    private var isTransforming:Boolean = false
    private var x:Int = 0
    private var y:Int = 0
    private var width:Int = 0
    private var height:Int = 0
    fun expandOrContract() {
        // If the transforming animation is running, cancel it
        if (isTransforming) {
            return
        } else {
            if (transformingSet != null) {
                transformingSet!!.cancel()
                transformingSet = null
            }
        }
        // If the settings menu is closed, expand, visa versa
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
        // Update the position and size
        transformX!!.addUpdateListener {
            x = it.animatedValue as Int
            adsButton!!.layoutParams = LayoutParams(width, height, x, y)
        }
        transformY!!.addUpdateListener {
            y = it.animatedValue as Int
            adsButton!!.layoutParams = LayoutParams(width, height, x, y)
        }
        transformWidth!!.addUpdateListener {
            width = it.animatedValue as Int
            adsButton!!.layoutParams = LayoutParams(width, height, x, y)
        }
        transformHeight!!.addUpdateListener {
            height = it.animatedValue as Int
            adsButton!!.layoutParams = LayoutParams(width, height, x, y)
        }
        // Setup the animation properties
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

    fun getContractedParams():LayoutParams {
        return contractedParams!!
    }

    fun getThis():ImageButton {
        return adsButton!!
    }

    fun setContractedParams(params: LayoutParams) {
        contractedParams = params
    }

    fun setExpandedParams(params: LayoutParams) {
        expandedParams = params
    }

    fun getExpandedParams(): LayoutParams {
        return expandedParams!!
    }

    private fun lightDominant() {
        adsButton!!.setBackgroundResource(R.drawable.lightnoads)
    }

    private fun darkDominant() {
        adsButton!!.setBackgroundResource(R.drawable.darknoads)
    }

    /*
        Set the style of the ad button based on
        the theme of the operating system
     */
    fun setStyle() {
        if (themeState != 2) {
            if (themeState == -1) {
                adsButton!!.setBackgroundResource(R.drawable.autostyle)
            } else if (themeState == 0) {
                adsButton!!.setBackgroundResource(R.drawable.darkstyle)
            } else if (themeState == 1) {
                adsButton!!.setBackgroundResource(R.drawable.lightstyle)
            }
        } else {
            if (MainActivity.isThemeDark) {
                lightDominant()
            } else {
                darkDominant()
            }
        }
    }
}