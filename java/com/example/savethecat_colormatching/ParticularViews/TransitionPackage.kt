
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageButton
import androidx.core.animation.doOnEnd
import com.example.savethecat_colormatching.MainActivity


class TransitionPackage(spawnParams:LayoutParams,
                        targetParams:LayoutParams,
                        heartButton: ImageButton,
                        isOpponent:Boolean) {

    private var targetParams:LayoutParams? = null
    private var spawnParams:LayoutParams? = null
    private var heartButton:ImageButton? = null
    private var animatorSet: AnimatorSet? = null
    private var xAnimation:ValueAnimator? = null
    private var yAnimation:ValueAnimator? = null
    private var transitionedToBase:Boolean = false
    private var isOpponent:Boolean = false
    private var targetX:Int = 0
    private var targetY:Int = 0

    init {
        this.targetParams = targetParams
        this.spawnParams = spawnParams
        this.heartButton = heartButton
        this.isOpponent = isOpponent
        heartButton.bringToFront()
        setupAnimationX()
        setupAnimationY()
        setupAnimationSet()
    }

    /*
        Setup x translation
     */
    private fun setupAnimationX() {
        targetX = spawnParams!!.x
        xAnimation = ValueAnimator.ofInt(spawnParams!!.x , targetParams!!.x)
        xAnimation!!.addUpdateListener {
            targetX = (it.animatedValue as Int)
            heartButton!!.layoutParams = LayoutParams(
                targetParams!!.width, targetParams!!.height,
                targetX, targetY
            )
        }
    }

    /*
        Setup y coordinate translation
     */
    private fun setupAnimationY() {
        targetY = spawnParams!!.y
        yAnimation = ValueAnimator.ofInt(spawnParams!!.y, targetParams!!.y)
        yAnimation!!.addUpdateListener {
            targetY = (it.animatedValue as Int)
            heartButton!!.layoutParams = LayoutParams(
                targetParams!!.width, targetParams!!.height,
                targetX, targetY)
        }
    }

    /*
        Setup both the x and y translation into one
     */
    private fun setupAnimationSet() {
        animatorSet = AnimatorSet()
        animatorSet!!.play(xAnimation!!).with(yAnimation!!)
        animatorSet!!.startDelay = 125
        animatorSet!!.duration = 2500
        animatorSet!!.start()
        animatorSet!!.doOnEnd {
            if (!transitionedToBase) {
                transitionedToBase = true
                // Increment counter based on player
                if (isOpponent) {
                    MainActivity.opponentLivesMeter!!.incrementLivesLeftCount()
                } else {
                    MainActivity.myLivesMeter!!.incrementLivesLeftCount()
                }
            } else {
                MainActivity.rootLayout!!.removeView(heartButton!!)
            }
        }
    }

    /*
        Remove a heart, illusion
     */
    fun drop() {
        if (transitionedToBase) {
            spawnParams = heartButton!!.layoutParams as LayoutParams
            targetParams = LayoutParams(targetParams!!.width, targetParams!!.height,
                targetParams!!.x, (MainActivity.dUnitHeight * 16).toInt())
            heartButton!!.bringToFront()
            setupAnimationX()
            setupAnimationY()
            setupAnimationSet()
        } else {
            MainActivity.rootLayout!!.removeView(heartButton!!)
        }
    }
}