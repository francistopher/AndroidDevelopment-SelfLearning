package com.example.savethecat_colormatching.ParticularViews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.Characters.CatButton
import com.example.savethecat_colormatching.Characters.CatButtons
import com.example.savethecat_colormatching.CustomViews.CImageView
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R

class AttackMeter(meterView: View, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var originalParams:LayoutParams? = null
    private var meterContext: Context? = null
    private var parentLayout:AbsoluteLayout? = null
    private var meterView:View? = null

    private var catButton:CatButton? = null
    private var enemyImage:CImageView? = null

    private var catButtons: CatButtons? = null

    // Unique properties
    private var didNotInvokeRelease:Boolean = true
    private var enemyPhase:EnemyPhase? = null

    init {
        this.meterView = meterView
        meterContext = meterView.context
        this.parentLayout = parentLayout
        this.meterView!!.layoutParams = params
        parentLayout.addView(this.meterView!!)
        setOriginalParams(params = params)
        setStyle()
        setCornerRadiusAndBorderWidth((params.height / 2.0).toInt(),
            (params.height / 12.0).toInt())
        setupCharacters()
        this.meterView!!.alpha = 0f
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
            shape!!.setStroke(borderWidth, Color.parseColor("#ffd60a"))
        }
        cornerRadius = radius
        shape!!.cornerRadius = radius.toFloat()
        meterView!!.setBackgroundDrawable(shape)
    }

    private fun setupCharacters() {
        setupCatButton()
        setupEnemy()
    }

    private fun setupCatButton() {
        catButton = CatButton(imageButton = ImageButton(meterContext!!), parentLayout = parentLayout!!,
            params = LayoutParams(getOriginalParams().height, getOriginalParams().height,
                getOriginalParams().x + getOriginalParams().width - getOriginalParams().height,
                getOriginalParams().y), backgroundColor = Color.TRANSPARENT)
        catButton!!.doNotStartRotationAndShow()
        catButton!!.getThis().alpha = 0f
    }

    private fun setupEnemy() {
        enemyImage = CImageView(imageView = ImageView(meterContext!!), parentLayout = parentLayout!!,
        params = LayoutParams(getOriginalParams().height, getOriginalParams().height,
            getOriginalParams().x, getOriginalParams().y))
        enemyImage!!.loadImages(R.drawable.lighthairball, R.drawable.darkhairball)
        enemyImage!!.getThis().alpha = 0f
    }

    private var fadeAnimator: ValueAnimator? = null
    private var fadeAnimatorIsRunning:Boolean = false
    private fun fade(In:Boolean, Out:Boolean, Duration:Float, Delay:Float) {
        if (fadeAnimator != null) {
            if (fadeAnimatorIsRunning) {
                fadeAnimator!!.cancel()
                fadeAnimatorIsRunning = false
                fadeAnimator = null
            }
        }
        if (In) {
            fadeAnimator = ValueAnimator.ofFloat(0f, 1f)
        }
        if (Out and !In) {
            fadeAnimator = ValueAnimator.ofFloat(1f, 0f)

        }
        fadeAnimator!!.addUpdateListener {
            val alpha:Float =  (it.animatedValue as Float)
            meterView!!.alpha = alpha
            catButton!!.getThis().alpha = alpha
            catButton!!.setCatImageAlpha(alpha)
            enemyImage!!.getThis().alpha = alpha
        }
        fadeAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        fadeAnimator!!.startDelay = (1000.0f * Delay).toLong()
        fadeAnimator!!.duration = (1000.0f * Duration).toLong()
        fadeAnimator!!.doOnEnd {
            if (In and Out) {
                this.fade(In = false, Out = true, Duration = Duration, Delay = 0.0f)
            } else {
                fadeAnimator!!.cancel()
                fadeAnimatorIsRunning = false
                fadeAnimator = null
            }
        }
        if (!fadeAnimatorIsRunning) {
            fadeAnimator!!.start()
        }
        fadeAnimatorIsRunning = true
    }

    fun fadeIn() {
        fade(true, false, 1.0f, 0.125f)
    }

    fun setOriginalParams(params: LayoutParams) {
        originalParams = params
    }

    fun getOriginalParams():LayoutParams {
        return originalParams!!
    }

    private fun lightDominant() {
        meterView!!.setBackgroundColor(Color.BLACK)
    }

    private fun darkDominant() {
        meterView!!.setBackgroundColor(Color.WHITE)
    }

    fun getThis():View {
        return meterView!!
    }

    fun setCatButtons(catButtons: CatButtons) {
        this.catButtons = catButtons
    }

    fun invokeRelease() {
        didNotInvokeRelease = false
    }

    // First rotation
    private fun startRotation(delay:Float) {
         if (enemyPhase == null || didNotInvokeRelease) {
             return
         }
        setupRotationAnimation()
    }

    private fun setupRotationAnimation() {

    }

    fun setStyle() {
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
    }
}