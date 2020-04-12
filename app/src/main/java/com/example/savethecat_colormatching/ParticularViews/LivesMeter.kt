package com.example.savethecat_colormatching.ParticularViews

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageButton
import android.widget.TextView
import com.example.savethecat_colormatching.CustomViews.CLabel
import com.example.savethecat_colormatching.CustomViews.CView
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R

class LivesMeter(meterView: View,
                 parentLayout: AbsoluteLayout,
                 params: LayoutParams,
                 isOpponent:Boolean) {

    private var originalParams:LayoutParams? = null
    private var meterContext: Context? = null
    private var meterLayout:AbsoluteLayout? = null
    private var meterView:View? = null

    private var imageHeart:Int = 0

    private var livesLeft:Int = 1
    private var heartButtons:MutableList<ImageButton> = mutableListOf()

    private var currentHeartButton:ImageButton? = null
    private var imageButtonText:CLabel? = null
    private var parentLayout:AbsoluteLayout? = null

    private var containerView:CView? = null

    init {
        if (isOpponent) {
            imageHeart = R.drawable.opponentheart
        } else {
            imageHeart = R.drawable.heart
        }
        this.meterView = meterView
        meterContext = meterView.context
        meterLayout = AbsoluteLayout(meterContext)
        setOriginalParams(params = params)
        this.meterView!!.layoutParams = params
        // Set border width and corner radius
        setStyle()
        setCornerRadiusAndBorderWidth(radius = params.height / 2,
            borderWidth = params.height / 12)
        this.parentLayout = parentLayout
        setupContainerMeterView()
        parentLayout.addView(this.meterView!!)
        // Setup heart interactive buttons
        setupHeartInteractiveButtons()
        setupImageButtonText()
    }

    private fun setupImageButtonText() {
        imageButtonText = CLabel(textView = TextView(meterView!!.context),
            parentLayout = parentLayout!!, params =  LayoutParams(getOriginalParams().width,
                getOriginalParams().height, getOriginalParams().x,
                (getOriginalParams().y + (getOriginalParams().height * 0.05).toInt())))
        imageButtonText!!.setText(livesLeft.toString())
        imageButtonText!!.setTextSize((getOriginalParams().height * 0.15).toFloat())
        imageButtonText!!.getThis().setBackgroundColor(Color.TRANSPARENT)
    }

    private fun setupHeartInteractiveButtons() {
        for (why in (heartButtons.size..1)) {
            buildHeartButton()
        }
    }

    private fun setupContainerMeterView() {
        containerView = CView(view = View(meterView!!.context), parentLayout = parentLayout!!,
        params = LayoutParams(((getOriginalParams().width * 2) - borderWidth),
            getOriginalParams().height, getOriginalParams().x - getOriginalParams().width +
                    borderWidth, getOriginalParams().y))
        containerView!!.setCornerRadiusAndBorderWidth(getOriginalParams().height / 2,
            getOriginalParams().height / 12)
    }

    private fun buildHeartButton() {
        currentHeartButton = ImageButton(meterView!!.context)
        currentHeartButton!!.layoutParams = LayoutParams(getOriginalParams().width,
            getOriginalParams().height, getOriginalParams().x, getOriginalParams().y)
        currentHeartButton!!.setBackgroundResource(imageHeart)
        heartButtons.add(currentHeartButton!!)
        parentLayout!!.addView(currentHeartButton)
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
        meterView!!.setBackgroundDrawable(shape)
    }

    fun setOriginalParams(params: LayoutParams) {
        originalParams = params
    }

    fun getOriginalParams():LayoutParams {
        return originalParams!!
    }

    fun getThis():View {
        return this.meterView!!
    }

    private fun lightDominant() {
        meterView!!.setBackgroundColor(Color.BLACK)
    }

    private fun darkDominant() {
        meterView!!.setBackgroundColor(Color.WHITE)
    }

    fun setStyle() {
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
    }
}