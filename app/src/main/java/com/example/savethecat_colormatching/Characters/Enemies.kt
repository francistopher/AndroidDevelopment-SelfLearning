package com.example.savethecat_colormatching.Characters

import android.graphics.Color
import android.util.Log
import android.widget.AbsoluteLayout
import android.widget.TextView
import com.example.savethecat_colormatching.CustomViews.CLabel
import com.example.savethecat_colormatching.MainActivity

class Enemies {

    init{
        buildEnemies()
    }

    var enemies:ArrayList<CLabel> = ArrayList()

    var sideLength:Float = 0.0f
    // Total Spacing
    var totalWidthSpacing:Float = 0.0f
    var totalHeightSpacing:Float = 0.0f
    // Enemy Spacing
    var enemyWidthSpacing:Float = 0.0f
    var enemyHeightSpacing:Float = 0.0f
    var label:CLabel? = null
    // Starting coordinates
    var x:Float = 0.0f
    var y:Float = 0.0f

    private fun buildEnemies(){
        sideLength = (MainActivity.dUnitHeight * 2.0).toFloat()
        Log.i("Side length", "${sideLength}")
        // Spacing
        totalWidthSpacing = MainActivity.dWidth.toFloat() - (sideLength * 3.0f)
        totalHeightSpacing = MainActivity.dHeight.toFloat() - (sideLength * 4.0f)
        // Enemy Spacing
        enemyWidthSpacing = totalWidthSpacing / 2.625f
        enemyHeightSpacing = totalHeightSpacing / 4.0f
        // Starting coordinates
        x = -enemyWidthSpacing * 0.665f
        y = (-(enemyHeightSpacing) * 0.45f) + MainActivity.dNavigationBarHeight.toFloat()
        // Build and plot enemies

        for (i in 0..2) {
            x += enemyWidthSpacing
            for (ii in 0..3) {
                if (i == 3 && ii == 4){
                    break
                }
                y += enemyHeightSpacing
                label = null
                label = CLabel(textView = TextView(MainActivity.staticSelf!!),
                    parentLayout = MainActivity.rootLayout!!,
                    params = AbsoluteLayout.LayoutParams(sideLength.toInt(), sideLength.toInt(),
                        x.toInt(), y.toInt()))
                MainActivity.staticSelf!!.setContentView(MainActivity.rootLayout!!)
                y += sideLength
            }
            x += sideLength
            y = (-(enemyHeightSpacing) * 0.45f) + MainActivity.dNavigationBarHeight.toFloat()
        }
    }

    fun setStyle() {
        for (enemy in enemies){
            enemy.setStyle()
        }
    }
}