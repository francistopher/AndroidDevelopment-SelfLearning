package com.example.savethecat_colormatching.Characters

import android.view.ViewPropertyAnimator
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageView
import android.widget.TextView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.example.savethecat_colormatching.Controllers.AudioController
import com.example.savethecat_colormatching.CustomViews.CImageView
import com.example.savethecat_colormatching.MainActivity

class Enemy(imageView: ImageView, parentLayout: AbsoluteLayout, params:LayoutParams) {

    private var originalParams:LayoutParams? = null
    private var selectedEnemy:EnemyEnum = EnemyEnum.HAIRBALL

    private var enemyImage:ImageView? = null

    private var lightImageR:Int = 0
    private var darkImageR:Int = 0

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
            fadeAnimator!!.interpolator = FastOutSlowInInterpolator()
        }
        if (Out and !In) {
            fadeAnimator = enemyImage!!.animate().alpha(0.0f)
            fadeAnimator!!.interpolator = LinearOutSlowInInterpolator()
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

    fun setStyle() {
        if(MainActivity.isThemeDark) {
            enemyImage!!.setImageResource(darkImageR)
        } else {
            enemyImage!!.setImageResource(lightImageR)
        }
    }



}