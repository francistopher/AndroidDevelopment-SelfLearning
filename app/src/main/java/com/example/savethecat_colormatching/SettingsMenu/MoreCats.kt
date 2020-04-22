package com.example.savethecat_colormatching.SettingsMenu

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
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
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.ParticularViews.ColorOptions
import com.example.savethecat_colormatching.ParticularViews.MCView
import com.example.savethecat_colormatching.ParticularViews.SettingsMenu
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
    private var catViewHandler: View? = null
    private var catTitleLabel:CLabel? = null
    private var controlButton:CButton? = null
    private var mouseCoin:Button? = null

    private var purchaseDialog:AlertDialog? = null


    companion object {
        var myCatsDict:MutableMap<Cat, Int> = mutableMapOf(Cat.STANDARD to 1, Cat.BREADING to 0,
            Cat.TACO to 0, Cat.EGYPTIAN to 0, Cat.SUPER to 0, Cat.CHICKEN to 0, Cat.COOL to 0,
            Cat.NINJA to 0, Cat.FAT to 0)
        var displayedCatIndex:Int = -1
        private var catPrices:MutableList<Int> = mutableListOf(0, 1, 420, 420, 420,
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
        this.moreCatsButton = imageButton
        this.moreCatsButton!!.layoutParams = params
        setupParentLayout(parentLayout)
        this.moreCatsButton!!.setBackgroundColor(Color.TRANSPARENT)
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

    fun loadMyCatsData(myCatsString:String?) {
        if (myCatsString != null) {
            MoreCats.myCatsString = myCatsString
        } else {
            MoreCats.myCatsString = "sdd+1bdg00tco00etn00spR00ccn00col00nna00fat00"
            MainActivity.gameNotification!!.displayFirebaseTrouble()
        }
    }

    private var tempMyCatsDict:MutableMap<Cat, Int>? = null
    private var tempMyCats:String = ""
    private var sectionMyCats:String = ""
    private fun getMyCatsDictFromData(data:String) {
        tempMyCatsDict = mutableMapOf()
        tempMyCats = data + ""
        while (tempMyCats.count() > 0) {
            sectionMyCats = tempMyCats.substring(0, 5)
            updateCatValue(getCatType(sectionMyCats.substring(0, 3)), sectionMyCats.substring(3, 5))
            tempMyCats = tempMyCats.substring(5, tempMyCats.count())
        }
    }

    private fun updateCatValue(cat:Cat, stringValue:String) {
        myCatsDict[cat] = stringValue.toInt()
    }

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
            MoreCats.myCatsString = tempMyCats
            MainActivity.multiPlayerController!!.setDocumentData()
        } else {
            displayFailureReason()
        }
    }

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

    private fun translate(show:Boolean, duration:Float) {
        if (translateAnimatorSet != null) {
            translateAnimatorSet!!.cancel()
        }
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
            infoClosePopupYAnimator = ValueAnimator.ofInt(infoClosePopupShownY, infoClosePopupHiddenY)
            previousNextButtonYAnimator = ValueAnimator.ofInt(previousNextShownY, previousNextHiddenY)
            presentationCatYAnimator = ValueAnimator.ofInt(presentationCatShownY, presentationCatHiddenY)
            catHandlerYAnimator = ValueAnimator.ofInt(catHandlerShownY, catHandlerHiddenY)
            titleLabelYAnimator = ValueAnimator.ofInt(titleLabelShownY, titleLabelHiddenY)
            controlButtonYAnimator = ValueAnimator.ofInt(controlButtonShownY, controlButtonHiddenY)
            mouseCoinYAnimator = ValueAnimator.ofInt(mouseCoinShownY, mouseCoinHiddenY)
        }

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
        translateAnimatorSet = AnimatorSet()
        translateAnimatorSet!!.play(infoClosePopupYAnimator!!).with(previousNextButtonYAnimator!!)
            .with(presentationCatYAnimator!!).with(catHandlerYAnimator!!)
            .with(titleLabelYAnimator!!).with(controlButtonYAnimator!!).with(mouseCoinYAnimator!!)
        translateAnimatorSet!!.duration = (1000 * duration).toLong()
        if (show) {
            translateAnimatorSet!!.interpolator = EasingInterpolator(Ease.QUAD_IN)
        } else {
            translateAnimatorSet!!.interpolator = EasingInterpolator(Ease.QUAD_OUT)
        }
        translateAnimatorSet!!.start()
    }

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

    private fun setCat() {
        presentationCat!!.cat = myCatsDict.keys.toTypedArray()[displayedCatIndex]
        catTitleLabel!!.setText(catNames[displayedCatIndex])
        presentationCat!!.setStyle()
    }

    private fun setupParentLayout(layout:AbsoluteLayout) {
        this.parentLayout = layout
        layout.addView(this.moreCatsButton!!)
    }

    private var shape: GradientDrawable? = null
    private var controlButtonColor:Int = ColorOptions.pink
    fun setCornerRadiusAndBorderWidth(radius: Int, borderWidth: Int, viewID:Int) {
        shape = null
        shape = GradientDrawable()
        shape!!.shape = GradientDrawable.RECTANGLE
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
        if (borderWidth > 0) {
            if (MainActivity.isThemeDark) {
                shape!!.setStroke(borderWidth, Color.WHITE)
            } else {
                shape!!.setStroke(borderWidth, Color.BLACK)
            }
        }
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

    private fun setupCatPresentation() {
        val sideLength:Int = (contentViewParams!!.width * 0.7).toInt()
        val color:Int = if (MainActivity.isThemeDark) {
            Color.BLACK
        } else {
            Color.WHITE
        }
        presentationCat = PCatButton(button = Button(popupContainerView!!.context),
            parentLayout = parentLayout!!,
            params = LayoutParams(sideLength, sideLength,
                contentViewParams!!.x + ((contentViewParams!!.width - sideLength) * 0.5).toInt(),
                contentViewParams!!.y + ((contentViewParams!!.height - sideLength) * 0.5).toInt()),
            backgroundColor = color)
        presentationCat!!.setCornerRadiusAndBorderWidth(
            presentationCat!!.getOriginalParams().height / 5, 0,
            withBackground = true)
        presentationCat!!.show()
        presentationCatShownY = presentationCat!!.getOriginalParams().y
        presentationCatHiddenY = (presentationCatShownY + MainActivity.dHeight).toInt()
    }

    private fun setupMemoriamMessage() {
        memoriamMessage = CButton(button = Button(MainActivity.rootView!!.context),
            parentLayout = parentLayout!!, params = presentationCat!!.getOriginalParams())
        val memoriam = SpannableString("In Memory of\nSchrödinger\nthe Cat")
        memoriamMessage!!.setText(memoriam.toString(), false)
        memoriamMessage!!.setTextSize(memoriamMessage!!.getOriginalParams().height * 0.07f)
        memoriamMessage!!.backgroundColor = ColorOptions.blue
        memoriamMessage!!.setCornerRadiusAndBorderWidth((memoriamMessage!!.
        getOriginalParams().height / 5.1).toInt(), 0)
        memoriamMessage!!.getThis().alpha = 1f
        memoriamMessage!!.shrunk()
    }

    private fun setupCatTitleLabel() {
        val width:Int = (presentationCat!!.getOriginalParams().width * 0.8).toInt()
        val height:Int = (infoButton!!.getOriginalParams().height * 0.75).toInt()
        val x:Int = presentationCat!!.getOriginalParams().x +
                (presentationCat!!.getOriginalParams().width * 0.1).toInt()
        val y:Int = (catViewHandler!!.layoutParams as LayoutParams).y +
                (infoButton!!.getOriginalParams().height * 0.1).toInt()
        catTitleLabel = CLabel(textView = TextView(popupContainerView!!.context),
            parentLayout = parentLayout!!, params = LayoutParams(width, height, x, y))
        catTitleLabel!!.isInverted
        catTitleLabel!!.setStyle()
        catTitleLabel!!.setText("Standard Cat")
        catTitleLabel!!.setTextSize(catTitleLabel!!.getOriginalParams().height * 0.35f)
        titleLabelShownY = y
        titleLabelHiddenY = (titleLabelShownY + MainActivity.dHeight).toInt()
    }

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
        controlButton!!.backgroundColor = ColorOptions.pink
        controlButton!!.getThis().alpha = 1f
        controlButton!!.setStyle()
        setCornerRadiusAndBorderWidth((controlButton!!.getOriginalParams().height / 2.0).toInt(),
            0, 7)
        controlButton!!.setTextSize(height * 0.2f)
        fun setupMouseCoin() {
            mouseCoin = Button(popupContainerView!!.context)
            mouseCoin!!.layoutParams = LayoutParams((height * 0.8).toInt(), (height * 0.8).toInt(),
                x + (width * 0.66).toInt(), y + (height * 0.1).toInt())
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

    private fun purchaseCatButton() {
        if (MainActivity.isInternetReachable && MainActivity.isGooglePlayGameServicesAvailable) {
            myCatsDict[presentationCat!!.cat] = -1
            purchaseDialog!!.hide()
            setControlButtonAppearance()
            unlockAchievement()
            saveMyCatsData()
        } else {
            displayFailureReason()
        }
    }

    private fun getAchievement():String {
        var selectedAchievement = ""
        when (presentationCat!!.cat) {
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

    private fun unlockAchievement() {
        achievementsClient!!.unlock(getAchievement())
    }

    private fun controlButtonSelector() {
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

    private fun setupCatHandler() {
        val sideLength:Int = (contentViewParams!!.width * 0.7).toInt()
        val color:Int = if (MainActivity.isThemeDark) {
            Color.WHITE
        } else {
            Color.BLACK
        }
        val width:Int = (sideLength * 1.1).toInt()
        val height:Int = sideLength + (infoButton!!.getOriginalParams().height * 1.75).toInt()
        val x:Int = contentViewParams!!.x + ((contentViewParams!!.width - width) * 0.5).toInt()
        val y:Int = contentViewParams!!.y + ((contentViewParams!!.height - height) * 0.5).toInt()
        catViewHandler = View(MainActivity.rootView!!.context)
        catViewHandler!!.layoutParams = LayoutParams(width, height, x, y)
        catViewHandler!!.setBackgroundColor(color)
        setCornerRadiusAndBorderWidth((MainActivity.dUnitWidth * 2.5).toInt(),
            (MainActivity.dUnitWidth / 3).toInt(), 6)
        MainActivity.rootLayout!!.addView(catViewHandler!!)
        catHandlerShownY = y
        catHandlerHiddenY = catHandlerShownY + (MainActivity.dHeight).toInt()
    }

    private fun setupPopupView() {
        popupContainerView = Button(MainActivity.rootView!!.context)
        popupContainerView!!.setBackgroundColor(Color.BLUE)
        popupContainerView!!.layoutParams = LayoutParams(((MainActivity.dWidth) - (MainActivity.dWidth * 0.05)).toInt(),
            (MainActivity.dHeight - (MainActivity.dWidth * 0.0625) - MainActivity.dStatusBarHeight - MainActivity.dNavigationBarHeight).toInt(),
            (MainActivity.dWidth * 0.025).toInt(), (MainActivity.dStatusBarHeight + (MainActivity.dWidth * 0.0125)).toInt())
        setCornerRadiusAndBorderWidth((MainActivity.dUnitWidth * 1.5).toInt(),
            (MainActivity.dUnitWidth / 3).toInt(), 1)
        popupContainerView!!.isEnabled = false
        MainActivity.rootLayout!!.addView(popupContainerView!!)
        setupContentViewParams()
        infoClosePopupShownY = contentViewParams!!.y
        infoClosePopupHiddenY = (infoClosePopupShownY + MainActivity.dHeight).toInt()
    }

    private fun setupInfoButton() {
        infoButton = CButton(button = Button(MainActivity.rootView!!.context),
                            parentLayout = parentLayout!!,
                            params = LayoutParams((contentViewParams!!.width * 0.3f).toInt(),
                            (contentViewParams!!.width * 0.18f).toInt(), contentViewParams!!.x,
                                contentViewParams!!.y ))
        infoButton!!.getThis().setBackgroundColor(ColorOptions.blue)
        setCornerRadiusAndBorderWidth((MainActivity.dUnitWidth * 1.5).toInt(),
            (MainActivity.dUnitWidth / 3).toInt(), 2)
        infoButton!!.setTextSize(infoButton!!.getOriginalParams().height * 0.3f)
        infoButton!!.setText("i", false)
        infoButton!!.getThis().alpha = 1f
        infoButton!!.getThis().setOnClickListener {
            memoriamMessage!!.growUnGrow(0.5f)
        }
    }

    private fun setupCloseButton() {
        closeButton = CButton(button = Button(popupContainerView!!.context),
                            parentLayout = parentLayout!!,
                            params = LayoutParams((contentViewParams!!.width * 0.3f).toInt(),
                            (contentViewParams!!.width * 0.18f).toInt(),
                                (contentViewParams!!.x + contentViewParams!!.width -
                                        (contentViewParams!!.width * 0.3f)).toInt(),
                            contentViewParams!!.y))
        closeButton!!.getThis().setBackgroundColor(Color.RED)
        setCornerRadiusAndBorderWidth((MainActivity.dUnitWidth * 1.5).toInt(),
            (MainActivity.dUnitWidth / 3).toInt(), 3)
        closeButton!!.setTextSize(infoButton!!.getOriginalParams().height * 0.3f)
        closeButton!!.setText("x", false)
        closeButton!!.getThis().alpha = 1f
        closeButton!!.getThis().setOnClickListener {
            translate(false, 0.5f)
        }
    }

    private fun setupPreviousButton() {
        previousButton = CButton(button = Button(popupContainerView!!.context),
            parentLayout = parentLayout!!,
            params = LayoutParams((contentViewParams!!.width * 0.3f).toInt(),
                (contentViewParams!!.width * 0.18f).toInt(),
                (contentViewParams!!.x), contentViewParams!!.y + contentViewParams!!.height -
                        (contentViewParams!!.width * 0.18f).toInt()))
        previousButton!!.getThis().setBackgroundColor(Color.YELLOW)
        setCornerRadiusAndBorderWidth((MainActivity.dUnitWidth * 1.5).toInt(),
            (MainActivity.dUnitWidth / 3).toInt(), 4)
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
        setCornerRadiusAndBorderWidth((MainActivity.dUnitWidth * 1.5).toInt(),
            (MainActivity.dUnitWidth / 3).toInt(), 5)
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
            if (MainActivity.isGooglePlayGameServicesAvailable) {
                translate(true, 0.5f)
            }
            saveMyCatsData()
        }
    }

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
        if (isTransforming) {
            return
        } else {
            if (transformingSet != null) {
                transformingSet!!.cancel()
                transformingSet = null
            }
        }
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

    fun setStyle() {
        if (MainActivity.isThemeDark) {
            lightDominant()
        } else {
            darkDominant()
        }
    }
}

