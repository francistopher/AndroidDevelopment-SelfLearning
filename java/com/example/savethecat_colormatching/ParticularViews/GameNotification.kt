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
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.Controllers.MPController
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.R
import com.example.savethecat_colormatching.SettingsMenu.LeaderBoard

class GameNotification(view:Button, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var parentLayout:AbsoluteLayout? = null

    private var view: Button? = null
    private var messageLabel: Button? = null
    private var imageButton: Button? = null

    private var spawnParams:LayoutParams? = null
    private var targetParams:LayoutParams? = null

    private var isShowing:Boolean = false
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

    fun bringToFront() {
        view!!.bringToFront()
        messageLabel!!.bringToFront()
        imageButton!!.bringToFront()
    }

    /*
        Translate the game notification view down or up
     */
    private var translationAnimatorSet:AnimatorSet? = null
    private var imageButtonYAnimator:ValueAnimator? = null
    private var viewMessageLabelYAnimator:ValueAnimator? = null
    private var toShow:Boolean = false
    private fun translate(show:Boolean, duration:Float, delay:Float) {
        // If the translation animation is running, cancel it
        if (translationAnimatorSet != null) {
            translationAnimatorSet!!.cancel()
        }
        // Show the notification or hide it
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
        // Translate all the sub-components, including the view container
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

    /*
        Start the timer that searches for any notifications
        to be displayed to the player
     */
    private var timerCount:Double = 0.0
    private fun startSearchingTimer() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                // Pull the notificiation in front of everything
                bringToFront()
                // Show a notification when there is one
                if (notificationQueue.size > 0 && !toShow) {
                    setNotificationDisplayed()
                    translate(true, 0.5f, 0f)
                }
                // Start counting a timer for the notification to disappear
                if (isShowing) {
                    timerCount += 0.125
                    if (timerCount > 3.25) {
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

    fun displayNewHighScore() {
        addToNotificationQueue(Notification.NEW_HIGH_SCORE)
    }

    fun displayHighScore() {
        addToNotificationQueue(Notification.HIGH_SCORE)
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

    fun displayNeedMoreMouseCoins() {
        addToNotificationQueue(Notification.NEED_MOUSE_COINS)
    }

    fun displayFirebaseTrouble() {
        addToNotificationQueue(Notification.FIREBASE_TROUBLE)
    }

    fun displayFirebaseConnected() {
        addToNotificationQueue(Notification.FIREBASE_CONNECTED)
    }

    fun displayGameOpponent() {
        addToNotificationQueue(Notification.PLAYING_AGAINST)
    }

    /*
        Updates the message and the picture that is
        displayed on the notification view
     */
    var spannableString = SpannableString("")
    private fun setNotificationDisplayed() {
        if (notificationQueue[0] == Notification.YES_GOOGLE_PLAY_GAME) {
            spannableString = SpannableString("Google Play Game\n Services " +
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
        } else if (notificationQueue[0] == Notification.NEW_HIGH_SCORE) {
            spannableString = if (LeaderBoard.singleGameScore > 1) {
                SpannableString("New High Score!!!\n" +
                        "${LeaderBoard.singleGameScore} Cats Saved")
            } else {
                SpannableString("New High Score!!!\n" +
                        "${LeaderBoard.singleGameScore} Cat Saved")
            }
            imageButton!!.setBackgroundResource(R.drawable.heart)
        } else if (notificationQueue[0] == Notification.HIGH_SCORE) {
            spannableString = if (LeaderBoard.singleGameScore > 1) {
                SpannableString("High Score\n" +
                        "${LeaderBoard.singleGameScore} Cats Saved")
            } else {
                SpannableString("High Score\n" +
                        "${LeaderBoard.singleGameScore} Cat Saved")
            }
            imageButton!!.setBackgroundResource(R.drawable.heart)
        } else if (notificationQueue[0] == Notification.NEED_MOUSE_COINS) {
            spannableString = SpannableString("You need ${MCView.neededMouseCoinCount}\n" +
                        "more Mouse Coins!!!")
            imageButton!!.setBackgroundResource(R.drawable.mousecoin)
        } else if (notificationQueue[0] == Notification.FIREBASE_TROUBLE) {
            spannableString = SpannableString("Lost connection with Firebase")
            imageButton!!.setBackgroundResource(R.drawable.nofirebase)
        } else if (notificationQueue[0] == Notification.FIREBASE_CONNECTED) {
            spannableString = SpannableString("Connected with Firebase\nSynchronizing game data")
            imageButton!!.setBackgroundResource(R.drawable.firebase)
        } else if (notificationQueue[0] == Notification.PLAYING_AGAINST) {
            spannableString = SpannableString("Playing against,\n" +
                    MPController.opponent)
            imageButton!!.setBackgroundResource(R.drawable.firebase)
        }
        spannableString.setSpan(
            AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
            0, spannableString.length, 0
        )
        messageLabel!!.text = spannableString
    }

    /*
        Adds a notification to a 'waiting line'
     */
    private fun addToNotificationQueue(notification:Notification) {
        if (notificationQueue.size > 0) {
            var index = 0
            var remove = false
            // Loop through each notification
            while (index < notificationQueue.size) {
                // Clear repetitions and outdated notifications
                remove = (notification == notificationQueue[index]) ||
                        ((notification == Notification.FIREBASE_CONNECTED) &&
                        (notificationQueue[index] == Notification.FIREBASE_TROUBLE)) ||
                        ((notification == Notification.YES_GOOGLE_PLAY_GAME) &&
                        (notificationQueue[index] == Notification.NO_GOOGLE_PLAY_GAME)) ||
                        ((notification == Notification.YES_INTERNET) &&
                        (notificationQueue[index] == Notification.NO_INTERNET))
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

    private fun setupView(view:Button) {
        this.view = view
        view.isFocusable = false
        view.setOnClickListener {
            timerCount = 3.5
        }
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

    /*
        Draw the borders and the corner radius
        of the notification view
     */
    private var shape: GradientDrawable? = null
    private var cornerRadius:Int = 0
    fun setCornerRadiusAndBorderWidth(radius: Int) {
        shape = null
        shape = GradientDrawable()
        // Draw the button of the notification view
        shape!!.shape = GradientDrawable.RECTANGLE
        if (MainActivity.isThemeDark) {
            shape!!.setColor(Color.DKGRAY)
        } else {
            shape!!.setColor(Color.LTGRAY)
        }
        // Draw the corner radius
        cornerRadius = radius
        shape!!.cornerRadius = radius.toFloat()
        view!!.setBackgroundDrawable(shape)
    }

    /*
        Creates the image view
     */
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
        parentLayout!!.addView(imageButton!!)
        imageButton!!.setOnClickListener {
            timerCount = 3.5
        }
    }

    /*
        Creates the message label
     */
    private fun setupMessageLabel() {
        messageLabel = Button(view!!.context)
        messageLabel!!.layoutParams = LayoutParams((targetParams!!.width * 0.65).toInt(),
            targetParams!!.height, x + width + (targetParams!!.width * 0.03).toInt(),
            targetParams!!.y)
        parentLayout!!.addView(messageLabel!!)
        messageLabel!!.isAllCaps = false
        messageLabel!!.setBackgroundColor(Color.TRANSPARENT)
        messageLabel!!.textSize = height * 0.15f
        messageLabel!!.gravity = Gravity.CENTER
        messageLabel!!.setTextColor(Color.WHITE)
        messageLabel!!.setOnClickListener {
            timerCount = 3.5
        }
    }

}