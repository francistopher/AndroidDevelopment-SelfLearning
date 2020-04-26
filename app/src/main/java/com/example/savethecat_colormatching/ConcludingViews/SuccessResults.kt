package com.example.savethecat_colormatching.ConcludingViews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageView
import android.widget.TextView
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.Characters.MouseCoin
import com.example.savethecat_colormatching.CustomViews.CImageView
import com.example.savethecat_colormatching.CustomViews.CLabel
import com.example.savethecat_colormatching.HeaderViews.SettingsMenu
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class SuccessResults(successView: View,
                     parentLayout: AbsoluteLayout,
                     params: LayoutParams) {

    private var successView:View? = null
    private var context: Context? = null
    private var parentLayout:AbsoluteLayout? = null

    private var unitHeight:Int = 0

    private var originalParams:LayoutParams? = null

    private var successLabel:CLabel? = null

    private var smilingCat:CImageView? = null
    private var deadCat:CImageView? = null

    private var aliveCatCountLabel:CLabel? = null
    private var deadCatCountLabel:CLabel? = null

    init {
        setupView(successView)
        setupOriginalParams(params)
        setupParentLayout(parentLayout)
        setStyle()
        setCornerRadiusAndBorderWidth(radius = params.height / 6,
            borderWidth = params.height / 60)
    }

    fun setupContents() {
        setupTitleView()
        setupSmilingCat()
        setupDeadCat()
        setupAliveCatCountLabel()
        setupDeadCatCountLabel()
        hideEverything()
    }

    private fun hideEverything() {
        successView!!.alpha = 0f
        successLabel!!.getThis().alpha = 0f
        smilingCat!!.getThis().alpha = 0f
        deadCat!!.getThis().alpha = 0f
        aliveCatCountLabel!!.getThis().alpha = 0f
        deadCatCountLabel!!.getThis().alpha = 0f
    }

    private fun setupSmilingCat() {
        smilingCat = CImageView(imageView = ImageView(context!!), parentLayout = parentLayout!!,
            params = LayoutParams((getOriginalParams().width * 0.625).toInt(),
                (getOriginalParams().width * 0.8125).toInt(), getOriginalParams().x -
                        (getOriginalParams().width * 0.04).toInt(),
                successLabel!!.getOriginalParams().y +
                        (successLabel!!.getOriginalParams().height * 0.25).toInt()))
        smilingCat!!.loadImages(R.drawable.lightsmilingcat, R.drawable.darksmilingcat)
    }

    private fun setupDeadCat() {
        deadCat = CImageView(imageView = ImageView(context!!), parentLayout = parentLayout!!,
            params = LayoutParams((getOriginalParams().width * 0.625).toInt(),
                (getOriginalParams().width * 0.8125).toInt(), getOriginalParams().x +
                        (getOriginalParams().width * 0.5).toInt() -
                        (getOriginalParams().width * 0.095).toInt(),
                successLabel!!.getOriginalParams().y +
                        (successLabel!!.getOriginalParams().height * 0.25).toInt()))
        deadCat!!.loadImages(R.drawable.lightdeadcat, R.drawable.darkdeadcat)
    }

    private fun setupAliveCatCountLabel() {
        aliveCatCountLabel = CLabel(textView = TextView(context), parentLayout = parentLayout!!,
            params = LayoutParams((getOriginalParams().width * 0.5).toInt() - (borderWidth * 2.0).toInt(),
                unitHeight, getOriginalParams().x + (borderWidth * 2.0).toInt(), smilingCat!!.getOriginalParams().y +
                        (smilingCat!!.getOriginalParams().height * 0.775).toInt()))
        aliveCatCountLabel!!.setTextSize(aliveCatCountLabel!!.getOriginalParams().height * 0.2f)
        aliveCatCountLabel!!.setText("${GameResults.savedCatButtonsCount}")
        aliveCatCountLabel!!.isInverted = true
        aliveCatCountLabel!!.setStyle()
    }

    private fun setupDeadCatCountLabel() {
        deadCatCountLabel = CLabel(textView = TextView(context), parentLayout = parentLayout!!,
            params = LayoutParams((getOriginalParams().width * 0.5).toInt() -
                    (borderWidth * 2.0).toInt(), unitHeight,
                getOriginalParams().x + (getOriginalParams().width * 0.5).toInt(),
                deadCat!!.getOriginalParams().y +
                        (deadCat!!.getOriginalParams().height * 0.775).toInt()))
        deadCatCountLabel!!.setTextSize(aliveCatCountLabel!!.getOriginalParams().height * 0.2f)
        deadCatCountLabel!!.setText("${GameResults.deadCatButtonsCount}")
        deadCatCountLabel!!.isInverted = true
        deadCatCountLabel!!.setStyle()
    }

    private fun setupTitleView() {
        successLabel = CLabel(textView = TextView(context!!), parentLayout = parentLayout!!,
            params = LayoutParams((getOriginalParams().width * 0.5).toInt(), (unitHeight * 2.0).toInt(),
                (getOriginalParams().width * 0.25).toInt() + getOriginalParams().x,
                getOriginalParams().y + (unitHeight * 0.4).toInt()))
        successLabel!!.setTextSize(successLabel!!.getOriginalParams().height * 0.1875f)
        successLabel!!.setText("You Won!!!")
        successLabel!!.isInverted = true
        successLabel!!.setStyle()
        successLabel!!.setCornerRadiusAndBorderWidth((getOriginalParams().height / 2.0).toInt(),
            0)
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
        shape!!.cornerRadius = radius.toFloat()
        this.successView!!.setBackgroundDrawable(shape)
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
            fadeInAnimator = ValueAnimator.ofFloat(successView!!.alpha, 1f)
            MainActivity.gameResults!!.getWatchAdButton().setText("Watch Short Ad to Win ····· s!", false)
        } else if (Out and !In) {
            fadeInAnimator = ValueAnimator.ofFloat(successView!!.alpha, 0f)
        }
        fadeInAnimator!!.addUpdateListener {
            val alpha:Float = it.animatedValue as Float
            successView!!.alpha = alpha
            successLabel!!.getThis().alpha = alpha
            smilingCat!!.getThis().alpha = alpha
            deadCat!!.getThis().alpha = alpha
            aliveCatCountLabel!!.getThis().alpha = alpha
            deadCatCountLabel!!.getThis().alpha = alpha
            MainActivity.gameResults!!.getWatchAdButton().getThis().alpha = alpha
            MainActivity.gameResults!!.getMouseCoin().alpha = alpha
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
        increment = 360f / GameResults.mouseCoinsEarned
        for (iteration in 1..GameResults.mouseCoinsEarned) {
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
        MainActivity.gameResults!!.getWatchAdButton().getThis().isEnabled = true
        MainActivity.gameResults!!.getMouseCoin().isEnabled = true
        aliveCatCountLabel!!.setText("${GameResults.savedCatButtonsCount}")
        deadCatCountLabel!!.setText("${GameResults.deadCatButtonsCount}")
        GameResults.savedCatButtonsCount = 0
        GameResults.deadCatButtonsCount = 0
        fade(true, false, 1f, 0.125f)
    }

    fun fadeOut() {
        MainActivity.gameResults!!.getMouseCoin().isEnabled = false
        MainActivity.gameResults!!.getWatchAdButton().getThis().isEnabled = false
        fade(false, true, 1f, 0.125f)
    }

    private fun setupParentLayout(parentLayout: AbsoluteLayout) {
        this.parentLayout = parentLayout
        parentLayout.addView(successView)
    }

    private fun setupView(view:View) {
        this.successView = view
        context = view.context
    }

    fun setupOriginalParams(params: LayoutParams) {
        unitHeight = (params.height / 8.0).toInt()
        successView!!.layoutParams = params
        originalParams = params
    }

    fun getThis():View {
        return successView!!
    }

    fun getOriginalParams():LayoutParams {
        return originalParams!!
    }

    private fun lightDominant() {
        successView!!.setBackgroundColor(Color.BLACK)
    }

    private fun darkDominant() {
        successView!!.setBackgroundColor(Color.WHITE)
    }

    fun setStyle() {
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
    }
}