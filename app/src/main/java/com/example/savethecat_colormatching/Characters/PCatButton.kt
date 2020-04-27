package com.example.savethecat_colormatching.Characters

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.CustomViews.CButton
import com.example.savethecat_colormatching.HeaderViews.SettingsMenu
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R

class PCatButton(button: Button, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var originalParams: LayoutParams? = null
    private var shrunkParams: LayoutParams? = null

    private var catButton: Button? = null
    private var originalBackgroundColor: Int = 0
    private var imageButton: CButton? = null

    private var buttonContext: Context? = null

    private var buttonLayout: AbsoluteLayout? = null
    private var parentLayout: AbsoluteLayout? = null

    private var doNotStartImageRotation: Boolean = false

    var cat:Cat = Cat.STANDARD
    private var catState:CatState = CatState.SMILING

    var isPodded: Boolean = false
    var isAlive: Boolean = true

    init {
        this.catButton = button
        this.buttonContext = button.context
        this.buttonLayout = AbsoluteLayout(buttonContext)
        this.catButton!!.layoutParams = params
        this.parentLayout = parentLayout
        parentLayout.addView(this.catButton)
        setOriginalParams(params = params)
        setShrunkParams()
        setupImageView()
        hide()
    }

    private fun hide() {
        this.imageButton!!.getThis().alpha = 0f
        this.catButton!!.alpha = 0f
    }

    fun show() {
        this.imageButton!!.getThis().alpha = 1f
        this.catButton!!.alpha = 1f
    }

    private var fadeInAnimator: ValueAnimator? = null
    private var fadeAnimatorIsRunning: Boolean = false
    fun fade(In: Boolean, Out: Boolean, Duration: Float, Delay: Float) {
        if (fadeInAnimator != null) {
            if (fadeAnimatorIsRunning) {
                fadeInAnimator!!.cancel()
                fadeAnimatorIsRunning = false
                fadeInAnimator = null
            }
        }
        if (In) {
            fadeInAnimator = ValueAnimator.ofFloat(imageButton!!.getThis().alpha, 1f)
        } else if (Out and !In) {
            fadeInAnimator = ValueAnimator.ofFloat(imageButton!!.getThis().alpha, 0f)
        }
        fadeInAnimator!!.addUpdateListener {
            imageButton!!.getThis().alpha = it.animatedValue as Float
            catButton!!.alpha = it.animatedValue as Float
        }
        fadeInAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        fadeInAnimator!!.startDelay = (1000.0f * Delay).toLong()
        fadeInAnimator!!.duration = (1000.0f * Duration).toLong()
        fadeInAnimator!!.start()
        fadeAnimatorIsRunning = true
    }

    private var shape: GradientDrawable? = null
    private var borderWidth: Int = 0
    private var cornerRadius: Int = 0
    fun setCornerRadiusAndBorderWidth(radius: Int, borderWidth: Int) {
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
        catButton!!.setBackgroundDrawable(shape)
    }

    fun getThisImage():Button {
        return imageButton!!.getThis()
    }


    private fun setOriginalParams(params: LayoutParams) {
        originalParams = params
    }

    fun getOriginalParams(): LayoutParams {
        return this.originalParams!!
    }

    private fun setShrunkParams() {
        shrunkParams = LayoutParams(originalParams!!.x / 2, originalParams!!.y / 2, 1, 1)
    }

    fun getThis(): Button {
        return catButton!!
    }

    private fun setupImageView() {
        imageButton = CButton(button = Button(buttonContext!!),
            parentLayout = parentLayout!!, params = originalParams!!)
        setStyle()
        startImageRotation()
    }

    fun setStyle() {
       when (cat) {
           Cat.STANDARD -> {
               if (catState == CatState.SMILING) {
                   imageButton!!.loadImages(
                       R.drawable.lightsmilingcat,
                       R.drawable.darksmilingcat)
               } else if (catState == CatState.CHEERING) {
                   imageButton!!.loadImages(
                       R.drawable.lightcheeringcat,
                       R.drawable.darkcheeringcat)
               } else if (catState == CatState.DEAD) {
                   imageButton!!.loadImages(
                       R.drawable.lightdeadcat,
                       R.drawable.darkdeadcat)
               }
           }
           Cat.BREADING -> {
               if (catState == CatState.SMILING) {
                   imageButton!!.loadImages(
                       R.drawable.lightbreadingsmilingcat,
                       R.drawable.darkbreadingsmilingcat)
               } else if (catState == CatState.CHEERING) {
                   imageButton!!.loadImages(
                       R.drawable.lightbreadingcheeringcat,
                       R.drawable.darkbreadingcheeringcat)
               } else if (catState == CatState.DEAD) {
                   imageButton!!.loadImages(
                       R.drawable.lightbreadingdeadcat,
                       R.drawable.darkbreadingdeadcat)
               }
           }
           Cat.TACO -> {
               if (catState == CatState.SMILING) {
                   imageButton!!.loadImages(
                       R.drawable.lighttacosmilingcat,
                       R.drawable.darktacosmilingcat)
               } else if (catState == CatState.CHEERING) {
                   imageButton!!.loadImages(
                       R.drawable.lighttacocheeringcat,
                       R.drawable.darktacocheeringcat)
               } else if (catState == CatState.DEAD) {
                   imageButton!!.loadImages(
                       R.drawable.lighttacodeadcat,
                       R.drawable.darktacodeadcat)
               }
           }
           Cat.EGYPTIAN -> {
               if (catState == CatState.SMILING) {
                   imageButton!!.loadImages(
                       R.drawable.lightegyptiansmilingcat,
                       R.drawable.darkegyptiansmilingcat)
               } else if (catState == CatState.CHEERING) {
                   imageButton!!.loadImages(
                       R.drawable.lightegyptiancheeringcat,
                       R.drawable.darkegyptiancheeringcat)
               } else if (catState == CatState.DEAD) {
                   imageButton!!.loadImages(
                       R.drawable.lightegyptiandeadcat,
                       R.drawable.darkegyptiandeadcat)
               }
           }
           Cat.SUPER -> {
               if (catState == CatState.SMILING) {
                   imageButton!!.loadImages(
                       R.drawable.lightsupersmilingcat,
                       R.drawable.darksupersmilingcat)
               } else if (catState == CatState.CHEERING) {
                   imageButton!!.loadImages(
                       R.drawable.lightsupercheeringcat,
                       R.drawable.darksupercheeringcat)
               } else if (catState == CatState.DEAD) {
                   imageButton!!.loadImages(
                       R.drawable.lightsuperdeadcat,
                       R.drawable.darksuperdeadcat)
               }
           }
           Cat.CHICKEN -> {
               if (catState == CatState.SMILING) {
                   imageButton!!.loadImages(
                       R.drawable.lightchickensmilingcat,
                       R.drawable.darkchickensmilingcat)
               } else if (catState == CatState.CHEERING) {
                   imageButton!!.loadImages(
                       R.drawable.lightchickencheeringcat,
                       R.drawable.darkchickencheeringcat)
               } else if (catState == CatState.DEAD) {
                   imageButton!!.loadImages(
                       R.drawable.lightchickendeadcat,
                       R.drawable.darkchickendeadcat)
               }
           }
           Cat.COOL -> {
               if (catState == CatState.SMILING) {
                   imageButton!!.loadImages(
                       R.drawable.lightcoolsmilingcat,
                       R.drawable.darkcoolsmilingcat)
               } else if (catState == CatState.CHEERING) {
                   imageButton!!.loadImages(
                       R.drawable.lightcoolcheeringcat,
                       R.drawable.darkcoolcheeringcat)
               } else if (catState == CatState.DEAD) {
                   imageButton!!.loadImages(
                       R.drawable.lightcooldeadcat,
                       R.drawable.darkcooldeadcat)
               }
           }
           Cat.NINJA -> {
               if (catState == CatState.SMILING) {
                   imageButton!!.loadImages(
                       R.drawable.lightninjasmilingcat,
                       R.drawable.darkninjasmilingcat)
               } else if (catState == CatState.CHEERING) {
                   imageButton!!.loadImages(
                       R.drawable.lightninjacheeringcat,
                       R.drawable.darkninjacheeringcat)
               } else if (catState == CatState.DEAD) {
                   imageButton!!.loadImages(
                       R.drawable.lightninjadeadcat,
                       R.drawable.darkninjadeadcat)
               }
           }
           Cat.FAT -> {
               if (catState == CatState.SMILING) {
                   imageButton!!.loadImages(
                       R.drawable.lightfatsmilingcat,
                       R.drawable.darkfatsmilingcat)
               } else if (catState == CatState.CHEERING) {
                   imageButton!!.loadImages(
                       R.drawable.lightfatcheeringcat,
                       R.drawable.darkfatcheeringcat)
               } else if (catState == CatState.DEAD) {
                   imageButton!!.loadImages(
                       R.drawable.lightfatdeadcat,
                       R.drawable.darkfatdeadcat)
               }
           }
       }
       setCornerRadiusAndBorderWidth(getOriginalParams().height / 5, 0)
    }

    var imageRotationAnimator: ValueAnimator? = null
    var isImageRotating: Boolean = false
    var rotateImageToRight: Boolean = true
    var stopImageRotation: Boolean = false
    private fun startImageRotation() {
        if (doNotStartImageRotation) {
            imageButton!!.getThis().rotation = 0f
            return
        }
        if (imageRotationAnimator != null) {
            if (isImageRotating) {
                imageRotationAnimator!!.cancel()
                isImageRotating = false
                imageRotationAnimator = null
            }
        }
        imageRotationAnimator = if (rotateImageToRight) {
            ValueAnimator.ofFloat(-90f, 90f)
        } else {
            ValueAnimator.ofFloat(90f, -90f)
        }
        imageRotationAnimator!!.addUpdateListener {
            imageButton!!.getThis().rotation = (it.animatedValue as Float)
        }
        imageRotationAnimator!!.duration = 1750
        imageRotationAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        isImageRotating = true
        imageRotationAnimator!!.start()
        imageRotationAnimator!!.doOnEnd {
            if (!stopImageRotation) {
                startImageRotation()
                rotateImageToRight = !rotateImageToRight
            }
        }
    }

    private fun earnMouseCoin() {
        val mcSettingsButton:LayoutParams = SettingsMenu.mouseCoinButton!!.
        getThis().layoutParams as LayoutParams
        MouseCoin(spawnParams = LayoutParams(mcSettingsButton.width, mcSettingsButton.height,
            (getOriginalParams().x + (getOriginalParams().width * 0.5) -
                    (mcSettingsButton.width * 0.5)).toInt(),
            (getOriginalParams().y + (getOriginalParams().height * 0.5) -
                    (mcSettingsButton.height * 0.5)).toInt()), targetParams = mcSettingsButton,
            isEarned = true)
    }
}