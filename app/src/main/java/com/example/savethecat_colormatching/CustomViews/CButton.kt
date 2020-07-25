package com.example.savethecat_colormatching.CustomViews

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.ViewPropertyAnimator
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.ParticularViews.BoardGame


class CButton(button: Button, parentLayout: AbsoluteLayout, params: LayoutParams) {

    var isInverted: Boolean = false

    private var originalParams: LayoutParams? = null
    private var shrunkParams: LayoutParams? = null
    private var minHeight:Int = 0

    private var button: Button? = null
    var backgroundColor: Int? = null
    var targetBackgroundColor: Int? = null
    var shrinkType: ShrinkType = ShrinkType.mid

    var isSelected:Boolean = false
    var willBeShrunk:Boolean = false

    private var parentLayout:AbsoluteLayout? = null
    var isBorderGold:Boolean = false

    var isTwoPlayerButton:Boolean = false

    private var lightImageR: Int = 0
    private var darkImageR: Int = 0

    init {
        this.button = button
        this.button!!.layoutParams = params
        this.minHeight = params.height
        this.parentLayout = parentLayout
        parentLayout.addView(button)
        setOriginalParams(params)
        setShrunkParams()
        setStyle()
        this.button!!.alpha = 0f
    }

    fun getThis(): Button {
        return this.button!!
    }

    fun getParentLayout():AbsoluteLayout {
        return parentLayout!!
    }

    fun setTextSize(size: Float) {
        button!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    }

    fun getText(): CharSequence? {
        return button!!.text
    }

    fun setText(text: String, caps: Boolean) {
        button!!.isAllCaps = caps
        button!!.typeface = Typeface.createFromAsset(
            MainActivity.rootView!!.context.assets,
            "SleepyFatCat.ttf"
        )
        button!!.text = text
    }

    /*
        Setup the corner radius and the border width of the button
     */
    private var shape: GradientDrawable? = null
    private var borderWidth:Int = 0
    private var cornerRadius:Int = 0
    fun setCornerRadiusAndBorderWidth(radius: Int, borderWidth: Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        // Draw the background shape
        if (backgroundColor != null) {
            shape!!.setColor(backgroundColor!!)
        } else if (targetBackgroundColor != null) {
            shape!!.setColor(targetBackgroundColor!!)
        } else {
            if (MainActivity.isThemeDark){
                shape!!.setColor(Color.BLACK)
            } else {
                shape!!.setColor(Color.WHITE)
            }
        }
        // Draw the border of the button
        if (borderWidth > 0) {
            this.borderWidth = borderWidth
            if (isBorderGold) {
                shape!!.setStroke(borderWidth, Color.parseColor("#ffd60a"))
            } else {
                if (MainActivity.isThemeDark) {
                    shape!!.setStroke(borderWidth, Color.WHITE)
                } else {
                    shape!!.setStroke(borderWidth, Color.BLACK)
                }
            }
        }
        // Setup the corner radius of the button
        cornerRadius = radius
        shape!!.cornerRadius = radius.toFloat()
        button!!.setBackgroundDrawable(shape)
    }

