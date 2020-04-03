package com.example.savethecat_colormatching.Controllers

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageView

class CenterController {

    companion object {

        var parentParam:LayoutParams? = null
        var childParam:LayoutParams? = null

        fun center(childView: ImageView, childParams:LayoutParams, parentParams:LayoutParams) {
            this.childParam = childParams
            this.parentParam = parentParams
            childView.layoutParams = LayoutParams(childParam!!.width, childParam!!.height, getCenterX().toInt(), getCenterY().toInt())
        }

        fun centerHorizontally(childView: View, childParams:LayoutParams, parentParams: LayoutParams) {
            this.childParam = childParams
            this.parentParam = parentParams
            childView.layoutParams = LayoutParams(childParam!!.width, childParam!!.height,
                getCenterX().toInt(), childParam!!.y)
        }

        private fun getCenterX():Float {
            return (parentParam!!.width.toFloat() - childParam!!.width.toFloat()) * 0.5f
        }

        private fun getCenterY():Float {
            return  (parentParam!!.height.toFloat() - childParam!!.height.toFloat()) * 0.5f
        }

    }

}