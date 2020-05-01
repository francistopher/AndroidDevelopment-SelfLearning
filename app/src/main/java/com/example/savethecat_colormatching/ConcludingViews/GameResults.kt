package com.example.savethecat_colormatching.ConcludingViews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.Characters.MouseCoin
import com.example.savethecat_colormatching.Controllers.AudioController
import com.example.savethecat_colormatching.CustomViews.CButton
import com.example.savethecat_colormatching.CustomViews.CImageView
import com.example.savethecat_colormatching.CustomViews.CLabel
import com.example.savethecat_colormatching.HeaderViews.SettingsMenu
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.MainActivity.Companion.mInterstitialAd
import com.example.savethecat_colormatching.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class GameResults(resultsView: View,
                  parentLayout: AbsoluteLayout,
                  params: LayoutParams) {

    private var originalParams:LayoutParams? = null
    private var resultsView:View? = null
    private var resultsContext: Context? = null

    private var parentLayout:AbsoluteLayout? = null
    private var gameOverLabel:CLabel? = null
    private var unitHeight:Int = 0

    private var smilingCat:CImageView? = null
    private var deadCat:CImageView? = null

    private var aliveCatCountLabel:CLabel? = null
    private var deadCatCountLabel:CLabel? = null

    private var watchAdButton:CButton? = null
    private var mouseCoin:Button? = null

    companion object {
        var savedCatButtonsCount:Int = 0
        var deadCatButtonsCount:Int = 0

        var mouseCoinsEarned:Int = 5

        var watchAdButtonWasSelected:Boolean = false
    }

    init {
        this.resultsView = resultsView
        this.resultsContext = resultsView.context
        this.resultsView!!.layoutParams = params
        unitHeight = (params.height / 8.0).toInt()
        setupOriginalParams(params = params)
        this.parentLayout = parentLayout
        parentLayout.addView(resultsView)
        setStyle()
        setCornerRadiusAndBorderWidth(params.height / 2,
           params.height / 60)
    }

    private fun hideEverything() {
        resultsView!!.alpha = 0f
        gameOverLabel!!.getThis().alpha = 0f
        smilingCat!!.getThis().alpha = 0f
        deadCat!!.getThis().alpha = 0f
        aliveCatCountLabel!!.getThis().alpha = 0f
        deadCatCountLabel!!.getThis().alpha = 0f
        watchAdButton!!.getThis().alpha = 0f
        mouseCoin!!.alpha = 0f
    }

    fun setupContents() {
        setupTitleView()
        setupSmilingCat()
        setupDeadCat()
        setupAliveCatCountLabel()
        setupDeadCatCountLabel()
        setupWatchAdButton()
        setupMouseCoin()
        hideEverything()
    }

    private var shape: GradientDrawable? = null
    private var borderWidth:Int = 0
    private var cornerRadius:Int = 0
    private fun setCornerRadiusAndBorderWidth(radius: Int, borderWidth: Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        if (MainActivity.isThemeDark) {
            shape!!.setColor(Color.BLACK)
        } else {
            shape!!.setColor(Color.WHITE)
        }
        if (borderWidth > 0) {
            this.borderWidth = borderWidth
            if (MainActivity.isThemeDark) {
                shape!!.setStroke(borderWidth, Color.WHITE)
            } else {
                shape!!.setStroke(borderWidth, Color.BLACK)
            }
        }
        cornerRadius = radius
        shape!!.cornerRadii = floatArrayOf(radius.toFloat(), radius.toFloat(),
            radius.toFloat(), radius.toFloat(), (MainActivity.dUnitHeight * 8.0 / 7.0).toFloat(),
            (MainActivity.dUnitHeight * 8.0 / 7.0).toFloat(),
            (MainActivity.dUnitHeight * 8.0 / 7.0).toFloat(),
            (MainActivity.dUnitHeight * 8.0 / 7.0).toFloat())
        this.resultsView!!.setBackgroundDrawable(shape)
    }

    fun getThis():View {
        return resultsView!!
    }

    fun setupOriginalParams(params:LayoutParams) {
        originalParams = params
    }

    fun getOriginalParams():LayoutParams {
        return originalParams!!
    }

    private fun lightDominant() {
        resultsView!!.setBackgroundColor(Color.BLACK)
    }

    private fun darkDominant() {
        resultsView!!.setBackgroundColor(Color.WHITE)
    }

    fun setStyle() {
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
    }

    private fun setupTitleView() {
        gameOverLabel = CLabel(textView = TextView(resultsContext!!), parentLayout = parentLayout!!,
        params = LayoutParams((getOriginalParams().width * 0.5).toInt(), (unitHeight * 2.0).toInt(),
            (getOriginalParams().width * 0.25).toInt() + getOriginalParams().x,
            getOriginalParams().y + (unitHeight * 0.5).toInt()))
        gameOverLabel!!.setTextSize(gameOverLabel!!.getOriginalParams().height * 0.1875f)
        gameOverLabel!!.setText("Game Over")
        gameOverLabel!!.isInverted = true
        gameOverLabel!!.setStyle()
        gameOverLabel!!.setCornerRadiusAndBorderWidth((getOriginalParams().height / 2.0).toInt(),
            0)
    }

    fun hideWatchAdButtonChildren() {
        watchAdButton!!.setText("", false)
        mouseCoin!!.alpha = 0f
    }

    private fun setupSmilingCat() {
        smilingCat = CImageView(imageView = ImageView(resultsContext!!), parentLayout = parentLayout!!,
        params = LayoutParams((getOriginalParams().width * 0.625).toInt(),
            (getOriginalParams().width * 0.8125).toInt(), getOriginalParams().x -
                    (getOriginalParams().width * 0.04).toInt(),
            gameOverLabel!!.getOriginalParams().y +
                    (gameOverLabel!!.getOriginalParams().height * 0.25).toInt()))
        smilingCat!!.loadImages(R.drawable.lightsmilingcat, R.drawable.darksmilingcat)
    }

    private fun setupDeadCat() {
        deadCat = CImageView(imageView = ImageView(resultsContext!!), parentLayout = parentLayout!!,
            params = LayoutParams((getOriginalParams().width * 0.625).toInt(),
                (getOriginalParams().width * 0.8125).toInt(), getOriginalParams().x +
                        (getOriginalParams().width * 0.5).toInt() -
                        (getOriginalParams().width * 0.095).toInt(),
                gameOverLabel!!.getOriginalParams().y +
                        (gameOverLabel!!.getOriginalParams().height * 0.25).toInt()))
        deadCat!!.loadImages(R.drawable.lightdeadcat, R.drawable.darkdeadcat)
    }

    private fun setupAliveCatCountLabel() {
        aliveCatCountLabel = CLabel(textView = TextView(resultsContext!!), parentLayout = parentLayout!!,
        params = LayoutParams((getOriginalParams().width * 0.5).toInt() - (borderWidth * 2.0).toInt(),
            unitHeight, getOriginalParams().x + (borderWidth * 2.0).toInt(), smilingCat!!.getOriginalParams().y +
                    (smilingCat!!.getOriginalParams().height * 0.775).toInt()))
        aliveCatCountLabel!!.setTextSize(aliveCatCountLabel!!.getOriginalParams().height * 0.2f)
        aliveCatCountLabel!!.setText("$savedCatButtonsCount")
        aliveCatCountLabel!!.isInverted = true
        aliveCatCountLabel!!.setStyle()
    }

    private fun setupDeadCatCountLabel() {
        deadCatCountLabel = CLabel(textView = TextView(resultsContext!!), parentLayout = parentLayout!!,
            params = LayoutParams((getOriginalParams().width * 0.5).toInt() -
                    (borderWidth * 2.0).toInt(), unitHeight,
               getOriginalParams().x + (getOriginalParams().width * 0.5).toInt(),
                deadCat!!.getOriginalParams().y +
                        (deadCat!!.getOriginalParams().height * 0.775).toInt()))
        deadCatCountLabel!!.setTextSize(aliveCatCountLabel!!.getOriginalParams().height * 0.2f)
        deadCatCountLabel!!.setText("$deadCatButtonsCount")
        deadCatCountLabel!!.isInverted = true
        deadCatCountLabel!!.setStyle()
    }

    private fun setupWatchAdButton() {
        watchAdButton = CButton(button = Button(resultsContext!!), parentLayout = parentLayout!!,
        params = LayoutParams((getOriginalParams().width * 0.8).toInt(), unitHeight,
            (getOriginalParams().x + (getOriginalParams().width * 0.1)).toInt(),
            deadCatCountLabel!!.getOriginalParams().y +
                    (deadCatCountLabel!!.getOriginalParams().height * 1.1).toInt()))
        watchAdButton!!.isBorderGold = true
        watchAdButton!!.setCornerRadiusAndBorderWidth((cornerRadius * 0.07).toInt(),
            (borderWidth * 0.75).toInt())
        watchAdButton!!.setText("Watch Short Ad to Win ····· s!", false)
        watchAdButton!!.setTextSize(watchAdButton!!.getOriginalParams().height * 0.2f)
        watchAdButton!!.getThis().setOnClickListener {
            if (MainActivity.isInternetReachable) {
                if (mInterstitialAd!!.isLoaded) {
                    mInterstitialAd!!.show()
                }
            } else {
                MainActivity.gameNotification!!.displayNoInternet()
            }
        }
        watchAdButton!!.getThis().isEnabled = false
    }

    private fun setupMouseCoin() {
        var mouseCoinParams: LayoutParams
        mouseCoinParams = if (MainActivity.dAspectRatio >= 1.9) {
            LayoutParams((watchAdButton!!.getOriginalParams().height * 0.7).toInt(),
                (watchAdButton!!.getOriginalParams().height * 0.7).toInt(),
                (getOriginalParams().x + getOriginalParams().width * 0.7).toInt(),
                (watchAdButton!!.getOriginalParams().y +
                        (watchAdButton!!.getOriginalParams().height * 0.15).toInt()))
        } else {
            LayoutParams((watchAdButton!!.getOriginalParams().height * 0.8).toInt(),
                (watchAdButton!!.getOriginalParams().height * 0.8).toInt(),
                (getOriginalParams().x + getOriginalParams().width * 0.7).toInt(),
                (watchAdButton!!.getOriginalParams().y +
                        (watchAdButton!!.getOriginalParams().height * 0.1).toInt()))
        }
        mouseCoin = Button(resultsContext!!)
        parentLayout!!.addView(mouseCoin!!)
        mouseCoin!!.layoutParams =mouseCoinParams
        mouseCoin!!.setBackgroundResource(R.drawable.mousecoin)
        mouseCoin!!.setOnClickListener {
            AudioController.coinEarned()
            if (mInterstitialAd!!.isLoaded) {
                mInterstitialAd!!.show()
            }
        }
        mouseCoin!!.isEnabled = false
    }

    fun getWatchAdButton():CButton {
        return watchAdButton!!
    }

    fun getMouseCoin():Button {
        return mouseCoin!!
    }

    private var fadeInAnimator: ValueAnimator? = null
    private var fadeAnimatorIsRunning:Boolean = false
    private fun fade(In:Boolean, Out:Boolean, Duration:Float, Delay:Float) {
        if (fadeInAnimator != null) {
            if (fadeAnimatorIsRunning) {
                fadeInAnimator!!.cancel()
                fadeAnimatorIsRunning = false
                fadeInAnimator = null
            }
        }
        if (In) {
            fadeInAnimator = ValueAnimator.ofFloat(resultsView!!.alpha, 1f)
            watchAdButton!!.setText("Watch Short Ad to Win ····· s!", false)
        } else if (Out and !In) {
            fadeInAnimator = ValueAnimator.ofFloat(resultsView!!.alpha, 0f)
        }
        fadeInAnimator!!.addUpdateListener {
            val alpha:Float = it.animatedValue as Float
            resultsView!!.alpha = alpha
            gameOverLabel!!.getThis().alpha = alpha
            smilingCat!!.getThis().alpha = alpha
            deadCat!!.getThis().alpha = alpha
            aliveCatCountLabel!!.getThis().alpha = alpha
            deadCatCountLabel!!.getThis().alpha = alpha
            watchAdButton!!.getThis().alpha = alpha
            mouseCoin!!.alpha = alpha
        }
        fadeInAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        fadeInAnimator!!.startDelay = (1000.0f * Delay).toLong()
        fadeInAnimator!!.duration = (1000.0f * Duration).toLong()
        fadeInAnimator!!.start()
        fadeAnimatorIsRunning = true
    }

    private var angle:Float = 0f
    private var increment:Float = 0f
    private var mouseCoinParams:LayoutParams = SettingsMenu.mouseCoinButton!!.getThis().layoutParams as LayoutParams
    fun giveMouseCoins() {
        angle = 52.5f
        increment = 360f / mouseCoinsEarned
        for (iteration in 1..mouseCoinsEarned) {
            val x:Int = ((mouseCoinParams.width * -0.5) + (MainActivity.dWidth * 0.5) +
                    (MainActivity.dWidth * 0.35 * cos(PI * angle / 180f))).toInt()
            val y:Int = ((mouseCoinParams.height * -1.0) + (MainActivity.dHeight * 0.5) +
                    (MainActivity.dWidth * 0.35 * sin(PI * angle / 180f))).toInt()
            MouseCoin(spawnParams = LayoutParams(mouseCoinParams.width,
                mouseCoinParams.height, x, y),
                targetParams = mouseCoinParams,
                isEarned = true)
            angle += increment
        }
    }

    fun fadeIn() {
        watchAdButton!!.getThis().isEnabled = true
        mouseCoin!!.isEnabled = true
        aliveCatCountLabel!!.setText("$savedCatButtonsCount")
        deadCatCountLabel!!.setText("$deadCatButtonsCount")
        savedCatButtonsCount = 0
        deadCatButtonsCount = 0
        fade(true, false, 1f, 0.125f)
    }

    fun fadeOut() {
        mouseCoin!!.isEnabled = false
        watchAdButton!!.getThis().isEnabled = false
        fade(false, true, 1f, 0.125f)
    }
}