    /*
        Scale the width of the button
     */
    private var growWidthAndChangeColor: AnimatorSet? = null
    private var growWidthAnimator: ValueAnimator? = null
    private var transitionColorAnimator: ValueAnimator? = null
    private var transitionXAnimator: ValueAnimator? = null
    private var fadeOutAnimator:ValueAnimator? = null
    var growWidthAndChangeColorIsRunning: Boolean = false
    fun growWidth(width: Float) {
        // If the width is scaling, cancel the animator
        if (growWidthAndChangeColor != null) {
            if (growWidthAndChangeColorIsRunning) {
                growWidthAndChangeColor!!.cancel()
                growWidthAndChangeColorIsRunning = false
                growWidthAndChangeColor = null
            }
        }
        // Displace x transition
        if (isTwoPlayerButton) {
            transitionXAnimator = ValueAnimator.ofInt(getOriginalParams().x,
                BoardGame.singlePlayerButton!!.getOriginalParams().x)
            transitionXAnimator!!.addUpdateListener {
                button!!.layoutParams = LayoutParams(
                    (button!!.layoutParams as LayoutParams).width,
                    getOriginalParams().height,
                    (it.animatedValue as Int),
                    getOriginalParams().y
                )
            }
        }
        // Grow width transition
        growWidthAnimator = ValueAnimator.ofFloat(originalParams!!.width.toFloat(), width)
        growWidthAnimator!!.addUpdateListener {
            button!!.layoutParams = LayoutParams(
                (it.animatedValue as Float).toInt(),
                getOriginalParams().height,
                (button!!.layoutParams as LayoutParams).x,
                getOriginalParams().y
            )
        }
        // Color animator transition
        transitionColorAnimator = ValueAnimator.ofArgb(getBackgroundColor(), targetBackgroundColor!!)
        transitionColorAnimator!!.addUpdateListener {
            button!!.setBackgroundColor(it.animatedValue as Int)
            targetBackgroundColor = (it.animatedValue as Int)
            setCornerRadiusAndBorderWidth((originalParams!!.height / 5.0).toInt(), borderWidth)
        }
        growWidthAndChangeColor = AnimatorSet()
        growWidthAndChangeColor!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        if (isTwoPlayerButton) {
            growWidthAndChangeColor!!.play(growWidthAnimator!!).
            with(transitionColorAnimator!!).with(transitionXAnimator!!)
        } else {
            growWidthAndChangeColor!!.play(growWidthAnimator!!).
            with(transitionColorAnimator!!)
        }
        // Setup animator properties
        growWidthAndChangeColor!!.duration = 1000
        growWidthAndChangeColor!!.startDelay = 125
        growWidthAndChangeColorIsRunning = true
        growWidthAndChangeColor!!.start()
        // Fade the button out after scaling width
        growWidthAndChangeColor!!.doOnEnd {
            growWidthAndChangeColorIsRunning = false
            fadeOutAnimator = ValueAnimator.ofFloat(1f, 0f)
            fadeOutAnimator!!.addUpdateListener{
                button!!.alpha = it.animatedValue as Float
            }
            fadeOutAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
            fadeOutAnimator!!.duration = 750
            fadeOutAnimator!!.start()
            // Remove button from screen after fading out
            fadeOutAnimator!!.doOnEnd {
                parentLayout!!.removeView(getThis())
            }
        }
    }

    var grow:Boolean = true
    fun growUnGrow(duration: Float) {
        grow(duration, 0f)
        grow = !grow
    }

    /*
        Dilates both the width and the height of the button
        to appear to grow
     */
    private var growAnimatorSet:AnimatorSet? = null
    private var growHeightAnimator:ValueAnimator? = null
    private var isGrowing:Boolean = false
    private var width:Float = 0f
    private var height:Float = 0f
    private var y:Float = 0f
    fun grow(duration:Float, delay:Float) {
        // Cancel shrink animation if its running
        if (shrinkAnimationSet != null) {
            shrinkAnimationSet!!.cancel()
        }
        // Cancel grow animation if its running
        if (growAnimatorSet != null) {
            if (isGrowing) {
                growAnimatorSet!!.cancel()
                isGrowing = false
                growAnimatorSet = null
            }
        }
        // Grow or shrink the button
        if (grow) {
            growWidthAnimator = ValueAnimator.ofFloat((button!!.layoutParams as LayoutParams).width.toFloat(), originalParams!!.width.toFloat())
            growHeightAnimator = ValueAnimator.ofFloat((button!!.layoutParams as LayoutParams).height.toFloat(), originalParams!!.height.toFloat())
        } else {
            growWidthAnimator = ValueAnimator.ofFloat((button!!.layoutParams as LayoutParams).width.toFloat(), 1f)
            growHeightAnimator = ValueAnimator.ofFloat((button!!.layoutParams as LayoutParams).width.toFloat(), 1f)
        }
        // Dilate the width and the height of the button
        growWidthAnimator!!.addUpdateListener {
            width = (it.animatedValue as Float)
            x = ((originalParams!!.x + (originalParams!!.width * 0.5) - (width * 0.5)).toFloat())
            button!!.layoutParams = LayoutParams(width.toInt(), height.toInt(), x.toInt(), y.toInt())
        }
        growHeightAnimator!!.addUpdateListener {
            height = (it.animatedValue as Float)
            y = (originalParams!!.y + (originalParams!!.height * 0.5) - (height * 0.5)).toFloat()
            button!!.layoutParams = LayoutParams(width.toInt(), height.toInt(), x.toInt(), y.toInt())
        }
        // Set the properties for animation
        growAnimatorSet = AnimatorSet()
        growAnimatorSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        growAnimatorSet!!.play(growHeightAnimator!!).with(growWidthAnimator!!)
        growAnimatorSet!!.duration = (1000 * duration).toLong()
        growAnimatorSet!!.startDelay = (1000 * delay).toLong()
        isGrowing = true
        growAnimatorSet!!.start()
    }

