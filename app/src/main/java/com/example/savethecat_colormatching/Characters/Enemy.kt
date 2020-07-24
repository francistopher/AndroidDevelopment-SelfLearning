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

    /*
        Function fades the enemy into the screen or
        fade the enemy out of the screen
     */
    private var fadeAnimator: ViewPropertyAnimator? = null
    private var fadeAnimatorIsRunning:Boolean = false
    fun fade(In:Boolean, Out:Boolean, Duration:Float, Delay:Float) {
        /*
            If the fading animator is running, cancel it
         */
        if (fadeAnimator != null) {
            if (fadeAnimatorIsRunning) {
                fadeAnimator!!.cancel()
                fadeAnimatorIsRunning = false
                fadeAnimator = null
            }
        }
        // Set the animator to have the enemy fade in or out
        if (In) {
            fadeAnimator = enemyImage!!.animate().alpha(1.0f)
            fadeAnimator!!.interpolator = EasingInterpolator(Ease.SINE_IN_OUT)
        }
        if (Out and !In) {
            fadeAnimator = enemyImage!!.animate().alpha(0.0f)
            fadeAnimator!!.interpolator =  EasingInterpolator(Ease.SINE_IN_OUT)
        }
        // Setup the properties for the animator
        fadeAnimator!!.startDelay = (1000.0f * Delay).toLong()
        fadeAnimator!!.duration = (1000.0f * Duration).toLong()
        fadeAnimator!!.withStartAction {
            fadeAnimatorIsRunning = true
        }
        // If the animator was assigned to fade in and out, fade out after fading in
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

    /*
        Translates the hairball to and from from the cat button
        to its original position
     */
    private var translateToCatButton:AnimatorSet? = null
    private var translateX:ValueAnimator? = null
    private var translateY:ValueAnimator? = null
    private var translatingToCatAndBack:Boolean = false
    fun translateToCatAndBack(targetX:Int, targetY:Int) {
        // Stop the hairball from swinging back and forth
        translatingToCatAndBack = true
        swayAnimatorSet!!.cancel()
        // If the hairball is translating to the cat button cancel it
        if (translateToCatButton != null) {
            translateToCatButton!!.cancel()
        }
        // Translate the hairball to and from the the specified position
        this.targetX = (targetX - ((getOriginalParams().width * 0.5))).toInt()
        this.targetY = (targetY - ((getOriginalParams().height * 0.5))).toInt()
        translateX = ValueAnimator.ofInt(getOriginalParams().x, this.targetX)
        translateX!!.addUpdateListener {
            swayX = (it.animatedValue as Int)
            enemyImage!!.layoutParams = LayoutParams(getOriginalParams().width,
                getOriginalParams().height, swayX, swayY)
        }
        // Initialize the properties of the animator
        translateY = ValueAnimator.ofInt(getOriginalParams().y, this.targetY)
        translateY!!.addUpdateListener {
            swayY = (it.animatedValue as Int)
            enemyImage!!.layoutParams = LayoutParams(getOriginalParams().width,
                getOriginalParams().height, swayX, swayY)
        }
        translateToCatButton = AnimatorSet()
        translateToCatButton!!.play(translateX!!).with(translateY!!)
        translateToCatButton!!.duration = 250
        translateToCatButton!!.start()
        // After the hairball attack is done, just sway back and forth
        translateToCatButton!!.doOnEnd {
            translatingToCatAndBack = false
            sway()
        }
    }

    /*
        Translates the hair ball back and forth
        a short distance from its original position
     */
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
        // If the hairball is already swaying, cancel the swaying
        if (swayAnimatorSet != null) {
            if (isSwaying) {
                swayAnimatorSet!!.cancel()
                isSwaying = false
                swayAnimatorSet = null
            }
        }
        // If the hair ball is swaying to one side, sway to the other
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
        // Set the properties for the sway animator
        swayAnimatorSet = AnimatorSet()
        swayAnimatorSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        swayAnimatorSet!!.play(swayXAnimator!!).with(swayYAnimator!!)
        swayAnimatorSet!!.duration = 1750
        isSwaying = true
        // Continue swaying
        swayAnimatorSet!!.doOnEnd {
            if (!translatingToCatAndBack) {
                sway()
            }
        }
        swayAnimatorSet!!.start()
    }

    fun fadeIn() {
        fade(true, false, 0.5f, 0.125f)
    }

    // Set the hair ball color based on the current theme
    fun setStyle() {
        if(MainActivity.isThemeDark) {
            enemyImage!!.setBackgroundResource(darkImageR)
        } else {
            enemyImage!!.setBackgroundResource(lightImageR)
        }
    }
}