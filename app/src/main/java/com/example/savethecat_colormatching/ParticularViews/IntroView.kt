package com.example.savethecat_colormatching.ParticularViews

import android.widget.AbsoluteLayout
import android.widget.ImageView
import com.example.savethecat_colormatching.CustomViews.CImageView

class IntroView(imageView: ImageView, parentLayout:AbsoluteLayout, params: AbsoluteLayout.LayoutParams) {

    private var textImageView:CImageView? = null

    init {
        textImageView = CImageView(imageView=imageView, parentLayout = parentLayout, params = params)

    }

}
