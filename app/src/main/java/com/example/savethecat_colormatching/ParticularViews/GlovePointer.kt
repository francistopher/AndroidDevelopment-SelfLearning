package com.example.savethecat_colormatching.ParticularViews

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R
import java.util.*

class GlovePointer (view: ImageView,
                    parentLayout: AbsoluteLayout,
                    params: LayoutParams){

    private var originalParams:LayoutParams? = null

    private var gpContext: Context? = null
    private var gpView:ImageView? = null
    private var parentLayout:AbsoluteLayout? = null

    private var lightGloveTapImage:Int = R.drawable.lightglovetap
    private var darkGloveTapImage:Int = R.drawable.darkglovetap

    private var lightGlovePointImage:Int = R.drawable.lightglovepointer
    private var darkGlovePointImage:Int = R.drawable.darkglovepointer

    private var initX:Int = 0
    private var initY:Int = 0
    private var translation:Int = 0

    init {
        this.gpView = view
        gpContext = view.context
        setupOriginalParams(params = params)
        setupParentLayout(parentLayout = parentLayout)
        gpView!!.alpha = 0f
    }

    private fun setupParentLayout(parentLayout: AbsoluteLayout) {
        this.parentLayout = parentLayout
        this.parentLayout!!.addView(gpView!!)
    }

    private fun setupOriginalParams(params:LayoutParams) {
        this.initX = params.x
        this.initY = params.y
        this.translation = (params.width / 3.5).toInt()
        this.gpView!!.layoutParams = params
        this.originalParams = params
    }

    private fun getOriginalParams():LayoutParams {
        return originalParams!!
    }

    fun fadeIn() {
        fade(true, false, 1f, 0.125f)
    }

    fun hide() {
        gpView!!.alpha = 0f
        quitSwaying = true
        swayAnimation!!.cancel()
    }

    private var fadeInAnimator: ValueAnimator? = null
    private var fadeAnimatorIsRunning:Boolean = false
    fun fade(In:Boolean, Out:Boolean, Duration:Float, Delay:Float) {
        if (fadeInAnimator != null) {
            if (fadeAnimatorIsRunning) {
                fadeInAnimator!!.cancel()
                fadeAnimatorIsRunning = false
                fadeInAnimator = null
            }
        }
        if (In) {
            fadeInAnimator = ValueAnimator.ofFloat(0f, 1f)
        } else if (Out and !In) {
            fadeInAnimator = ValueAnimator.ofFloat(1f, 0f)
        }
        fadeInAnimator!!.addUpdateListener {
            gpView!!.alpha = it.animatedValue as Float
        }
        fadeInAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        fadeInAnimator!!.startDelay = (1000.0f * Delay).toLong()
        fadeInAnimator!!.duration = (1000.0f * Duration).toLong()
        fadeInAnimator!!.start()
        fadeAnimatorIsRunning = true
    }

    fun translate(initX:Int, initY:Int) {
        if (quitSwaying) return
        this.initX = initX
        this.initY = initY
        this.translateInstead = true
        this.gpView!!.bringToFront()
    }

    private var swayAnimation:AnimatorSet? = null
    private var swayXAnimation:ValueAnimator? = null
    private var swayYAnimation:ValueAnimator? = null
    private var isSwayingAway:Boolean = false
    private var translateInstead:Boolean = false
    private var quitSwaying:Boolean = false
    fun sway() {
        isSwayingAway = !isSwayingAway
        if (translateInstead) {
            swayXAnimation = ValueAnimator.ofInt(getOriginalParams().x, initX)
            swayYAnimation = ValueAnimator.ofInt(getOriginalParams().y, initY)
        } else if (isSwayingAway) {
            swayXAnimation = ValueAnimator.ofInt(initX, initX - translation)
            swayYAnimation = ValueAnimator.ofInt(initY, initY - translation)
        } else {
            swayXAnimation = ValueAnimator.ofInt(initX - translation, initX)
            swayYAnimation = ValueAnimator.ofInt( initY - translation, initY)
        }
        swayXAnimation!!.addUpdateListener {
            gpView!!.layoutParams = LayoutParams(getOriginalParams().width, getOriginalParams().height,
            it.animatedValue as Int, (gpView!!.layoutParams as LayoutParams).y)
        }
        swayYAnimation!!.addUpdateListener {
            gpView!!.layoutParams = LayoutParams(getOriginalParams().width, getOriginalParams().height,
                (gpView!!.layoutParams as LayoutParams).x, it.animatedValue as Int)
        }
        swayAnimation = AnimatorSet()
        swayAnimation!!.play(swayXAnimation!!).with(swayYAnimation!!)
        swayAnimation!!.duration = 500
        if (isSwayingAway) {
            swayAnimation!!.startDelay = 125
            if (MainActivity.isThemeDark) {
                gpView!!.setBackgroundResource(darkGloveTapImage)
            } else {
                gpView!!.setBackgroundResource(lightGloveTapImage)
            }
        }
        swayAnimation!!.doOnStart {
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    MainActivity.staticSelf!!.runOnUiThread {
                        if (MainActivity.isThemeDark) {
                            gpView!!.setBackgroundResource(darkGlovePointImage)
                        } else {
                            gpView!!.setBackgroundResource(lightGlovePointImage)
                        }
                    }
                }
            }, 125)
        }
        swayAnimation!!.doOnEnd {
            if (!quitSwaying) {
                quitSwaying = false
                if (translateInstead) {
                    translateInstead = false
                    isSwayingAway = true
                }
                sway()
            }
        }
        swayAnimation!!.start()
    }




}