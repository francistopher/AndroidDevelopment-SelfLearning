package com.example.savethecat_colormatching.Characters

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.ViewPropertyAnimator
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.CustomViews.CImageView
import com.example.savethecat_colormatching.MainActivity

class Enemy(imageView: ImageView, parentLayout: AbsoluteLayout, params:LayoutParams) {

    private var originalParams:LayoutParams? = null
    private var selectedEnemy:EnemyEnum = EnemyEnum.HAIRBALL

    private var enemyImage:ImageView? = null

    private var lightImageR:Int = 0
    private var darkImageR:Int = 0
    private var verticalSignFloat:Int = randomSignInt()
    private var horizontalSignFloat:Int = randomSignInt()
    init {

        this.enemyImage = imageView
        this.enemyImage!!.layoutParams = params
        parentLayout.addView(imageView)
        setOriginalParams(params)
        setStyle()
    }

    private fun setOriginalParams(params: LayoutParams) {
        originalParams = params
    }

    fun getOriginalParams():LayoutParams {
        return originalParams!!
    }

    fun getThis():ImageView {
        return enemyImage!!
    }

    fun loadImages(lightImageR:Int, darkImageR:Int) {
        this.lightImageR = lightImageR
        this.darkImageR = darkImageR
    }

    private var fadeAnimator: ViewPropertyAnimator? = null
    private var fadeAnimatorIsRunning:Boolean = false
    fun fade(In:Boolean, Out:Boolean, Duration:Float, Delay:Float) {
        if (fadeAnimator != null) {
            if (fadeAnimatorIsRunning) {
                fadeAnimator!!.cancel()
                fadeAnimatorIsRunning = false
                fadeAnimator = null
            }
        }
        if (In) {
            fadeAnimator = enemyImage!!.animate().alpha(1.0f)
            fadeAnimator!!.interpolator = EasingInterpolator(Ease.SINE_IN_OUT)
        }
        if (Out and !In) {
            fadeAnimator = enemyImage!!.animate().alpha(0.0f)
            fadeAnimator!!.interpolator =  EasingInterpolator(Ease.SINE_IN_OUT)
        }
        fadeAnimator!!.startDelay = (1000.0f * Delay).toLong()
        fadeAnimator!!.duration = (1000.0f * Duration).toLong()
        fadeAnimator!!.withStartAction {
            fadeAnimatorIsRunning = true
        }
        fadeAnimator!!.withEndAction {
            if (In and Out) {
                CImageView.stopRotation = true
                this.fade(In = false, Out = true, Duration =(Duration * 0.75f), Delay = 0.0f)
            } else {
                fadeAnimator!!.cancel()
                fadeAnimatorIsRunning = false
                fadeAnimator = null
            }
        }
        if (!fadeAnimatorIsRunning) {
            fadeAnimator!!.start()
        }
    }


    private fun randomSignInt():Int {
        return if ((0..1).random() == 0) {
            -1
        } else {
            1
        }
    }

    private var swayAnimatorSet:AnimatorSet? = null
    private var swayXAnimator:ValueAnimator? = null
    private var swayYAnimator:ValueAnimator? = null
    private var swayY:Int = originalParams!!.y
    private var swayX:Int = originalParams!!.x
    private var isSwaying:Boolean = false
    private var swayBack:Boolean = false
    private var targetX:Int = 0
    private var targetY:Int = 0
    fun sway() {
        if (swayAnimatorSet != null) {
            if (isSwaying) {
                swayAnimatorSet!!.cancel()
                isSwaying = false
                swayAnimatorSet = null
            }
        }
        if (swayBack) {
            targetX = this.originalParams!!.x
            targetY = this.originalParams!!.y
            swayBack = false
        } else {
            targetX = (enemyImage!!.layoutParams as LayoutParams).x + (randomSignInt() *
                    (this.originalParams!!.width / 7.5)).toInt()
            targetY = ((enemyImage!!.layoutParams as LayoutParams).y) + (randomSignInt() *
                    (this.originalParams!!.width / 7.5)).toInt()
            swayBack = true
        }
        swayYAnimator = ValueAnimator.ofInt((enemyImage!!.layoutParams as LayoutParams).y,
            targetY)
        swayYAnimator!!.addUpdateListener {
            swayY = it.animatedValue as Int
            enemyImage!!.layoutParams = LayoutParams(originalParams!!.width, originalParams!!.height,
                swayX, swayY)
        }
        swayXAnimator = ValueAnimator.ofInt((enemyImage!!.layoutParams as LayoutParams).x,
            targetX)
        swayXAnimator!!.addUpdateListener {
            swayX = it.animatedValue as Int
            enemyImage!!.layoutParams = LayoutParams(originalParams!!.width, originalParams!!.height,
                swayX, swayY)
        }
        swayAnimatorSet = AnimatorSet()
        swayAnimatorSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        swayAnimatorSet!!.play(swayXAnimator!!).with(swayYAnimator!!)
        swayAnimatorSet!!.duration = 1750
        isSwaying = true
        swayAnimatorSet!!.doOnEnd {
            sway()
        }
        swayAnimatorSet!!.start()
    }

    fun fadeIn() {
        fade(true, false, 0.5f, 0.125f)
    }

    fun setStyle() {
        if(MainActivity.isThemeDark) {
            enemyImage!!.setImageResource(darkImageR)
        } else {
            enemyImage!!.setImageResource(lightImageR)
        }
    }



}