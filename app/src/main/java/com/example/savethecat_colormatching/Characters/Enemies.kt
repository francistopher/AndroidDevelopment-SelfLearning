package com.example.savethecat_colormatching.Characters

import android.widget.AbsoluteLayout
import android.widget.ImageView
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R

class Enemies {

    private var enemies = mutableListOf<Enemy>()

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
        // Spacing
        totalWidthSpacing = (MainActivity.dWidth).toFloat() - (sideLength * 3.0f)
        totalHeightSpacing = (MainActivity.dHeight).toFloat() - (sideLength * 4.0f)
        // Enemy Spacing
        enemyWidthSpacing = totalWidthSpacing / 2.625f
        enemyHeightSpacing = totalHeightSpacing / 4.0f
        // Starting coordinates
        x = -enemyWidthSpacing * 0.665f
        y = -enemyHeightSpacing * 0.45f
        // Build and plot hair balls equidistant from each other
        for (i in 0..2) {
            x += enemyWidthSpacing
            for (ii in 0..3) {
                y += enemyHeightSpacing
                enemy = null
                enemy = Enemy(imageView = ImageView(MainActivity.staticSelf!!),
                    parentLayout = MainActivity.rootLayout!!,
                    params = AbsoluteLayout.LayoutParams(sideLength.toInt(), sideLength.toInt(),
                        x.toInt(), y.toInt()))
                enemy!!.getThis().alpha = 0f
                enemy!!.loadImages(lightImageR = R.drawable.lighthairball, darkImageR = R.drawable.darkhairball)
                enemy!!.setStyle()
                enemies.add(enemy!!)
                y += sideLength
            }
            x += sideLength
            y = -(enemyHeightSpacing * 0.45f)
        }
    }

    /*
        During an attack the hair balls swing to the center
        of the screen and back
     */
    fun translateToCatAndBack(catButton:CatButton) {
        for (enemy in enemies) {
            enemy.translateToCatAndBack((catButton.getOriginalParams().x +
                    (catButton.getOriginalParams().width * 0.5)).toInt(),
                (catButton.getOriginalParams().y +
                        (catButton.getOriginalParams().height * 0.5)).toInt())
        }
    }

    /*
        Updates the image of all the hair balls
     */
    fun setStyle() {
        for (enemy in enemies){
            enemy.setStyle()
        }
    }

    /*
        Enables all the hair balls to swing back and forth
     */
    fun sway() {
        for (enemy in enemies) {
            enemy.sway()
        }
    }

    /*
        Fades in all the hair balls to appear from nothin
     */
    fun fadeIn() {
        for (enemy in enemies) {
            enemy.fadeIn()
        }
    }
}