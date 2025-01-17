package com.example.savethecat_colormatching.HeaderViews

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageButton
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.Characters.CatButton
import com.example.savethecat_colormatching.CustomViews.CImageView
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.ParticularViews.EnemyPhase
import com.example.savethecat_colormatching.R
import java.util.*

class AttackMeter(meterView: View, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var originalParams:LayoutParams? = null
    private var meterContext: Context? = null
    private var parentLayout:AbsoluteLayout? = null
    private var meterView:View? = null

    private var catButton:CatButton? = null
    private var enemyImage:CImageView? = null

    // Unique properties
    private var enemyPhase: EnemyPhase? = null

    private var rotationCheckpoint:Float = 0f

    private var previousDisplacementDuration:Float = 3.5f
    private var displacementDuration:Float = 3.5f
    private var initialEnemyCatDistance:Int = 0

    companion object {
        var didNotInvokeRelease:Boolean = true
    }

    init {
        this.meterView = meterView
        meterContext = meterView.context
        this.parentLayout = parentLayout
        this.meterView!!.layoutParams = params
        parentLayout.addView(this.meterView!!)
        setOriginalParams(params = params)
        setStyle()
        setupCharacters()
        this.meterView!!.alpha = 0f
    }

    /*
        Draw the border and the corner radius of the attack meter view
     */
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

    /*
        Setup the hairball and the cat on the attack meter
     */
    private fun setupCharacters() {
        setupCatButton()
        setupEnemy()
        initialEnemyCatDistance = catButton!!.getOriginalParams().x -
                enemyImage!!.getOriginalParams().x
    }

    /*
        Creates and setup the cat button on the attack meter
     */
    private fun setupCatButton() {
        catButton = CatButton(imageButton = ImageButton(meterContext!!), parentLayout = parentLayout!!,
            params = LayoutParams(getOriginalParams().height, getOriginalParams().height,
                getOriginalParams().x + getOriginalParams().width - getOriginalParams().height,
                getOriginalParams().y), backgroundColor = Color.TRANSPARENT)
        catButton!!.doNotStartRotationAndShow()
        catButton!!.getThis().alpha = 0f
        catButton!!.noBorder = true
        catButton!!.setStyle()
    }

    /*
        Create and setup the hairball
     */
    private fun setupEnemy() {
        enemyImage = CImageView(imageView = ImageButton(meterContext!!), parentLayout = parentLayout!!,
        params = LayoutParams(getOriginalParams().height, getOriginalParams().height,
            getOriginalParams().x, getOriginalParams().y))
        enemyImage!!.loadImages(R.drawable.lighthairball, R.drawable.darkhairball)
        enemyImage!!.getThis().alpha = 0f
    }

    /*
        Makes the attack meter, and its contents appear or disappear
        by changing its transparency
     */
    private var fadeAnimator: ValueAnimator? = null
    private var fadeAnimatorIsRunning:Boolean = false
    private fun fade(In:Boolean, Out:Boolean, Duration:Float, Delay:Float) {
        // If the fade animation is running, cancel it
        if (fadeAnimator != null) {
            if (fadeAnimatorIsRunning) {
                fadeAnimator!!.cancel()
                fadeAnimatorIsRunning = false
                fadeAnimator = null
            }
        }
        // Fade the animation in or out
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
        // Setup the properties for the fade animator
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

    fun invokeRelease() {
        didNotInvokeRelease = false
        startRotation(0.5f)
    }

    /*
        Rotates the hairball clockwise
     */
    private var rotationAnimator:ValueAnimator? = null
    private fun startRotation(delay: Float) {
         if (enemyPhase != null || didNotInvokeRelease) {
             return
         }
        setupRotationAnimation()
        rotationAnimator!!.startDelay = (1000 * delay).toLong()
        rotationAnimator!!.start()
        enemyPhase = EnemyPhase.ROTATION
    }

    /*
        Sets up the rotation animation of the hairball
     */
    var remainingPercentage:Float = 0f
    var rotationDuration:Float = 0f
    private fun setupRotationAnimation() {
        rotationDuration = 1f
        // Enables the hairball to start rotating exactly from where it left off
        if (rotationCheckpoint > 0f) {
            remainingPercentage = (360f - enemyImage!!.getThis().rotation) / 360f
            rotationDuration *= remainingPercentage
        }
        // Setup the rotation animation properties
        rotationAnimator = ValueAnimator.ofFloat(enemyImage!!.getThis().rotation, 360f)
        rotationAnimator!!.addUpdateListener {
            enemyImage!!.getThis().rotation = (it.animatedValue as Float)
        }
        rotationAnimator!!.duration = (1000 * rotationDuration).toLong()
        // After the hairball stops rotating, translate it
        rotationAnimator!!.doOnEnd {
                enemyImage!!.getThis().rotation = 0f
            if (enemyPhase != EnemyPhase.TranslationToStart) {
                dismantleFirstRotation()
                startTranslationToCat(0.125f)
            } else {
                dismantleFirstRotation()
            }
        }
    }

    private fun dismantleFirstRotation() {
        enemyPhase = null
        rotationAnimator = null
    }

    /*
        Translates the hairball to the cat
     */
    private var translationToCatAnimator:ValueAnimator? = null
    private fun startTranslationToCat(delay:Float) {
        if (isEnemyInPhase()) {
            return
        }
        setupTranslationToCatAnimation()
        translationToCatAnimator!!.startDelay = (1000 * delay).toLong()
        translationToCatAnimator!!.start()
        enemyPhase = EnemyPhase.TranslationToCat
    }

    /*
        Sets up the animation for the hairball to translate to the cat
     */
    private fun setupTranslationToCatAnimation() {
        translationToCatAnimator = ValueAnimator.ofInt(enemyImage!!.getOriginalParams().x,
        getCurrentEnemyCatDistance() + getOriginalParams().x)
        // Update the x position
        translationToCatAnimator!!.addUpdateListener {
            val params = LayoutParams(
                enemyImage!!.getOriginalParams().width,
                enemyImage!!.getOriginalParams().height,
                (it.animatedValue as Int),
                enemyImage!!.getOriginalParams().y
            )
            enemyImage!!.getThis().layoutParams = params
            enemyImage!!.setOriginalParams(params)
        }
        // Setup the animation properties
        translationToCatAnimator!!.duration = getEnemyToCatDuration()
        // After translation, start jumping on the virus
        translationToCatAnimator!!.doOnEnd {
            if (enemyPhase != EnemyPhase.TranslationToStart) {
                dismantleTranslationToCat()
                startSizeExpansion(0.125f)
            } else {
                dismantleTranslationToCat()
            }
        }
    }

    private fun dismantleTranslationToCat() {
        enemyPhase = null
        translationToCatAnimator = null
    }

    /*
        Causes the hairball to jump on the cat
     */
    private var sizeExpansionAnimator:ValueAnimator? = null
    private fun startSizeExpansion(delay:Float) {
        if (isEnemyInPhase()) {
            return
        }
        setupSizeExpansionAnimation()
        sizeExpansionAnimator!!.startDelay = (1000 * delay).toLong()
        sizeExpansionAnimator!!.start()
        enemyPhase = EnemyPhase.SizeExpansion
    }

    /*
        Creates the size expansion animation
     */
    private fun setupSizeExpansionAnimation() {
        sizeExpansionAnimator = ValueAnimator.ofInt(enemyImage!!.getOriginalParams().height,
            (getOriginalParams().height * 1.5).toInt())
        // Updates the x, y, and width, height of the hairball
        sizeExpansionAnimator!!.addUpdateListener {
            val value:Int = (it.animatedValue as Int)
            val x:Int = (getOriginalParams().x + getOriginalParams().width -
                    getOriginalParams().height - ((value - getOriginalParams().height) * 0.5)).toInt()
            val y:Int = (getOriginalParams().y - ((value - getOriginalParams().height) * 0.5)).toInt()
            val params = LayoutParams(value, value, x, y)
            enemyImage!!.getThis().layoutParams = params
            enemyImage!!.setOriginalParams(params)
        }
        // Setup the properties of the attack meter
        sizeExpansionAnimator!!.duration = (1000 * 0.5).toLong()
        // Stop hairball jump after the cat is dead
        sizeExpansionAnimator!!.doOnEnd {
            if (enemyPhase != EnemyPhase.TranslationToStart) {
                dismantleSizeExpansion()
                startSizeReduction(0.0f)
            } else {
                dismantleSizeExpansion()
            }
        }
    }

    private fun dismantleSizeExpansion() {
        enemyPhase = null
        sizeExpansionAnimator = null
    }

    /*
        Starts reducing the size of the hairball
        after jumping on the cat
     */
    private var sizeReductionAnimator:ValueAnimator? = null
    private fun startSizeReduction(delay:Float) {
        if (isEnemyInPhase()) {
            return
        }
        setupSizeReductionAnimation()
        sizeReductionAnimator!!.startDelay = (1000 * delay).toLong()
        sizeReductionAnimator!!.start()
        enemyPhase =
            EnemyPhase.SizeReduction
    }

    /*
        Sets up the animation to reduce the hairball
     */
    private fun setupSizeReductionAnimation() {
        sizeReductionAnimator = ValueAnimator.ofInt((getOriginalParams().height * 1.5).toInt(),
            (getOriginalParams().height))
        // Translates the x,y and scales down the width and height of the hairball
        sizeReductionAnimator!!.addUpdateListener {
            val value:Int = (it.animatedValue as Int)
            val x:Int = (getOriginalParams().x + getOriginalParams().width -
                    getOriginalParams().height - ((value - getOriginalParams().height) * 0.5)).toInt()
            val y:Int = (getOriginalParams().y - ((value - getOriginalParams().height) * 0.5)).toInt()
            val params = LayoutParams(value, value, x, y)
            enemyImage!!.getThis().layoutParams = params
            enemyImage!!.setOriginalParams(params)
        }
        // Setup the properties for the animation
        sizeReductionAnimator!!.duration = (1000 * 0.5).toLong()
        // Return the hairball to its original position after hairball attack
        sizeReductionAnimator!!.doOnEnd {
            if (enemyPhase != EnemyPhase.TranslationToStart) {
                dismantleSizeReduction()
                attackRandomCatButton()
                startTransitionToStart(0.125f)
            } else {
                dismantleSizeReduction()
            }
        }
    }

    private fun attackRandomCatButton() {
        val randomCatButton:CatButton? = MainActivity.boardGame!!.getCatButtons().randomLivingCatButton()
        if (randomCatButton != null) {
            MainActivity.boardGame!!.attackCatButton(randomCatButton)
        }
    }

    private fun dismantleSizeReduction() {
        enemyPhase = null
        sizeReductionAnimator = null
    }

    /*
        Starts translating the hairball to its original position
        after attacking the cat
     */
    private var transitionToStartAnimator:AnimatorSet? = null
    private fun startTransitionToStart(delay: Float) {
        if (isEnemyInPhase()) {
            return
        }
        setupTransitionToStartAnimation()
        transitionToStartAnimator!!.startDelay = (1000 * delay).toLong()
        transitionToStartAnimator!!.start()
        enemyPhase = EnemyPhase.TranslationToStart
    }

    /*
        Setup transition animation of hairball to its original position
     */
    var transitionToStartX:ValueAnimator? = null
    private fun setupTransitionToStartAnimation() {
        transitionToStartX = ValueAnimator.ofInt(enemyImage!!.getOriginalParams().x,
        getOriginalParams().x)
        // Translate the x value of the hairball to the left
        transitionToStartX!!.addUpdateListener {
            val params = LayoutParams(
                enemyImage!!.getOriginalParams().width,
                enemyImage!!.getOriginalParams().height,
                (it.animatedValue as Int),
                enemyImage!!.getOriginalParams().y
            )
            enemyImage!!.getThis().layoutParams = params
            enemyImage!!.setOriginalParams(params)
        }
        // Reduce the size of the hairball if its larger than its original size
        sizeReductionAnimator = ValueAnimator.ofInt((enemyImage!!.getOriginalParams().height),
            (getOriginalParams().height))
        sizeReductionAnimator!!.addUpdateListener {
            val value:Int = (it.animatedValue as Int)
            val x:Int = enemyImage!!.getOriginalParams().x
            val y:Int = (getOriginalParams().y - ((value - getOriginalParams().height) * 0.5)).toInt()
            val params = LayoutParams(value, value, x, y)
            enemyImage!!.getThis().layoutParams = params
            enemyImage!!.setOriginalParams(params)
        }
        // Setup the properties for the translation to start animation
        transitionToStartAnimator = AnimatorSet()
        transitionToStartAnimator!!.play(transitionToStartX!!).with(sizeReductionAnimator!!)
        transitionToStartAnimator!!.duration = getEnemyToStartDuration()
        transitionToStartAnimator!!.doOnEnd {
            dismantleTransitionToStart()
            startRotation(0.125f)
        }
    }

    private fun dismantleTransitionToStart() {
        enemyPhase = null
        transitionToStartAnimator = null
        transitionToStartX = null
        sizeReductionAnimator = null
    }

    private fun getEnemyToStartDuration():Long {
        return ((displacementDuration * getCurrentEnemyStartDistance()
                / initialEnemyCatDistance) * 1000).toLong()
    }

    private fun getCurrentEnemyStartDistance():Int {
        return (enemyImage!!.getOriginalParams().x - getOriginalParams().x)
    }
    private fun getCurrentEnemyCatDistance():Int {
        return (catButton!!.getOriginalParams().x - enemyImage!!.getOriginalParams().x)
    }

    private fun getEnemyToCatDuration():Long {
        return ((displacementDuration * getCurrentEnemyCatDistance()
                / initialEnemyCatDistance) * 1000).toLong()
    }

    /*
        Returns true if the hairball is currently doing something
     */
    private fun isEnemyInPhase():Boolean {
        return ((rotationAnimator != null && rotationAnimator!!.isRunning) ||
                (translationToCatAnimator != null && translationToCatAnimator!!.isRunning) ||
                (sizeExpansionAnimator != null && sizeExpansionAnimator!!.isRunning)  ||
                (sizeReductionAnimator != null && sizeReductionAnimator!!.isRunning) ||
                (transitionToStartAnimator != null && transitionToStartAnimator!!.isRunning))
    }

    /*
        Stops the hairball from anything its doing and send it to its original state
     */
    fun sendEnemyToStart() {
        if (enemyPhase != null && enemyPhase != EnemyPhase.TranslationToStart) {
            displacementDuration = 1.0f
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    MainActivity.staticSelf!!.runOnUiThread {
                        displacementDuration = previousDisplacementDuration
                    }
                }
            }, 1000)
            pauseEnemyMovement()
            rotationCheckpoint = 0f
            enemyPhase =
                EnemyPhase.TranslationToStart
            resumeEnemyMovement()
        }
    }

    /*
        Stops the hairball wherever it is
     */
    private fun pauseEnemyMovement() {
        when (enemyPhase!!) {
            EnemyPhase.ROTATION -> {
                enemyPhase =
                    EnemyPhase.TranslationToStart
                rotationAnimator?.cancel()
                rotationCheckpoint = enemyImage!!.rotation
            }
            EnemyPhase.TranslationToCat -> {
                enemyPhase =
                    EnemyPhase.TranslationToStart
                translationToCatAnimator?.cancel()
            }
            EnemyPhase.SizeExpansion -> {
                enemyPhase =
                    EnemyPhase.TranslationToStart
                sizeExpansionAnimator?.cancel()
            }
            EnemyPhase.SizeReduction -> {
                enemyPhase =
                    EnemyPhase.TranslationToStart
                sizeReductionAnimator?.cancel()
            }
            EnemyPhase.TranslationToStart -> {
                enemyPhase =
                    EnemyPhase.TranslationToStart
                transitionToStartAnimator!!.cancel()
            }
        }
    }

    /*
        Starts the hairball from wherever it left off
     */
    private fun resumeEnemyMovement() {
        when(enemyPhase!!) {
            EnemyPhase.ROTATION -> {
                enemyPhase = null
                startRotation(0.125f)
            }
            EnemyPhase.TranslationToCat -> {
                val delay:Float = (0.75f * getEnemyToStartDuration() / initialEnemyCatDistance)
                startTranslationToCat(delay)
            }
            EnemyPhase.TranslationToStart -> {
                setupTransitionToStartAnimation()
                startTransitionToStart(0f)
            }
            EnemyPhase.SizeExpansion ->
                startSizeExpansion(0.125f)
            EnemyPhase.SizeReduction ->
                startSizeReduction(0.125f)
        }
    }

    /*
        Changes the duration of the hairball attack
     */
    fun updateDuration(change:Float) {
        if (change > 0f) {
            displacementDuration += change
            previousDisplacementDuration += change
        } else if (change < 0f) {
            if (previousDisplacementDuration + change >= 1.5f) {
                displacementDuration += change
                previousDisplacementDuration += change
            } else {
                displacementDuration = 1.5f
                previousDisplacementDuration = 1.5f
            }
        }
    }

    fun resetDisplacementDuration() {
        previousDisplacementDuration = 3.5f
        displacementDuration = previousDisplacementDuration
    }

    fun setCompiledStyle() {
        setStyle()
        catButton!!.setStyle()
        enemyImage!!.setStyle()
    }

    /*
        Sets the colors of the attack meter, hairball, and cat
        based on the theme of the operating system
     */
    private fun setStyle() {
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
        setCornerRadiusAndBorderWidth((getOriginalParams().height / 2.0).toInt(),
            (getOriginalParams().height / 12.0).toInt())
    }
}