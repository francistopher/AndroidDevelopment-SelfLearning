package com.example.savethecat_colormatching.Controllers

import android.util.DisplayMetrics
import android.widget.AbsoluteLayout
import com.example.savethecat_colormatching.MainActivity

class AspectRatio {
    companion object {
        private val displayMetrics: DisplayMetrics = DisplayMetrics()
        private var screenAspectRatio:Double = 0.0;
        fun setupAspectRatio() {
            fun getStatusBarHeight():Double {
                var result = 0.0
                val resourceId:Int = MainActivity.staticSelf!!.resources.
                getIdentifier("status_bar_height", "dimen", "android")
                if (resourceId > 0) {
                    result = MainActivity.staticSelf!!.resources.
                    getDimensionPixelSize(resourceId).toDouble()
                }
                return result
            }

            MainActivity.dNavigationBarHeight = getStatusBarHeight() * 0.5f
            fun setupScreenDimension() {
                MainActivity.dWidth = displayMetrics.widthPixels.toDouble()
                MainActivity.adHeight = displayMetrics.heightPixels.toDouble()
                MainActivity.dHeight = displayMetrics.heightPixels.toDouble()
            }
            fun setupUnitScreenDimension() {
                MainActivity.dUnitWidth = MainActivity.dWidth / 18.0
                MainActivity.dUnitHeight = MainActivity.dHeight / 18.0
            }
            MainActivity.staticSelf!!.windowManager.defaultDisplay.getMetrics(displayMetrics)

            screenAspectRatio = (MainActivity.dHeight / MainActivity.dWidth)
            when {
                screenAspectRatio > 2.16 -> {
                    MainActivity.aspectRatio = AREnum.ar19point5by9
                }
                screenAspectRatio > 2.09 -> {
                    MainActivity.aspectRatio = AREnum.ar19by9
                }
                screenAspectRatio > 2.07 -> {
                    MainActivity.aspectRatio = AREnum.ar18point7by9
                }
                screenAspectRatio > 2.05 -> {
                    MainActivity.aspectRatio = AREnum.ar18point5by9
                }
                screenAspectRatio > 1.9 -> {
                    MainActivity.aspectRatio = AREnum.ar18by9
                }
                screenAspectRatio > 1.8 -> {
                    MainActivity.aspectRatio = AREnum.ar19by10
                }
                screenAspectRatio > 1.7 -> {
                    MainActivity.aspectRatio = AREnum.ar16by9
                }
                screenAspectRatio > 1.66 -> {
                    MainActivity.aspectRatio = AREnum.ar5by3
                }
                screenAspectRatio > 1.5 -> {
                    MainActivity.aspectRatio = AREnum.ar16by10
                }
                screenAspectRatio > 1.4 -> {
                    MainActivity.aspectRatio = AREnum.ar3by2
                }
                else -> {
                    // screen aspect ratio > 1.3
                    MainActivity.aspectRatio = AREnum.ar4by3
                }
            }
            if (MainActivity.aspectRatio != AREnum.ar19point5by9) {
                setupScreenDimension()
                MainActivity.dHeight *= 1.2
                MainActivity.params = AbsoluteLayout.LayoutParams(MainActivity.dWidth.toInt(),
                    (MainActivity.dHeight).toInt(), 0, 0)
                setupUnitScreenDimension()
            }
        }
    }
}