    /*
        Button transitions to the selected position and size
     */
    private var translateAnimatorSet:AnimatorSet? = null
    private var translateWidthAnimator:ValueAnimator? = null
    private var selectAnimator:ValueAnimator? = null
    private var isSelectRunning:Boolean = false
    private var targetWidth:Float = -1f
    private var targetX:Float = -1f
    fun select(targetX:Float, targetWidth:Float) {
        // Cancel the select animation if its already running
        if (selectAnimator != null) {
            if (isSelectRunning) {
                selectAnimator!!.cancel()
                isSelectRunning = false
                selectAnimator = null
            }
        }
        // Update the size of the button
        this.targetWidth = targetWidth
        this.targetX = targetX
        selectAnimator = ValueAnimator.ofFloat((button!!.layoutParams as LayoutParams).
        height.toFloat(), minHeight * 1.275f)
        selectAnimator!!.addUpdateListener {
            height = (it.animatedValue as Float)
            y = originalParams!!.y - (((it.animatedValue as Float) - originalParams!!.
            height.toFloat()) * 0.5f)
                button!!.layoutParams = LayoutParams(width.toInt(), height.toInt(), x.toInt(),
                    y.toInt())
        }
        // Translate the position of the button
        if (targetWidth != -1f && targetX != -1f) {
            translateXAnimator = ValueAnimator.ofFloat((button!!.layoutParams as
                LayoutParams).x.toFloat(), targetX)
            translateWidthAnimator = ValueAnimator.ofFloat((button!!.layoutParams as
                    LayoutParams).width.toFloat(), targetWidth)
        } else {
            translateXAnimator = ValueAnimator.ofFloat((button!!.layoutParams as
                    LayoutParams).x.toFloat(), originalParams!!.x.toFloat())
            translateWidthAnimator = ValueAnimator.ofFloat((button!!.layoutParams as
                    LayoutParams).width.toFloat(), originalParams!!.width.toFloat())
        }
        translateXAnimator!!.addUpdateListener {
            x = (it.animatedValue as Float)
            button!!.layoutParams = LayoutParams(width.toInt(), height.toInt(), x.toInt(), y.toInt())
        }
        translateWidthAnimator!!.addUpdateListener {
            width = (it.animatedValue as Float)
            button!!.layoutParams = LayoutParams(width.toInt(), height.toInt(), x.toInt(), y.toInt())
        }
        // Create the properties for the animation
        translateAnimatorSet = AnimatorSet()
        translateAnimatorSet!!.play(selectAnimator!!).with(translateWidthAnimator!!).
            with(translateXAnimator!!)
        translateAnimatorSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        translateAnimatorSet!!.startDelay = 125
        translateAnimatorSet!!.duration = 500
        isSelectRunning = true
        translateAnimatorSet!!.start()
    }

