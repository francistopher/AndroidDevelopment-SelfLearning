package com.example.savethecat_colormatching.ParticularViews

import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageButton
import android.widget.ImageView
import com.example.savethecat_colormatching.CustomViews.CImageView

class IntroView(imageView: ImageButton, parentLayout:AbsoluteLayout, params: LayoutParams) {

    var textImageView:CImageView? = null
    var catImageView:CImageView? = null

    init {
        // Load all the sub components
        textImageView = CImageView(imageView= imageView, parentLayout = parentLayout, params = params)
        catImageView = CImageView(imageView= ImageView(textImageView!!.getContext()), parentLayout = parentLayout, params = params)
        catImageView!!.getThis().scaleType = ImageView.ScaleType.CENTER_INSIDE
        catImageView!!.isCatImage = true
        textImageView!!.getThis().alpha = 0.0f
        catImageView!!.getThis().alpha = 0.0f
    }

    /*
        Stats the intro animation
     */
    fun start() {
        textImageView!!.rotateText()
        textImageView!!.fade(In = true, Out = true, Duration = 2.0f, Delay = 0.5f)
        catImageView!!.fade(In = true, Out = true, Duration = 2.0f, Delay = 0.5f)
    }

    /*
        Loads the images onto the views
     */
    fun loadTextImages(lightTextImageR:Int, darkTextImageR:Int, lightCatImageR:Int, darkCatImageR:Int) {
        textImageView!!.loadImages(lightTextImageR, darkTextImageR)
        catImageView!!.loadImages(lightCatImageR, darkCatImageR)
    }

    fun getTextImage():ImageView {
        return textImageView!!.getThis()
    }

    fun getCatImage():ImageView {
        return catImageView!!.getThis()
    }

    fun getTextParams():LayoutParams {
        return textImageView!!.getOriginalParams()
    }

    fun getCatParams():LayoutParams {
        return catImageView!!.getOriginalParams()
    }

    fun fadeOut(duration:Float) {
        textImageView!!.fadeOut(duration)
        catImageView!!.fadeOut(duration)
    }

    /*
        Changes the colors based on the system theme
     */
    fun setStyle() {
        textImageView!!.setStyle()
        catImageView!!.setStyle()
    }
}
