package com.example.savethecat_colormatching.ParticularViews

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import android.widget.TextView
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R

class GameNotification(view:View, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var parentLayout:AbsoluteLayout? = null

    private var view: View? = null
    private var messageLabel: TextView? = null
    private var imageButton: Button? = null

    private var spawnParams:LayoutParams? = null
    private var targetParams:LayoutParams? = null

    private var yesInternet:Int = R.drawable.yesinternet
    private var noInternet:Int = R.drawable.nointernet
    private var yesGooglePlayGames:Int = R.drawable.yesgoogleplaygame
    private var noGooglePlayGames:Int = R.drawable.nogoogleplaygame

    init {
        setupView(view)
        setupTargetParams(params)
        setupSpawnParams()
        setupParentLayout(parentLayout)
        setCornerRadiusAndBorderWidth((params.height * 0.5).toInt())
        setupImageButton()
        setupMessageLabel()
    }

    private fun setupView(view:View) {
        this.view = view
    }

    private fun setupTargetParams(params:LayoutParams) {
        this.targetParams = params
        this.view!!.layoutParams = params
    }

    private fun setupSpawnParams() {
        spawnParams = LayoutParams(targetParams!!.width, targetParams!!.height,
            targetParams!!.x, -(targetParams!!.y + targetParams!!.height))
    }

    private fun setupParentLayout(parentLayout:AbsoluteLayout) {
        this.parentLayout = parentLayout
        this.parentLayout!!.addView(this.view!!)
    }

    private var shape: GradientDrawable? = null
    private var cornerRadius:Int = 0
    fun setCornerRadiusAndBorderWidth(radius: Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        if (MainActivity.isThemeDark) {
            shape!!.setColor(Color.DKGRAY)
        } else {
            shape!!.setColor(Color.LTGRAY)
        }
        cornerRadius = radius
        shape!!.cornerRadius = radius.toFloat()
        view!!.setBackgroundDrawable(shape)
    }

    private var width:Int = 0
    private var height:Int = 0
    private var x:Int = 0
    private var y:Int = 0
    private fun setupImageButton() {
        imageButton = Button(view!!.context)
        width = (targetParams!!.height * 0.65).toInt()
        height =  (targetParams!!.height * 0.65).toInt()
        x = targetParams!!.x + (targetParams!!.width * 0.1).toInt()
        y = targetParams!!.y + (targetParams!!.height * 0.175).toInt()
        imageButton!!.layoutParams = LayoutParams(width, height, x, y)
        parentLayout!!.addView(imageButton!!)
        imageButton!!.setBackgroundResource(yesGooglePlayGames)
    }

    private fun setupMessageLabel() {
        messageLabel = TextView(view!!.context)
        messageLabel!!.layoutParams = LayoutParams((targetParams!!.width * 0.525).toInt(), height,
        x + width + (targetParams!!.width * 0.0625).toInt(), y)
        parentLayout!!.addView(messageLabel!!)
        messageLabel!!.setBackgroundColor(Color.BLUE)
    }

}