    /*
        Translate and transform the button to be unselected
     */
    private var unSelectAnimatorSet:AnimatorSet? = null
    private var unSelectAnimator:ValueAnimator? = null
    private var isUnSelectRunning:Boolean = false
    fun unSelect() {
        // If the unselected animator is running, cancel it
        if (unSelectAnimator != null) {
            if (isUnSelectRunning) {
                unSelectAnimator!!.cancel()
                isUnSelectRunning = false
                unSelectAnimator = null
            }
        }
        // Dilate the height and the y position
        unSelectAnimator = ValueAnimator.ofFloat((button!!.layoutParams as LayoutParams).
        height.toFloat(), minHeight.toFloat())
        unSelectAnimator!!.addUpdateListener {
            height = (it.animatedValue as Float)
            y = originalParams!!.y - (((it.animatedValue as Float) -
                    originalParams!!.height.toFloat()) * 0.5f)
            button!!.layoutParams = LayoutParams(width.toInt(), height.toInt(),
                x.toInt(), y.toInt())
        }
        // Translate the x position and dilate the width
        translateXAnimator = ValueAnimator.ofFloat((button!!.layoutParams as
                LayoutParams).x.toFloat(), originalParams!!.x.toFloat())
        translateWidthAnimator = ValueAnimator.ofFloat((button!!.layoutParams as
                LayoutParams).width.toFloat(), originalParams!!.width.toFloat())
        translateXAnimator!!.addUpdateListener {
            x = (it.animatedValue as Float)
            button!!.layoutParams = LayoutParams(width.toInt(), height.toInt(), x.toInt(), y.toInt())
        }
        translateWidthAnimator!!.addUpdateListener {
            width = (it.animatedValue as Float)
            button!!.layoutParams = LayoutParams(width.toInt(), height.toInt(), x.toInt(), y.toInt())
        }
        // Setup the properties for the animation set
        unSelectAnimatorSet = AnimatorSet()
        unSelectAnimatorSet!!.play(unSelectAnimator!!).with(translateXAnimator!!).
        with(translateWidthAnimator!!)
        unSelectAnimatorSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        unSelectAnimatorSet!!.startDelay = 125
        unSelectAnimatorSet!!.duration = 500
        isUnSelectRunning = true
        unSelectAnimatorSet!!.start()
    }

    fun fadeIn() {
        fade(true, false, 1f, 0.125f)
    }

    /*
        Dilate and translate the button to shrink the button
     */
    private var x: Float = 0f
    private var duration: Float = 0f
    private var translateXAnimator: ValueAnimator? = null
    private var shrinkWidthAnimator: ValueAnimator? = null
    private var shrinkAnimationSet: AnimatorSet? = null
    private var shrinkAnimationSetIsRunning: Boolean = false
    fun shrink() {
        grow = true
        // If the shrink animation is running, cancel it
        if (shrinkAnimationSet != null) {
            if (shrinkAnimationSetIsRunning) {
                shrinkAnimationSet!!.cancel()
                shrinkAnimationSetIsRunning = false
                shrinkAnimationSet = null
            }
        }
        // Select from which position the button shrinks
        when (shrinkType) {
            ShrinkType.left -> {
                x = ((button!!.layoutParams as LayoutParams).x).toFloat()
                duration = 750f
            }
            ShrinkType.mid -> {
                x = ((button!!.layoutParams as LayoutParams).x +
                        (button!!.layoutParams as LayoutParams).width * 0.5).toFloat()
                duration = 500f
            }
            ShrinkType.right -> {
                x = ((button!!.layoutParams as LayoutParams).x + (button!!.layoutParams as LayoutParams).width).toFloat()
                duration = 750f
            }
        }
        // Translate the button's x position
        translateXAnimator = ValueAnimator.ofFloat((button!!.layoutParams as LayoutParams).x.toFloat(), x)
        translateXAnimator!!.addUpdateListener {
            x = (it.animatedValue as Float)
            button!!.layoutParams =  LayoutParams(width.toInt(), (button!!.layoutParams as LayoutParams).height,
                x.toInt(), (button!!.layoutParams as LayoutParams).y)
        }
        // Dilate the width of the button
        shrinkWidthAnimator = ValueAnimator.ofFloat((button!!.layoutParams as LayoutParams).width.toFloat(), 0f)
        shrinkWidthAnimator!!.addUpdateListener {
            width = (it.animatedValue as Float)
            button!!.layoutParams = LayoutParams(width.toInt(), (button!!.layoutParams as LayoutParams).height,
                x.toInt(),(button!!.layoutParams as LayoutParams).y)
        }
        shrinkAnimationSet = AnimatorSet()
        shrinkAnimationSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        shrinkAnimationSet!!.play(translateXAnimator!!).with(shrinkWidthAnimator)
        shrinkAnimationSet!!.duration = duration.toLong()
        shrinkAnimationSet!!.start()
        // Remove the button from the screen after it has shrunk
        shrinkAnimationSet!!.doOnEnd {
            parentLayout!!.removeView(this.getThis())
        }
    }

