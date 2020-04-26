import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.ImageButton
import androidx.core.animation.doOnEnd
import com.example.savethecat_colormatching.MainActivity


class TransitionPackage(spawnParams:LayoutParams,
                        targetParams:LayoutParams,
                        heartButton: ImageButton) {

    private var targetParams:LayoutParams? = null
    private var spawnParams:LayoutParams? = null
    private var heartButton:ImageButton? = null
    private var animatorSet: AnimatorSet? = null
    private var xAnimation:ValueAnimator? = null
    private var yAnimation:ValueAnimator? = null
    private var transitionedToBase:Boolean = false
    private var targetX:Int = 0
    private var targetY:Int = 0

    init {
        this.targetParams = targetParams
        this.spawnParams = spawnParams
        this.heartButton = heartButton
        heartButton.bringToFront()
        setupAnimationX()
        setupAnimationY()
        setupAnimationSet()
    }

    private fun setupAnimationX() {
        targetX = spawnParams!!.x
        xAnimation = ValueAnimator.ofInt(spawnParams!!.x , targetParams!!.x)
        xAnimation!!.addUpdateListener {
            targetX = (it.animatedValue as Int)
            heartButton!!.layoutParams = AbsoluteLayout.LayoutParams(
                targetParams!!.width, targetParams!!.height,
                targetX, targetY
            )
        }
    }

    private fun setupAnimationY() {
        targetY = spawnParams!!.y
        yAnimation = ValueAnimator.ofInt(spawnParams!!.y, targetParams!!.y)
        yAnimation!!.addUpdateListener {
            targetY = (it.animatedValue as Int)
            heartButton!!.layoutParams = LayoutParams(targetParams!!.width, targetParams!!.height,
                targetX, targetY)
        }
    }

    private fun setupAnimationSet() {
        animatorSet = AnimatorSet()
        animatorSet!!.play(xAnimation!!).with(yAnimation!!)
        animatorSet!!.startDelay = 125
        animatorSet!!.duration = 2500
        animatorSet!!.start()
        animatorSet!!.doOnEnd {
            if (!transitionedToBase) {
                transitionedToBase = true
                MainActivity.myLivesMeter!!.incrementLivesLeftCount()
                MainActivity.myLivesMeter!!.setLivesLeftTextCount()
            } else {
                MainActivity.rootLayout!!.removeView(heartButton!!)
            }
        }
    }

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