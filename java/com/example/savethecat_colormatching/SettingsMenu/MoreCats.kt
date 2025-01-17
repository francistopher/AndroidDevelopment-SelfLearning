package com.example.savethecat_colormatching.SettingsMenu

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.Layout
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.animation.doOnEnd
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.example.savethecat_colormatching.Characters.Cat
import com.example.savethecat_colormatching.Characters.PCatButton
import com.example.savethecat_colormatching.CustomViews.CButton
import com.example.savethecat_colormatching.CustomViews.CLabel
import com.example.savethecat_colormatching.HeaderViews.SettingsMenu
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.ParticularViews.ColorOptions
import com.example.savethecat_colormatching.ParticularViews.MCView
import com.example.savethecat_colormatching.R
import com.google.android.gms.games.AchievementsClient
import com.google.android.gms.games.Games
import java.util.*
import kotlin.math.abs


class MoreCats (imageButton: ImageButton, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var expandedParams: LayoutParams? = null
    private var contractedParams: LayoutParams? = null

    private var parentLayout:AbsoluteLayout? = null

    private var moreCatsButton: ImageButton? = null

    private var contentViewParams:LayoutParams? = null
    private var infoButton:CButton? = null
    private var closeButton:CButton? = null
    private var previousButton:CButton? = null
    private var nextButton:CButton? = null
    private var memoriamMessage:CButton? = null
    private var popupContainerView: Button? = null

    private var presentationCat:PCatButton? = null
    private var catViewHandler: Button? = null
    private var catTitleLabel:CLabel? = null
    private var controlButton:CButton? = null
    private var mouseCoin:Button? = null

    private var purchaseDialog:AlertDialog? = null

    companion object {
        // Initialize the cats data
        var myCatsDict:MutableMap<Cat, Int> = mutableMapOf(Cat.STANDARD to 1, Cat.BREADING to 0,
            Cat.TACO to 0, Cat.EGYPTIAN to 0, Cat.SUPER to 0, Cat.CHICKEN to 0, Cat.COOL to 0,
            Cat.NINJA to 0, Cat.FAT to 0)
        var displayedCatIndex:Int = -1
        private var catPrices:MutableList<Int> = mutableListOf(0, 420, 420, 420, 420,
            420, 420, 420, 420)
        private var catNames:MutableList<String> = mutableListOf("Standard Cat", "Cat Breading",
            "Taco Cat", "Egyptian Cat", "Super Cat", "Chicken Cat", "Cool Cat", "Ninja Cat", "Fat Cat")
        var myCatsString:String = "sdd+1bdg00tco00etn00spR00ccn00col00nna00fat00"
    }

    private var translateAnimatorSet:AnimatorSet? = null
    private var infoClosePopupYAnimator:ValueAnimator? = null
    private var previousNextButtonYAnimator:ValueAnimator? = null
    private var presentationCatYAnimator:ValueAnimator? = null
    private var catHandlerYAnimator:ValueAnimator? = null
    private var titleLabelYAnimator:ValueAnimator? = null
    private var controlButtonYAnimator:ValueAnimator? = null
    private var mouseCoinYAnimator:ValueAnimator? = null

    private var infoClosePopupShownY:Int = 0
    private var previousNextShownY:Int = 0
    private var presentationCatShownY:Int = 0
    private var catHandlerShownY:Int = 0
    private var titleLabelShownY:Int = 0
    private var controlButtonShownY:Int = 0
    private var mouseCoinShownY:Int = 0

    private var infoClosePopupHiddenY:Int = 0
    private var previousNextHiddenY:Int = 0
    private var presentationCatHiddenY:Int = 0
    private var catHandlerHiddenY:Int = 0
    private var titleLabelHiddenY:Int = 0
    private var controlButtonHiddenY:Int = 0
    private var mouseCoinHiddenY:Int = 0

    private var achievementsClient:AchievementsClient? = null

    init {
        moreCatsButton = imageButton
        moreCatsButton!!.layoutParams = params
        setupParentLayout(parentLayout)
        moreCatsButton!!.setBackgroundColor(Color.TRANSPARENT)
        setupPopupView()
        setupInfoButton()
        setupCloseButton()
        setupPreviousButton()
        setupNextButton()
        setupCatPresentation()
        setupMemoriamMessage()
        setupCatHandler()
        setupCatTitleLabel()
        setupControlButton()
        setupSelector()
        selectCat()
        setupAlertDialog()
        setStyle()
        translate(false, 0f)
    }

    /*
        Loads the player's cat data if there is any
     */
    fun loadMyCatsData(myCatsString:String?) {
        if (myCatsString != null) {
            MoreCats.myCatsString = myCatsString
            updateMyCatsDict(myCatsString)
        } else {
            MoreCats.myCatsString = "sdd+1bdg00tco00etn00spR00ccn00col00nna00fat00"
            MainActivity.gameNotification!!.displayFirebaseTrouble()
        }
    }

    /*
        Updates the player's current cat data
     */
    private var tempMyCatsDict:MutableMap<Cat, Int>? = null
    private var tempMyCats:String = ""
    private var sectionMyCats:String = ""
    private var updateIndex:Int = 0
    private var next:Boolean = true
    private fun updateMyCatsDict(data:String) {
        updateIndex = 0
        next = true
        tempMyCatsDict = mutableMapOf()
        tempMyCats = data + ""
        while (tempMyCats.count() > 0) {
            sectionMyCats = tempMyCats.substring(0, 5)
            updateCatValue(getCatType(sectionMyCats.substring(0, 3)), sectionMyCats.substring(3, 5))
            updateIndex += 1
            tempMyCats = tempMyCats.substring(5, tempMyCats.count())
        }
    }

    /*
        Updates the cat value to represent whether or not its selected
     */
    private fun updateCatValue(cat:Cat, stringValue:String) {
        myCatsDict[cat] = stringValue.toInt()
        if (stringValue.toInt() > 0) {
            if (next) {
                next = false
                displayedCatIndex = updateIndex
                setCat()
                setControlButtonAppearance()
            }
        }
        if (abs(stringValue.toInt()) > 0) {
            unlockAchievement(cat)
        }
    }

    fun getSelectedCat():Cat {
        return presentationCat!!.cat
    }

    /*
        Returns the cat type based on a string snippet
     */
    private var tempCatType:Cat = Cat.STANDARD
    private fun getCatType(catType:String):Cat {
        when (catType) {
            "sdd" -> {
                tempCatType = Cat.STANDARD
            }
            "bdg" -> {
                tempCatType =  Cat.BREADING
            }
            "tco" -> {
                tempCatType =  Cat.TACO
            }
            "etn" -> {
                tempCatType =  Cat.EGYPTIAN
            }
            "spR" -> {
                tempCatType =  Cat.SUPER
            }
            "ccn" -> {
                tempCatType =  Cat.CHICKEN
            }
            "col" -> {
                tempCatType =  Cat.COOL
            }
            "nna" -> {
                tempCatType =  Cat.NINJA
            }
            "fat" -> {
                tempCatType =  Cat.FAT
            }
        }
        return tempCatType
    }

    /*
        Saves the player's current cat data, if unsuccessful
        sends a notification about the mishap
     */
    private fun saveMyCatsData() {
        tempMyCats = ""
        sectionMyCats = ""
        for ((cats, state) in myCatsDict) {
            sectionMyCats = cats.toString()
            tempMyCats += sectionMyCats[0].toLowerCase()
            tempMyCats += sectionMyCats[(sectionMyCats.length) / 2].toLowerCase()
            tempMyCats += sectionMyCats[sectionMyCats.length - 1].toLowerCase()
            if (state > 0) {
                tempMyCats += "+$state"
            } else if (state < 0) {
                tempMyCats += state.toString()
            } else {
                tempMyCats += "00"
            }
        }
        if (MainActivity.isInternetReachable && MainActivity.isGooglePlayGameServicesAvailable) {
            myCatsString = tempMyCats
            MainActivity.gdController!!.uploadMyCatsData()
        } else {
            displayFailureReason()
        }
    }

    /*
        Displays a notification about the mishap
     */
    private fun displayFailureReason() {
        if (!MainActivity.isGooglePlayGameServicesAvailable) {
            MainActivity.gameNotification!!.displayNoGooglePlayGameServices()
        }
        if (!MainActivity.isInternetReachable) {
            MainActivity.gameNotification!!.displayNoInternet()
        }
        MainActivity.gameNotification!!.displayFirebaseTrouble()
    }

    fun setupAchievementsClient() {
        achievementsClient = Games.getAchievementsClient(MainActivity.staticSelf!!,
            MainActivity.signedInAccount!!)
    }

    /*
        Brings the more cats view and content infront of other
        components of the game
     */
    fun bringToFront() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                MainActivity.staticSelf!!.runOnUiThread {
                    popupContainerView!!.bringToFront()
                    infoButton!!.getThis().bringToFront()
                    closeButton!!.getThis().bringToFront()
                    previousButton!!.getThis().bringToFront()
                    nextButton!!.getThis().bringToFront()
                    catViewHandler!!.bringToFront()
                    catTitleLabel!!.getThis().bringToFront()
                    presentationCat!!.getThis().bringToFront()
                    presentationCat!!.getThisImage().bringToFront()
                    memoriamMessage!!.getThis().bringToFront()
                    controlButton!!.getThis().bringToFront()
                    mouseCoin!!.bringToFront()
                }
            }
        } , 10)
    }

    /*
        Updates the position of the content view
        to show or hide the cats
     */
    private fun translate(show:Boolean, duration:Float) {
        // If the animation is running, cancel it
        if (translateAnimatorSet != null) {
            translateAnimatorSet!!.cancel()
        }
        // Either show or hide the content view
        if (show) {
            bringToFront()
            infoClosePopupYAnimator = ValueAnimator.ofInt(infoClosePopupHiddenY, infoClosePopupShownY)
            previousNextButtonYAnimator = ValueAnimator.ofInt(previousNextHiddenY, previousNextShownY)
            presentationCatYAnimator = ValueAnimator.ofInt(presentationCatHiddenY, presentationCatShownY)
            catHandlerYAnimator = ValueAnimator.ofInt(catHandlerHiddenY, catHandlerShownY)
            titleLabelYAnimator = ValueAnimator.ofInt(titleLabelHiddenY, titleLabelShownY)
            controlButtonYAnimator = ValueAnimator.ofInt(controlButtonHiddenY, controlButtonShownY)
            mouseCoinYAnimator = ValueAnimator.ofInt(mouseCoinHiddenY, mouseCoinShownY)
        } else {
            memoriamMessage!!.grow = false
            memoriamMessage!!.growUnGrow(0.05f)
            infoButton!!.setText("i", false)
            infoClosePopupYAnimator = ValueAnimator.ofInt(infoClosePopupShownY, infoClosePopupHiddenY)
            previousNextButtonYAnimator = ValueAnimator.ofInt(previousNextShownY, previousNextHiddenY)
            presentationCatYAnimator = ValueAnimator.ofInt(presentationCatShownY, presentationCatHiddenY)
            catHandlerYAnimator = ValueAnimator.ofInt(catHandlerShownY, catHandlerHiddenY)
            titleLabelYAnimator = ValueAnimator.ofInt(titleLabelShownY, titleLabelHiddenY)
            controlButtonYAnimator = ValueAnimator.ofInt(controlButtonShownY, controlButtonHiddenY)
            mouseCoinYAnimator = ValueAnimator.ofInt(mouseCoinShownY, mouseCoinHiddenY)
        }
        // Update the y position of the content view and all the sub content
        infoClosePopupYAnimator!!.addUpdateListener {
            infoButton!!.getThis().layoutParams = LayoutParams(
                infoButton!!.getOriginalParams().width,
                infoButton!!.getOriginalParams().height,
                infoButton!!.getOriginalParams().x,
                (it.animatedValue as Int)
            )
            closeButton!!.getThis().layoutParams = LayoutParams(
                closeButton!!.getOriginalParams().width,
                closeButton!!.getOriginalParams().height,
                closeButton!!.getOriginalParams().x,
                (it.animatedValue as Int)
            )
            popupContainerView!!.layoutParams = LayoutParams(
                (popupContainerView!!.layoutParams as LayoutParams).width,
                (popupContainerView!!.layoutParams as LayoutParams).height,
                (popupContainerView!!.layoutParams as LayoutParams).x,
                (it.animatedValue as Int)
            )
        }
        catHandlerYAnimator!!.addUpdateListener {
            catViewHandler!!.layoutParams = LayoutParams(
                (catViewHandler!!.layoutParams as LayoutParams).width,
                (catViewHandler!!.layoutParams as LayoutParams).height,
                (catViewHandler!!.layoutParams as LayoutParams).x,
                (it.animatedValue as Int)
            )
        }
        titleLabelYAnimator!!.addUpdateListener {
            catTitleLabel!!.getThis().layoutParams = LayoutParams(
                catTitleLabel!!.getOriginalParams().width,
                catTitleLabel!!.getOriginalParams().height,
                catTitleLabel!!.getOriginalParams().x,
                (it.animatedValue as Int)
            )
        }
        presentationCatYAnimator!!.addUpdateListener {
            presentationCat!!.getThis().layoutParams = LayoutParams(
                presentationCat!!.getOriginalParams().width,
                presentationCat!!.getOriginalParams().height,
                presentationCat!!.getOriginalParams().x,
                (it.animatedValue as Int)
            )
            presentationCat!!.getThisImage().layoutParams = LayoutParams(
                presentationCat!!.getOriginalParams().width,
                presentationCat!!.getOriginalParams().height,
                presentationCat!!.getOriginalParams().x,
                (it.animatedValue as Int)
            )
        }
        controlButtonYAnimator!!.addUpdateListener {
            controlButton!!.getThis().layoutParams = LayoutParams(
                controlButton!!.getOriginalParams().width,
                controlButton!!.getOriginalParams().height,
                controlButton!!.getOriginalParams().x,
                (it.animatedValue as Int)
            )
        }
        previousNextButtonYAnimator!!.addUpdateListener {
            previousButton!!.getThis().layoutParams = LayoutParams(
                previousButton!!.getOriginalParams().width,
                previousButton!!.getOriginalParams().height,
                previousButton!!.getOriginalParams().x,
                (it.animatedValue as Int)
            )
            nextButton!!.getThis().layoutParams = LayoutParams(
                nextButton!!.getOriginalParams().width,
                nextButton!!.getOriginalParams().height,
                nextButton!!.getOriginalParams().x,
                (it.animatedValue as Int)
            )
        }
        mouseCoinYAnimator!!.addUpdateListener {
            mouseCoin!!.layoutParams = LayoutParams(
                (mouseCoin!!.layoutParams as LayoutParams).width,
                (mouseCoin!!.layoutParams as LayoutParams).height,
                (mouseCoin!!.layoutParams as LayoutParams).x,
                (it.animatedValue as Int)
            )
        }
        // Set the properties animation
        translateAnimatorSet = AnimatorSet()
        translateAnimatorSet!!.play(infoClosePopupYAnimator!!).with(previousNextButtonYAnimator!!)
            .with(presentationCatYAnimator!!).with(catHandlerYAnimator!!)
            .with(titleLabelYAnimator!!).with(controlButtonYAnimator!!).with(mouseCoinYAnimator!!)
        translateAnimatorSet!!.duration = (1000 * duration).toLong()
        // Set the show and hide rates
        if (show) {
            translateAnimatorSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN)
        } else {
            translateAnimatorSet!!.interpolator = EasingInterpolator(Ease.QUAD_OUT)
        }
        // Remove view when it is hidden, and show view when its going to be shown
        translateAnimatorSet!!.doOnEnd {
            if (!show) {
                MainActivity.rootLayout!!.removeView(memoriamMessage!!.getThis())
            } else {
                MainActivity.rootLayout!!.addView(memoriamMessage!!.getThis())
            }
        }
        translateAnimatorSet!!.start()
    }

    /*
        Selects a cat to be displayed to the player
     */
    private fun selectCat() {
        // Loop for selecting cat for display
        var index = 0
        for (state in myCatsDict.values) {
            if (state > 0) {
                displayedCatIndex = index
                break
            }
            index += 1
        }
        setCat()
        setControlButtonAppearance()
    }

    /*
        Select a cat to be displayed and update
        the text label with the cat's name
     */
    private fun setCat() {
        presentationCat!!.cat = myCatsDict.keys.toTypedArray()[displayedCatIndex]
        catTitleLabel!!.setText(catNames[displayedCatIndex])
        presentationCat!!.setStyle()
        if (myCatsDict[presentationCat!!.cat] == 0) {
            presentationCat!!.getThisImage().alpha = 0f
            presentationCat!!.getThis().text = "?"
        } else {
            presentationCat!!.getThisImage().alpha = 1f
            presentationCat!!.getThis().text = ""
        }
    }

    private fun setupParentLayout(layout:AbsoluteLayout) {
        this.parentLayout = layout
        layout.addView(this.moreCatsButton!!)
    }

    /*
        Draw the corner radius and the border width of the component
     */
    private var shape: GradientDrawable? = null
    private var controlButtonColor:Int = ColorOptions.pink
    fun setCornerRadiusAndBorderWidth(radius: Int, borderWidth: Int, viewID:Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
        // Draw the background based on the OS theme / component
        if (viewID == 1) {
            if (MainActivity.isThemeDark) {
                shape!!.setColor(Color.BLACK)
            } else {
                shape!!.setColor(Color.WHITE)
            }
        } else if (viewID == 2) {
            shape!!.setColor(ColorOptions.blue)
        } else if (viewID == 3) {
            shape!!.setColor(Color.RED)
        } else if (viewID == 4 || viewID == 5) {
            shape!!.setColor(ColorOptions.yellow)
        } else if (viewID == 6) {
            if (MainActivity.isThemeDark) {
                shape!!.setColor(Color.WHITE)
            } else {
                shape!!.setColor(Color.BLACK)
            }
        } else if (viewID == 7) {
            shape!!.setColor(controlButtonColor)
        }
        // Draw the border
        if (borderWidth > 0) {
            if (MainActivity.isThemeDark) {
                shape!!.setStroke(borderWidth, Color.WHITE)
            } else {
                shape!!.setStroke(borderWidth, Color.BLACK)
            }
        }
        // Draw the corner radius
        if (viewID == 1) {
            shape!!.cornerRadius = radius.toFloat()
            popupContainerView!!.setBackgroundDrawable(shape)
        } else if (viewID == 2) {
            shape!!.cornerRadii = floatArrayOf(radius.toFloat(), radius.toFloat(), 0f, 0f,
                radius.toFloat(), radius.toFloat(), 0f, 0f)
            infoButton!!.getThis().setBackgroundDrawable(shape)
        } else if (viewID == 3) {
            shape!!.cornerRadii = floatArrayOf(0f, 0f, radius.toFloat(), radius.toFloat(),
                0f, 0f, radius.toFloat(), radius.toFloat())
            closeButton!!.getThis().setBackgroundDrawable(shape)
        } else if (viewID == 4) {
            shape!!.cornerRadii = floatArrayOf(0f, 0f, radius.toFloat(), radius.toFloat(),
                0f, 0f, radius.toFloat(), radius.toFloat())
            previousButton!!.getThis().setBackgroundDrawable(shape)
        } else if (viewID == 5) {
            shape!!.cornerRadii = floatArrayOf(radius.toFloat(), radius.toFloat(), 0f, 0f,
                radius.toFloat(), radius.toFloat(), 0f, 0f)
            nextButton!!.getThis().setBackgroundDrawable(shape)
        } else if (viewID == 6) {
            shape!!.cornerRadius = radius.toFloat()
            catViewHandler!!.setBackgroundDrawable(shape)
        } else if (viewID == 7) {
            shape!!.cornerRadius = radius.toFloat()
            controlButton!!.getThis().setBackgroundDrawable(shape)
        }
    }

    /*
        Create the button that houses the currently
        selected cat
     */
    private fun setupCatPresentation() {
        var sideLength:Int = 0
        // Set the sidelength based on the screen aspect ratio
        if (MainActivity.dAspectRatio >= 1.8) {
            sideLength = (contentViewParams!!.width * 0.65).toInt()
        } else if (MainActivity.dAspectRatio >= 1.4) {
            sideLength = (contentViewParams!!.width * 0.6).toInt()
        } else {
            sideLength = (contentViewParams!!.width * 0.5).toInt()
        }
        presentationCat = PCatButton(button = Button(popupContainerView!!.context),
            parentLayout = parentLayout!!,
            params = LayoutParams(sideLength, sideLength,
                contentViewParams!!.x + ((contentViewParams!!.width - sideLength) * 0.5).toInt(),
                contentViewParams!!.y + ((contentViewParams!!.height - sideLength) * 0.5).toInt()))
        presentationCat!!.show()
        presentationCatShownY = presentationCat!!.getOriginalParams().y
        presentationCatHiddenY = (presentationCatShownY + MainActivity.dHeight).toInt()
        presentationCat!!.getThis().textAlignment = View.TEXT_ALIGNMENT_CENTER
        presentationCat!!.getThis().textSize = presentationCat!!.getOriginalParams().height * 0.20f
        presentationCat!!.getThis().typeface = Typeface.createFromAsset(
            MainActivity.rootView!!.context.assets, "SleepyFatCat.ttf")
    }

    /*
        Create the memoriam message label
     */
    private fun setupMemoriamMessage() {
        memoriamMessage = CButton(button = Button(MainActivity.rootView!!.context),
            parentLayout = parentLayout!!, params = presentationCat!!.getOriginalParams())
        val memoriam = SpannableString("In Memory of\nSchrödinger\nthe Cat")
        memoriamMessage!!.setText(memoriam.toString(), false)
        memoriamMessage!!.setTextSize(memoriamMessage!!.getOriginalParams().height * 0.07f)
        memoriamMessage!!.backgroundColor = ColorOptions.blue
        memoriamMessage!!.getThis().alpha = 1f
        memoriamMessage!!.shrunk()
    }

    /*
        Create the title label that displays the
        name of the currently selected cat
     */
    private fun setupCatTitleLabel() {
        val width:Int = (presentationCat!!.getOriginalParams().width * 0.725).toInt()
        val height:Int = (infoButton!!.getOriginalParams().height * 0.75).toInt()
        val x:Int = presentationCat!!.getOriginalParams().x +
                (presentationCat!!.getOriginalParams().width * 0.125).toInt()
        val y:Int = (catViewHandler!!.layoutParams as LayoutParams).y +
                (infoButton!!.getOriginalParams().height * 0.1).toInt()
        catTitleLabel = CLabel(textView = TextView(popupContainerView!!.context),
            parentLayout = parentLayout!!, params = LayoutParams(width, height, x, y))
        catTitleLabel!!.isInverted
        catTitleLabel!!.setStyle()
        catTitleLabel!!.setText("Standard Cat")
        if (MainActivity.dAspectRatio >= 1.8) {
            catTitleLabel!!.setTextSize(catTitleLabel!!.getOriginalParams().height * 0.25f)
        } else if (MainActivity.dAspectRatio >= 1.7) {
            catTitleLabel!!.setTextSize(catTitleLabel!!.getOriginalParams().height * 0.25f)
        } else {
            catTitleLabel!!.setTextSize(catTitleLabel!!.getOriginalParams().height * 0.225f)
        }
        titleLabelShownY = y
        titleLabelHiddenY = (titleLabelShownY + MainActivity.dHeight).toInt()
    }

    /*
        Setup the button for selecting/purchasing cats
     */
    private fun setupControlButton() {
        val width:Int = (presentationCat!!.getOriginalParams().width * 0.8).toInt()
        val height:Int = (infoButton!!.getOriginalParams().height * 0.75).toInt()
        val x:Int = presentationCat!!.getOriginalParams().x +
                (presentationCat!!.getOriginalParams().width * 0.1).toInt()
        val y:Int = (catViewHandler!!.layoutParams as LayoutParams).y +
                (catViewHandler!!.layoutParams as LayoutParams).height  -
                (infoButton!!.getOriginalParams().height * 0.06).toInt() -
                catTitleLabel!!.getOriginalParams().height
        controlButton = CButton(button = Button(popupContainerView!!.context),
            parentLayout = parentLayout!!,
            params =  LayoutParams(width, height, x, y))
        controlButton!!.isInverted = true
        controlButton!!.backgroundColor = ColorOptions.pink
        controlButton!!.getThis().alpha = 1f
        controlButton!!.setStyle()
        controlButton!!.setTextSize(height * 0.2f)
        // Setup the mouse coin for the purchasing state of the button
        fun setupMouseCoin() {
            mouseCoin = Button(popupContainerView!!.context)
            var mouseCoinParams = LayoutParams(0,0,0,0)
            if (MainActivity.dAspectRatio >= 1.7) {
                mouseCoinParams.width = (height * 0.65).toInt()
                mouseCoinParams.height = (height * 0.65).toInt()
                mouseCoinParams.x =  x + (width * 0.67).toInt()
                mouseCoinParams.y = y + (height * 0.155).toInt()
            } else if (MainActivity.dAspectRatio >= 1.4) {
                mouseCoinParams.width = (height * 0.7).toInt()
                mouseCoinParams.height = (height * 0.7).toInt()
                mouseCoinParams.x =  x + (width * 0.67).toInt()
                mouseCoinParams.y = y + (height * 0.15).toInt()
            } else {
                mouseCoinParams.width = (height * 0.7).toInt()
                mouseCoinParams.height = (height * 0.7).toInt()
                mouseCoinParams.x =  x + (width * 0.7).toInt()
                mouseCoinParams.y = y + (height * 0.15).toInt()
            }
            mouseCoin!!.layoutParams = mouseCoinParams
            mouseCoin!!.setBackgroundResource(R.drawable.mousecoin)
            mouseCoinShownY = (mouseCoin!!.layoutParams as LayoutParams).y
            mouseCoinHiddenY = (mouseCoinShownY + MainActivity.dHeight).toInt()
            parentLayout!!.addView(mouseCoin!!)
            mouseCoin!!.setOnClickListener {
                controlButtonSelector()
            }
        }
        setupMouseCoin()
        controlButton!!.getThis().isFocusable = false
        controlButton!!.getThis().setOnClickListener {
            controlButtonSelector()
        }
        controlButtonShownY = y
        controlButtonHiddenY = (controlButtonShownY + MainActivity.dHeight).toInt()
    }

    /*
        Setup alert dialog to notify player about the cat purchase
     */
    private fun setupAlertDialog() {
        val dialogBuilder = AlertDialog.Builder(MainActivity.staticSelf!!)
        val spannableString = SpannableString("Cat Purchase")
        spannableString.setSpan(
            AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
            0, spannableString.length, 0
        )
        dialogBuilder.setTitle(spannableString)
        val dialogClickListener = DialogInterface.OnClickListener{_, which ->
            when(which) {
                DialogInterface.BUTTON_POSITIVE -> purchaseCatButton()
                DialogInterface.BUTTON_NEUTRAL -> purchaseDialog!!.hide()
            }
        }
        dialogBuilder.setPositiveButton("Buy", dialogClickListener)
        dialogBuilder.setNegativeButton("Cancel", dialogClickListener)
        purchaseDialog = dialogBuilder.create()
    }

    /*
        Purchases a cat button and reduces the amount
        of mouse coins the user has obtained
     */
    private fun purchaseCatButton() {
        if (MainActivity.isInternetReachable && MainActivity.isGooglePlayGameServicesAvailable) {
            myCatsDict[presentationCat!!.cat] = -1
            purchaseDialog!!.hide()
            setControlButtonAppearance()
            unlockAchievement(presentationCat!!.cat)
            saveMyCatsData()
        } else {
            displayFailureReason()
        }
    }

    /*
        Prizes the user with a reward based off the cat purchased
     */
    private fun getAchievement(cat:Cat):String {
        var selectedAchievement = ""
        when (cat) {
            Cat.BREADING -> {
                selectedAchievement = MainActivity.staticSelf!!.getString(R.string.breading_id)
            }
            Cat.TACO -> {
                selectedAchievement = MainActivity.staticSelf!!.getString(R.string.taco_id)
            }
            Cat.EGYPTIAN -> {
                selectedAchievement = MainActivity.staticSelf!!.getString(R.string.egyptian_id)
            }
            Cat.SUPER -> {
                selectedAchievement = MainActivity.staticSelf!!.getString(R.string.super_id)
            }
            Cat.CHICKEN -> {
                selectedAchievement = MainActivity.staticSelf!!.getString(R.string.chicken_id)
            }
            Cat.COOL -> {
                selectedAchievement = MainActivity.staticSelf!!.getString(R.string.cool_id)
            }
            Cat.NINJA -> {
                selectedAchievement = MainActivity.staticSelf!!.getString(R.string.ninja_id)
            }
            Cat.FAT -> {
                selectedAchievement = MainActivity.staticSelf!!.getString(R.string.fat_id)
            }
        }
        return selectedAchievement
    }

    /*
        Unlocks a specific acheivement
     */
    private fun unlockAchievement(cat:Cat) {
        if (MainActivity.isInternetReachable && MainActivity.isGooglePlayGameServicesAvailable) {
            achievementsClient!!.unlock(getAchievement(cat))
        } else {
            displayFailureReason()
        }
    }

    /*
        Updates the text for the control button
        based on the state of the cat selected
     */
    private fun controlButtonSelector() {
        // For purchasing, it displays an offer on the control button
        if (controlButton!!.getText()!!.contains("Get for")) {
            if (MainActivity.isInternetReachable && MainActivity.isGooglePlayGameServicesAvailable) {
                if (MCView.mouseCoinCount < catPrices[displayedCatIndex]) {
                    MCView.neededMouseCoinCount =
                        catPrices[displayedCatIndex] - MCView.mouseCoinCount
                    MainActivity.gameNotification!!.displayNeedMoreMouseCoins()
                } else {
                    val spannableString = SpannableString(
                        "Do you want to buy " +
                                "${catNames[displayedCatIndex]}\n for " +
                                "${catPrices[displayedCatIndex]} Mouse Coins"
                    )
                    spannableString.setSpan(
                        AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                        0, spannableString.length, 0
                    )
                    purchaseDialog!!.setMessage(spannableString)
                    purchaseDialog!!.show()
                }
            } else {
                displayFailureReason()
            }
        } else if (controlButton!!.getText().toString() == "Select") {
            for ((cat, state) in myCatsDict) {
                if (cat == presentationCat!!.cat) {
                    myCatsDict[cat] = abs(state)
                } else {
                    myCatsDict[cat] = -abs(state)
                }
            }
            setControlButtonAppearance()
            MainActivity.boardGame!!.getCatButtons().updateCatType(presentationCat!!.cat)
            saveMyCatsData()
        }
    }

    /*
        Setup the container for the cat and the title label
     */
    private fun setupCatHandler() {
        val color:Int = if (MainActivity.isThemeDark) {
            Color.WHITE
        } else {
            Color.BLACK
        }
        var sideLength:Int = 0
        if (MainActivity.dAspectRatio >= 1.8) {
            sideLength = (contentViewParams!!.width * 0.65).toInt()
        } else if (MainActivity.dAspectRatio >= 1.4) {
            sideLength = (contentViewParams!!.width * 0.6).toInt()
        } else {
            sideLength = (contentViewParams!!.width * 0.5).toInt()
        }
        val width:Int = (sideLength * 1.1).toInt()
        val height:Int = sideLength + (infoButton!!.getOriginalParams().height * 1.75).toInt()
        val x:Int = contentViewParams!!.x + ((contentViewParams!!.width - width) * 0.5).toInt()
        val y:Int = contentViewParams!!.y + ((contentViewParams!!.height - height) * 0.5).toInt()
        catViewHandler = Button(MainActivity.rootView!!.context)
        catViewHandler!!.layoutParams = LayoutParams(width, height, x, y)
        catViewHandler!!.setBackgroundColor(color)
        MainActivity.rootLayout!!.addView(catViewHandler!!)
        catViewHandler!!.isEnabled = false
        catHandlerShownY = y
        catHandlerHiddenY = catHandlerShownY + (MainActivity.dHeight).toInt()
    }

    /*
        Setup the container view for the entire view
     */
    private fun setupPopupView() {
        popupContainerView = Button(MainActivity.rootView!!.context)
        popupContainerView!!.setBackgroundColor(Color.BLUE)
        popupContainerView!!.layoutParams = LayoutParams(((MainActivity.dWidth) - (MainActivity.dWidth * 0.05)).toInt(),
            (MainActivity.dHeight - (MainActivity.dWidth * 0.0625) - MainActivity.dStatusBarHeight - MainActivity.dNavigationBarHeight).toInt(),
            (MainActivity.dWidth * 0.025).toInt(), (MainActivity.dStatusBarHeight + (MainActivity.dWidth * 0.0125)).toInt())
        popupContainerView!!.isEnabled = false
        MainActivity.rootLayout!!.addView(popupContainerView!!)
        setupContentViewParams()
        infoClosePopupShownY = contentViewParams!!.y
        infoClosePopupHiddenY = (infoClosePopupShownY + MainActivity.dHeight).toInt()
    }

    /*
        Updates the style of the components based on
        the theme of the operating system
     */
    private fun setCompiledStyle() {
        memoriamMessage!!.setStyle()
        catTitleLabel!!.setStyle()
        infoButton!!.setStyle()
        closeButton!!.setStyle()
        previousButton!!.setStyle()
        nextButton!!.setStyle()
        controlButton!!.setStyle()
        presentationCat!!.setStyle()
        memoriamMessage!!.setCornerRadiusAndBorderWidth((memoriamMessage!!.
        getOriginalParams().height / 5.1).toInt(), 0)
        setCornerRadiusAndBorderWidth((MainActivity.dUnitWidth * 1.5).toInt(),
            (MainActivity.dUnitWidth / 3).toInt(), 1)
        setCornerRadiusAndBorderWidth((MainActivity.dUnitWidth * 1.5).toInt(),
            (MainActivity.dUnitWidth / 3).toInt(), 2)
        setCornerRadiusAndBorderWidth((MainActivity.dUnitWidth * 1.5).toInt(),
            (MainActivity.dUnitWidth / 3).toInt(), 3)
        setCornerRadiusAndBorderWidth((MainActivity.dUnitWidth * 1.5).toInt(),
            (MainActivity.dUnitWidth / 3).toInt(), 4)
        setCornerRadiusAndBorderWidth((MainActivity.dUnitWidth * 1.5).toInt(),
            (MainActivity.dUnitWidth / 3).toInt(), 5)
        setCornerRadiusAndBorderWidth((MainActivity.dUnitWidth * 2.5).toInt(),
            (MainActivity.dUnitWidth / 3).toInt(), 6)
        setCornerRadiusAndBorderWidth((controlButton!!.getOriginalParams().height / 2.0).toInt(),
            0, 7)
    }

    /*
        Setup the info button that displays the memeoriam message
     */
    private fun setupInfoButton() {
        infoButton = CButton(button = Button(MainActivity.rootView!!.context),
                            parentLayout = parentLayout!!,
                            params = LayoutParams((contentViewParams!!.width * 0.3f).toInt(),
                            (contentViewParams!!.width * 0.18f).toInt(), contentViewParams!!.x,
                                contentViewParams!!.y ))
        infoButton!!.getThis().setBackgroundColor(ColorOptions.blue)
        infoButton!!.setTextSize(infoButton!!.getOriginalParams().height * 0.3f)
        infoButton!!.setText("i", false)
        infoButton!!.getThis().alpha = 1f
        infoButton!!.getThis().setOnClickListener {
            memoriamMessage!!.growUnGrow(0.5f)
            if (memoriamMessage!!.grow) {
                infoButton!!.setText("i", false)
            } else {
                infoButton!!.setText("x", false)
            }
        }
    }

    /*
        Setup the close button that makes the more cats view disappear
     */
    private fun setupCloseButton() {
        closeButton = CButton(button = Button(popupContainerView!!.context),
                            parentLayout = parentLayout!!,
                            params = LayoutParams((contentViewParams!!.width * 0.3f).toInt(),
                            (contentViewParams!!.width * 0.18f).toInt(),
                                (contentViewParams!!.x + contentViewParams!!.width -
                                        (contentViewParams!!.width * 0.3f)).toInt(),
                            contentViewParams!!.y))
        closeButton!!.getThis().setBackgroundColor(Color.RED)
        closeButton!!.setTextSize(infoButton!!.getOriginalParams().height * 0.3f)
        closeButton!!.setText("x", false)
        closeButton!!.getThis().alpha = 1f
        closeButton!!.getThis().setOnClickListener {
            translate(false, 0.5f)
            if (MainActivity.gameResults!!.getThis().alpha > 0f) {
                MainActivity.glovePointer!!.getThis().alpha = 1f
                MainActivity.gameResults!!.getWatchAdButton().getThis().alpha = 1f
                MainActivity.gameResults!!.getMouseCoin().alpha = 1f
            }
        }
    }

    /*
        Setup the previous button that changes to the
        previous cat in the list
     */
    private fun setupPreviousButton() {
        previousButton = CButton(button = Button(popupContainerView!!.context),
            parentLayout = parentLayout!!,
            params = LayoutParams((contentViewParams!!.width * 0.3f).toInt(),
                (contentViewParams!!.width * 0.18f).toInt(),
                (contentViewParams!!.x), contentViewParams!!.y + contentViewParams!!.height -
                        (contentViewParams!!.width * 0.18f).toInt()))
        previousButton!!.getThis().setBackgroundColor(Color.YELLOW)
        previousButton!!.setTextSize(previousButton!!.getOriginalParams().height * 0.3f)
        previousButton!!.setText("<", false)
        previousButton!!.getThis().alpha = 1f
        previousButton!!.getThis().setOnClickListener {
            if (displayedCatIndex == 0) {
                displayedCatIndex = 8
            } else {
                displayedCatIndex -= 1
            }
            setCat()
            setControlButtonAppearance()
        }
        previousNextShownY = previousButton!!.getOriginalParams().y
        previousNextHiddenY = (previousNextShownY + MainActivity.dHeight).toInt()
    }

    /*
        Setup the next button that selects the following
        cat in the list
     */
    private fun setupNextButton() {
        nextButton = CButton(button = Button(popupContainerView!!.context),
            parentLayout = parentLayout!!,
            params = LayoutParams((contentViewParams!!.width * 0.3f).toInt(),
                (contentViewParams!!.width * 0.18f).toInt(),
                (contentViewParams!!.x + contentViewParams!!.width -
                        (contentViewParams!!.width * 0.3f)).toInt(),
                contentViewParams!!.y + contentViewParams!!.height -
                        (contentViewParams!!.width * 0.18f).toInt()))
        nextButton!!.getThis().setBackgroundColor(Color.YELLOW)
        nextButton!!.setTextSize(nextButton!!.getOriginalParams().height * 0.3f)
        nextButton!!.setText(">", false)
        nextButton!!.getThis().alpha = 1f
        nextButton!!.getThis().setOnClickListener {
            if (displayedCatIndex == 8) {
                displayedCatIndex = 0
            } else {
                displayedCatIndex += 1
            }
            setCat()
            setControlButtonAppearance()
        }
    }

    /*
        Updates the text of the control button based off the
        selected cat button
     */
    private fun setControlButtonAppearance() {
        if (myCatsDict[presentationCat!!.cat]!! > 0) {
            controlButton!!.setText("Selected", false)
            controlButtonColor = ColorOptions.pink
            mouseCoin!!.alpha = 0f
        } else if (myCatsDict[presentationCat!!.cat]!! == 0) {
            controlButton!!.setText("Get for ${catPrices[displayedCatIndex]}  MCs", false)
            controlButtonColor = ColorOptions.orange
            mouseCoin!!.alpha = 1f
        } else if (myCatsDict[presentationCat!!.cat]!! < 0) {
            controlButton!!.setText("Select", false)
            controlButtonColor = ColorOptions.green
            mouseCoin!!.alpha = 0f
        }
        setCornerRadiusAndBorderWidth((controlButton!!.getOriginalParams().height / 2.0).toInt(),
            0, 7)
    }


    private fun setupContentViewParams() {
        contentViewParams = popupContainerView!!.layoutParams as LayoutParams
    }

    private fun setupSelector() {
        moreCatsButton!!.setOnClickListener {
            translate(true, 0.5f)
            if (MainActivity.gameResults!!.getThis().alpha > 0f) {
                MainActivity.glovePointer!!.getThis().alpha = 0f
                MainActivity.gameResults!!.getWatchAdButton().getThis().alpha = 0f
                MainActivity.gameResults!!.getMouseCoin().alpha = 0f
            }
        }
    }

    /*
        Transforms the more cats button to the expanded or contracted
        state of the settings menu
     */
    private var transformingSet: AnimatorSet? = null
    private var transformX: ValueAnimator? = null
    private var transformY: ValueAnimator? = null
    private var transformWidth: ValueAnimator? = null
    private var transformHeight: ValueAnimator? = null
    private var isTransforming:Boolean = false
    private var x:Int = 0
    private var y:Int = 0
    private var width:Int = 0
    private var height:Int = 0
    fun expandOrContract() {
        // If the transformation animation is running, cancel it
        if (isTransforming) {
            return
        } else {
            if (transformingSet != null) {
                transformingSet!!.cancel()
                transformingSet = null
            }
        }
        // If the settings menu button is expanded, contract it, visa verse
        if (SettingsMenu.isExpanded) {
            transformX = ValueAnimator.ofInt(getExpandedParams().x,
                getContractedParams().x)
            transformY = ValueAnimator.ofInt(getExpandedParams().y,
                getContractedParams().y)
            transformWidth = ValueAnimator.ofInt(getExpandedParams().width,
                getContractedParams().width)
            transformHeight = ValueAnimator.ofInt(getExpandedParams().height,
                getContractedParams().height)
        } else {
            transformX = ValueAnimator.ofInt(getContractedParams().x,
                getExpandedParams().x)
            transformY = ValueAnimator.ofInt(getContractedParams().y,
                getExpandedParams().y)
            transformWidth = ValueAnimator.ofInt(getContractedParams().width,
                getExpandedParams().width)
            transformHeight = ValueAnimator.ofInt(getContractedParams().height,
                getExpandedParams().height)
        }
        // Update the position and size of the more cats button
        transformX!!.addUpdateListener {
            x = it.animatedValue as Int
            moreCatsButton!!.layoutParams = LayoutParams(width, height, x, y)
        }
        transformY!!.addUpdateListener {
            y = it.animatedValue as Int
            moreCatsButton!!.layoutParams = LayoutParams(width, height, x, y)
        }
        transformWidth!!.addUpdateListener {
            width = it.animatedValue as Int
            moreCatsButton!!.layoutParams = LayoutParams(width, height, x, y)
        }
        transformHeight!!.addUpdateListener {
            height = it.animatedValue as Int
            moreCatsButton!!.layoutParams = LayoutParams(width, height, x, y)
        }
        // Set the transformation properties
        transformingSet = AnimatorSet()
        transformingSet!!.play(transformX!!).with(transformY!!).with(transformWidth!!).with(transformHeight!!)
        transformingSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN_OUT)
        transformingSet!!.duration = 1000
        transformingSet!!.start()
        isTransforming = true
        transformingSet!!.doOnEnd {
            isTransforming = false
        }
    }

    fun getThis(): ImageButton {
        return moreCatsButton!!
    }

    fun setContractedParams(params: LayoutParams) {
        contractedParams = params
    }

    fun getContractedParams(): LayoutParams {
        return contractedParams!!
    }

    fun setExpandedParams(params: LayoutParams) {
        expandedParams = params
    }

    fun getExpandedParams(): LayoutParams {
        return expandedParams!!
    }

    private fun lightDominant() {
        moreCatsButton!!.setBackgroundResource(R.drawable.lightmorecats)
    }

    private fun darkDominant() {
        moreCatsButton!!.setBackgroundResource(R.drawable.darkmorecats)
    }

    /*
        Update the colors of the button based off
        the theme of the operating system
     */
    fun setStyle() {
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
        setCompiledStyle()
    }
}

