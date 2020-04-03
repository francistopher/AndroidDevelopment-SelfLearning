package com.example.savethecat_colormatching.Characters

import android.util.Log
import android.widget.AbsoluteLayout
import android.widget.ImageView
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R

class Enemies {

    var enemies = mutableListOf<Enemy>()

    init{
        buildEnemies()
    }

    private var sideLength:Float = 0.0f
    // Total Spacing
    private var totalWidthSpacing:Float = 0.0f
    private var totalHeightSpacing:Float = 0.0f
    // Enemy Spacing
    private var enemyWidthSpacing:Float = 0.0f
    private var enemyHeightSpacing:Float = 0.0f
    private var enemy:Enemy? = null
    // Starting coordinates
    private var x:Float = 0.0f
    private var y:Float = 0.0f

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
                enemy = null
                enemy = Enemy(imageView = ImageView(MainActivity.staticSelf!!),
                    parentLayout = MainActivity.rootLayout!!,
                    params = AbsoluteLayout.LayoutParams(sideLength.toInt(), sideLength.toInt(),
                        x.toInt(), y.toInt()))
                enemy!!.loadImages(lightImageR = R.drawable.lighthairball, darkImageR = R.drawable.darkhairball)
                enemy!!.setStyle()
                enemy!!.getThis().alpha = 0.0f
                enemy!!.sway()
                enemies.add(enemy!!)
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