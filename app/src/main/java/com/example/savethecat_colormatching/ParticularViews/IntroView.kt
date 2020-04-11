package com.example.savethecat_colormatching.ParticularViews

import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageView
import com.example.savethecat_colormatching.CustomViews.CImageView

class IntroView(imageView: ImageView, parentLayout:AbsoluteLayout, params: LayoutParams) {

    var textImageView:CImageView? = null
    var catImageView:CImageView? = null

    init {
        textImageView = CImageView(imageView=imageView, parentLayout = parentLayout, params = params)
        val catImageViewParams = LayoutParams(params.width, params.height, params.x, (params.y - params.height * 0.0015).toInt())
        catImageView = CImageView(imageView= ImageView(textImageView!!.getContext()), parentLayout = parentLayout, params = catImageViewParams)
        catImageView!!.isCatImage = true
        textImageView!!.getThis().alpha = 0.0f
        catImageView!!.getThis().alpha = 0.0f
    }

    fun start() {
        textImageView!!.rotateText()
        textImageView!!.fade(In = true, Out = true, Duration = 2.0f, Delay = 0.5f)
        catImageView!!.fade(In = true, Out = true, Duration = 2.0f, Delay = 0.5f)
    }

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

    fun setStyle() {
        textImageView!!.setStyle()
        catImageView!!.setStyle()
    }

    fun hide() {
        textImageView!!.getThis().alpha = 0.0f
        catImageView!!.getThis().alpha = 0.0f
    }

}
