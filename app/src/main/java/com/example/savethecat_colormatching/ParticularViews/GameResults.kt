package com.example.savethecat_colormatching.ParticularViews

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageView
import android.widget.TextView
import com.example.savethecat_colormatching.CustomViews.CImageView
import com.example.savethecat_colormatching.CustomViews.CLabel
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R

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


    companion object {
        var savedCatButtonsCount:Int = 0
        var deadCatButtonsCount:Int = 0
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
        setCornerRadiusAndBorderWidth(radius = params.height / 2,
            borderWidth = params.height / 36)
    }

    fun setupContents() {
        setupTitleView()
        setupSmilingCat()
        setupDeadCat()
        setupAliveCatCountLabel()
        setupDeadCatCountLabel()
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
        aliveCatCountLabel!!.setTextSize(aliveCatCountLabel!!.getOriginalParams().height * 0.1875f)
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
        deadCatCountLabel!!.setTextSize(aliveCatCountLabel!!.getOriginalParams().height * 0.1875f)
        deadCatCountLabel!!.setText("$deadCatButtonsCount")
        deadCatCountLabel!!.isInverted = true
        deadCatCountLabel!!.setStyle()
    }

    private fun setGameResults() {

    }

    private fun resetGameResults() {

    }
}