    /*
        Make the button appear or disappear
     */
    private var fadeAnimator: ViewPropertyAnimator? = null
    private var fadeAnimatorIsRunning:Boolean = false
    fun fade(In:Boolean, Out:Boolean, Duration:Float, Delay:Float) {
        // If the fading animation is runnning, cancel it
        if (fadeAnimator != null) {
            if (fadeAnimatorIsRunning) {
                fadeAnimator!!.cancel()
                fadeAnimatorIsRunning = false
                fadeAnimator = null
            }
        }
        // Fade the button in or out
        if (In) {
            fadeAnimator = button!!.animate().alpha(1.0f)
            fadeAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        }
        if (Out and !In) {
            fadeAnimator = button!!.animate().alpha(0.0f)
            fadeAnimator!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        }
        // Setup the fade animation properties
        fadeAnimator!!.startDelay = (1000.0f * Delay).toLong()
        fadeAnimator!!.duration = (1000.0f * Duration).toLong()
        fadeAnimator!!.withStartAction {
            fadeAnimatorIsRunning = true
        }
        // Decide whether to fade in our out or just stop after fading in
        fadeAnimator!!.withEndAction {
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
    }

    /*
        Returns the current background color of the button
        based on the theme
     */
    private fun getBackgroundColor(): Int {
        if (backgroundColor != null) {
            return backgroundColor!!
        } else {
            return if (MainActivity.isThemeDark) {
                if (isInverted) {
                    Color.WHITE
                } else {
                    Color.BLACK
                }
            } else {
                if (isInverted) {
                    Color.BLACK
                } else {
                    Color.WHITE
                }
            }
        }
    }

    fun shrunk() {
        button!!.layoutParams = shrunkParams!!
    }

    fun setOriginalParams(params: LayoutParams) {
        originalParams = params
    }

    fun getOriginalParams(): LayoutParams {
        return originalParams!!
    }

    private fun setShrunkParams() {
        shrunkParams = LayoutParams(0, 0,originalParams!!.x / 2, originalParams!!.y / 2)
    }

    /*
        Updates the style to a light theme
     */
    private fun lightDominant() {
        if (backgroundColor == null) {
            button!!.setBackgroundColor(Color.BLACK)
        } else {
            button!!.setBackgroundColor(backgroundColor!!)
        }
        button!!.setTextColor(Color.WHITE)
    }

    /*
        Updates the style to a dark theme
     */
    private fun darkDominant() {
        if (backgroundColor == null) {
            button!!.setBackgroundColor(Color.WHITE)
        } else {
            button!!.setBackgroundColor(backgroundColor!!)
        }
        button!!.setTextColor(Color.BLACK)
    }

    /*
        Draws the image selected by the user based on the theme
     */
    fun loadImages(lightImageR:Int, darkImageR:Int) {
        this.lightImageR = lightImageR
        this.darkImageR = darkImageR
        fun lightDominant() {
            button!!.setBackgroundResource(lightImageR)
        }
        fun darkDominant() {
            button!!.setBackgroundResource(darkImageR)
        }
        if (MainActivity.isThemeDark) {
            darkDominant()
        } else {
            lightDominant()
        }
    }

    /*
        Sets the appearance of the button based on the theme
     */
    fun setStyle() {
        if (MainActivity.isThemeDark) {
            if (isInverted) {
                darkDominant()
            } else {
                lightDominant()
            }
        } else {
            if (isInverted) {
                lightDominant()
            } else {
                darkDominant()
            }
        }
        setCornerRadiusAndBorderWidth(cornerRadius, borderWidth)
    }
}
