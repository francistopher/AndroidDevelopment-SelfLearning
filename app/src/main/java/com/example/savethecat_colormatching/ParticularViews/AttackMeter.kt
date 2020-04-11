package com.example.savethecat_colormatching.ParticularViews

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageButton
import android.widget.ImageView
import com.example.savethecat_colormatching.Characters.CatButton
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
    }

    private fun setupEnemy() {
        enemyImage = CImageView(imageView = ImageView(meterContext!!), parentLayout = parentLayout!!,
        params = LayoutParams(getOriginalParams().height, getOriginalParams().height,
            getOriginalParams().x, getOriginalParams().y))
        enemyImage!!.loadImages(R.drawable.lighthairball, R.drawable.darkhairball)
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

    fun setStyle() {
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
    }
}