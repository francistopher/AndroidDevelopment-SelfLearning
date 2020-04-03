package com.example.savethecat_colormatching.ParticularViews

import android.view.ViewPropertyAnimator
import android.view.animation.LinearInterpolator
import android.widget.AbsoluteLayout
import android.widget.ImageView
import com.example.savethecat_colormatching.CustomViews.CImageView

class IntroView(imageView: ImageView, parentLayout:AbsoluteLayout, params: AbsoluteLayout.LayoutParams) {

    var textImageView:CImageView? = null
    var catImageView:CImageView? = null

    init {
        textImageView = CImageView(imageView=imageView, parentLayout = parentLayout, params = params)
        val catImageViewParams = AbsoluteLayout.LayoutParams(params.width, params.height, params.x, (params.y - params.height * 0.0015).toInt())
        catImageView = CImageView(imageView= ImageView(textImageView!!.getContext()), parentLayout = parentLayout, params = catImageViewParams)
        catImageView!!.isCatImage = true
        textImageView!!.getThis().alpha = 0.0f
        catImageView!!.getThis().alpha = 0.0f
    }

    fun start() {
        textImageView!!.rotateText()
        textImageView!!.fade(In = true, Out = true, Duration = 2.4f, Delay = 0.5f)
        catImageView!!.fade(In = true, Out = true, Duration = 2.4f, Delay = 0.5f)
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

    fun getTextParams():AbsoluteLayout.LayoutParams {
        return textImageView!!.getOriginalParams()
    }

    fun getCatParams():AbsoluteLayout.LayoutParams {
        return catImageView!!.getOriginalParams()
    }

    fun setStyle() {
        textImageView!!.setStyle()
        catImageView!!.setStyle()
    }

}
