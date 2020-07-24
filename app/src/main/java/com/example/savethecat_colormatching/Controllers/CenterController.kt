package com.example.savethecat_colormatching.Controllers

import android.view.View
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageView

class CenterController {

    companion object {

        private var parentParam:LayoutParams? = null
        private var childParam:LayoutParams? = null

        /*
            Centers the view onto its parent view
         */
        fun centerView(childView: View, childParams: LayoutParams, parentParams: LayoutParams) {
            this.childParam = childParams
            this.parentParam = parentParams
            childView.layoutParams = LayoutParams(childParam!!.width, childParam!!.height,
                getCenterX().toInt(), getCenterY().toInt())
        }

        /*
            Centers the image view onto its parent view
         */
        fun center(childView: ImageView, childParams:LayoutParams, parentParams:LayoutParams) {
            this.childParam = childParams
            this.parentParam = parentParams
            childView.layoutParams = LayoutParams(childParam!!.width, childParam!!.height,
                getCenterX().toInt(), (getCenterY()).toInt())
        }

        /*
            Centers the view horizontally onto its parents view
         */
        fun centerViewHorizontally(childView: View, childParams:LayoutParams, parentParams: LayoutParams) {
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