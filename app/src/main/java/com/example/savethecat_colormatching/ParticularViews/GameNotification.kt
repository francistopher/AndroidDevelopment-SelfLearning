package com.example.savethecat_colormatching.ParticularViews

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.text.Layout
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.view.Gravity
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import android.widget.TextView
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R

class GameNotification(view:View, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var parentLayout:AbsoluteLayout? = null

    private var view: View? = null
    private var messageLabel: TextView? = null
    private var imageButton: Button? = null

    private var spawnParams:LayoutParams? = null
    private var targetParams:LayoutParams? = null

    private var isShowing:Boolean = false

    private var notification:Notification? = null
    private var difference:Int = 0

    private var notificationQueue:MutableList<Notification> = mutableListOf()

    init {
        setupView(view)
        setupTargetParams(params)
        setupSpawnParams()
        setupParentLayout(parentLayout)
        setCornerRadiusAndBorderWidth((params.height * 0.5).toInt())
        setupImageButton()
        setupMessageLabel()
        translate(false, 0f, 0f)
        startSearchingTimer()
    }

    private var translationAnimatorSet:AnimatorSet? = null
    private var imageButtonYAnimator:ValueAnimator? = null
    private var viewMessageLabelYAnimator:ValueAnimator? = null
    private var toShow:Boolean = false
    private fun translate(show:Boolean, duration:Float, delay:Float) {
        if (translationAnimatorSet != null) {
            translationAnimatorSet!!.cancel()
        }
        if (show) {
            imageButtonYAnimator = ValueAnimator.ofInt((imageButton!!.layoutParams as LayoutParams).y,
                imageButtonTargetY)
            viewMessageLabelYAnimator = ValueAnimator.ofInt((view!!.layoutParams as LayoutParams).y,
                targetParams!!.y)
            toShow = true
        } else {
            imageButtonYAnimator = ValueAnimator.ofInt((imageButton!!.layoutParams as LayoutParams).y,
                imageButtonSpawnY)
            viewMessageLabelYAnimator = ValueAnimator.ofInt((view!!.layoutParams as LayoutParams).y,
                spawnParams!!.y)
            toShow = false
        }

        imageButtonYAnimator!!.addUpdateListener {
            imageButton!!.layoutParams = LayoutParams(
                (imageButton!!.layoutParams as LayoutParams).width,
                (imageButton!!.layoutParams as LayoutParams).height,
                (imageButton!!.layoutParams as LayoutParams).x,
                (it.animatedValue as Int))
        }

        viewMessageLabelYAnimator!!.addUpdateListener {
            view!!.layoutParams = LayoutParams(
                (view!!.layoutParams as LayoutParams).width,
                (view!!.layoutParams as LayoutParams).height,
                (view!!.layoutParams as LayoutParams).x,
                (it.animatedValue as Int))
            messageLabel!!.layoutParams = LayoutParams(
                (messageLabel!!.layoutParams as LayoutParams).width,
                (messageLabel!!.layoutParams as LayoutParams).height,
                (messageLabel!!.layoutParams as LayoutParams).x,
                (it.animatedValue as Int))
        }

        translationAnimatorSet = AnimatorSet()
        translationAnimatorSet!!.play(imageButtonYAnimator!!).with(viewMessageLabelYAnimator!!)
        translationAnimatorSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        translationAnimatorSet!!.duration = (1000 * duration).toLong()
        translationAnimatorSet!!.startDelay = (1000 * delay).toLong()
        translationAnimatorSet!!.start()

        translationAnimatorSet!!.doOnEnd {
            if (show) {
                isShowing = show
            }
        }
    }

    private var timerCount:Double = 0.0
    private fun startSearchingTimer() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                view!!.bringToFront()
                messageLabel!!.bringToFront()
                imageButton!!.bringToFront()
                if (notificationQueue.size > 0 && !toShow) {
                    setNotificationDisplayed()
                    translate(true, 0.5f, 0f)
                }
                if (isShowing) {
                    timerCount += 0.125
                    if (timerCount > 3.5) {
                        timerCount = 0.0
                        notificationQueue.removeAt(0)
                        if (notificationQueue.size == 0) {
                            translate(false, 0.5f, 0f)
                            isShowing = false
                        } else {
                            setNotificationDisplayed()
                        }
                    }
                }
                handler.postDelayed(this, 125)
            }
        }, 0)
    }

    fun displayNoInternet() {
        addToNotificationQueue(Notification.NO_INTERNET)
    }

    fun displayYesInternet() {
        addToNotificationQueue(Notification.YES_INTERNET)
    }

    fun displayYesGooglePlayGameServices() {
        addToNotificationQueue(Notification.YES_GOOGLE_PLAY_GAME)
    }

    fun displayNoGooglePlayGameServices() {
        addToNotificationQueue(Notification.NO_GOOGLE_PLAY_GAME)
    }

    var spannableString = SpannableString("")
    private fun setNotificationDisplayed() {
        if (notificationQueue[0] == Notification.YES_GOOGLE_PLAY_GAME) {
            spannableString = SpannableString("Google Play Game Services\n" +
                    "are available!")
            imageButton!!.setBackgroundResource(R.drawable.yesgoogleplaygame)
        } else if (notificationQueue[0] == Notification.YES_INTERNET) {
            spannableString = SpannableString("Connected to the internet!\nGame " +
                    "Experience is Renewable!")
            imageButton!!.setBackgroundResource(R.drawable.yesinternet)
        } else if (notificationQueue[0] == Notification.NO_GOOGLE_PLAY_GAME) {
            spannableString = SpannableString("Sign into Google Play\nGame " +
                    "Services for more fun!")
            imageButton!!.setBackgroundResource(R.drawable.nogoogleplaygame)
        } else if (notificationQueue[0] == Notification.NO_INTERNET) {
            spannableString = SpannableString("No internet connection!\nGame " +
                    "Experience is Limited!")
            imageButton!!.setBackgroundResource(R.drawable.nointernet)
        }
        spannableString.setSpan(
            AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
            0, spannableString.length, 0
        )
        messageLabel!!.text = spannableString
    }

    private fun addToNotificationQueue(notification:Notification) {
        if (notificationQueue.size > 0) {
            var index = 0
            var remove = false
            while (index < notificationQueue.size) {
                remove = (notification == notificationQueue[index])
                if (remove) {
                    notificationQueue.removeAt(index)
                    remove = false
                } else {
                    index += 1
                }
            }
        }
        notificationQueue.add(notification)
    }

    private fun setupView(view:View) {
        this.view = view
    }

    private fun setupTargetParams(params:LayoutParams) {
        this.targetParams = params
        this.view!!.layoutParams = params
    }

    private fun setupSpawnParams() {
        spawnParams = LayoutParams(targetParams!!.width, targetParams!!.height,
            targetParams!!.x, - (targetParams!!.y + targetParams!!.height))
        difference = targetParams!!.y - spawnParams!!.y
    }

    private fun setupParentLayout(parentLayout:AbsoluteLayout) {
        this.parentLayout = parentLayout
        this.parentLayout!!.addView(this.view!!)
    }

    private var shape: GradientDrawable? = null
    private var cornerRadius:Int = 0
    fun setCornerRadiusAndBorderWidth(radius: Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        if (MainActivity.isThemeDark) {
            shape!!.setColor(Color.DKGRAY)
        } else {
            shape!!.setColor(Color.LTGRAY)
        }
        cornerRadius = radius
        shape!!.cornerRadius = radius.toFloat()
        view!!.setBackgroundDrawable(shape)
    }

    private var width:Int = 0
    private var height:Int = 0
    private var x:Int = 0
    private var y:Int = 0
    private var imageButtonSpawnY:Int = 0
    private var imageButtonTargetY:Int = 0
    private fun setupImageButton() {
        imageButton = Button(view!!.context)
        width = (targetParams!!.height * 0.65).toInt()
        height =  (targetParams!!.height * 0.65).toInt()
        x = targetParams!!.x + (targetParams!!.width * 0.075).toInt()
        y = targetParams!!.y + (targetParams!!.height * 0.175).toInt()
        imageButtonTargetY = y
        imageButtonSpawnY = y - difference
        imageButton!!.layoutParams = LayoutParams(width, height, x, y)
        imageButton!!.isEnabled = false
        parentLayout!!.addView(imageButton!!)
    }

    private fun setupMessageLabel() {
        messageLabel = TextView(view!!.context)
        messageLabel!!.layoutParams = LayoutParams((targetParams!!.width * 0.65).toInt(),
            targetParams!!.height, x + width + (targetParams!!.width * 0.03).toInt(),
            targetParams!!.y)
        parentLayout!!.addView(messageLabel!!)
        messageLabel!!.setBackgroundColor(Color.TRANSPARENT)
        messageLabel!!.textSize = height * 0.15f
        messageLabel!!.gravity = Gravity.CENTER
        messageLabel!!.setTextColor(Color.WHITE)
    }

}