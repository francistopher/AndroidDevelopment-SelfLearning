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
import android.util.Log
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
import com.example.savethecat_colormatching.Controllers.AudioController
import com.example.savethecat_colormatching.CustomViews.CButton
import com.example.savethecat_colormatching.CustomViews.CLabel
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.ParticularViews.ColorOptions
import com.example.savethecat_colormatching.ParticularViews.SettingsMenu
import com.example.savethecat_colormatching.R


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
        private var catPrices:MutableList<Int> = mutableListOf(0, 420, 420, 420, 420,
            420, 420, 420, 420)
        private var catNames:MutableList<String> = mutableListOf("Standard Cat", "Cat Breading",
            "Taco Cat", "Egyptian Cat", "Super Cat", "Chicken Cat", "Cool Cat", "Ninja Cat", "Fat Cat")
    }

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
        setupCatHandler()
        setupCatTitleLabel()
        setupControlButton()
        setupSelector()
        selectCat()
        setupAlertDialog()
        setStyle()
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
        presentationCat!!.removeFromParent()
        presentationCat!!.show()
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
        parentLayout!!.removeView(catTitleLabel!!.getThis())
        catTitleLabel!!.setText("Standard Cat")
        catTitleLabel!!.setTextSize(catTitleLabel!!.getOriginalParams().height * 0.35f)
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
        parentLayout!!.removeView(controlButton!!.getThis())
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
            mouseCoin!!.setOnClickListener {
                controlButtonSelector()
            }
        }
        setupMouseCoin()
        controlButton!!.getThis().isFocusable = false
        controlButton!!.getThis().setOnClickListener {
            controlButtonSelector()
        }
    }

    private fun setupAlertDialog() {
        val dialogBuilder = AlertDialog.Builder(MainActivity.staticSelf!!)
        val spannableText = SpannableString("Cat Purchase")
        spannableText.setSpan(
            AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
            0, spannableText.length, 0
        )
        dialogBuilder.setTitle(spannableText)
        val dialogClickListener = DialogInterface.OnClickListener{_, which ->
            when(which) {
                DialogInterface.BUTTON_POSITIVE -> print("ok")
                DialogInterface.BUTTON_NEUTRAL -> purchaseDialog!!.hide()
            }
        }
        dialogBuilder.setPositiveButton("Buy", dialogClickListener)
        dialogBuilder.setNegativeButton("Cancel", dialogClickListener)
        purchaseDialog = dialogBuilder.create()
    }

    private fun controlButtonSelector() {
        if (controlButton!!.getText()!!.toString() == "Selected") {
            Log.i("CONTROL BUTTON", "Selected")
        }
        if (controlButton!!.getText()!!.contains("Get for")) {
            AudioController.coinEarned()
            purchaseDialog!!.setMessage("Do you want to buy ${catNames[displayedCatIndex]}\n" +
                    "for ${catPrices[displayedCatIndex]} Mouse Coins")
            purchaseDialog!!.show()
        }
        if (controlButton!!.getText().toString() == "Select") {
            Log.i("CONTROL BUTTON", "Select")
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
    }

    private fun setupPopupView() {
        popupContainerView = Button(MainActivity.rootView!!.context)
        popupContainerView!!.setBackgroundColor(Color.BLUE)
        popupContainerView!!.layoutParams = LayoutParams((MainActivity.dWidth -
                (MainActivity.dWidth * 0.025 * 2)).toInt(),
            (MainActivity.dUnitHeight * 16 - ((MainActivity.dWidth * 0.05) +
                    (MainActivity.dNavigationBarHeight * 5.0))).toInt(),
            (MainActivity.dWidth * 0.025).toInt(),
            ((MainActivity.dWidth * 0.025) + (MainActivity.dNavigationBarHeight * 2.0)).toInt())
        setCornerRadiusAndBorderWidth((MainActivity.dUnitWidth * 1.5).toInt(),
            (MainActivity.dUnitWidth / 3).toInt(), 1)
        popupContainerView!!.isEnabled = false
        setupContentViewParams()
    }

    private fun setupInfoButton() {
        infoButton = CButton(button = Button(popupContainerView!!.context),
                            parentLayout = parentLayout!!,
                            params = LayoutParams((contentViewParams!!.width * 0.3f).toInt(),
                            (contentViewParams!!.width * 0.18f).toInt(), contentViewParams!!.x,
                                contentViewParams!!.y ))
        infoButton!!.getThis().setBackgroundColor(ColorOptions.blue)
        setCornerRadiusAndBorderWidth((MainActivity.dUnitWidth * 1.5).toInt(),
            (MainActivity.dUnitWidth / 3).toInt(), 2)
        infoButton!!.setTextSize(infoButton!!.getOriginalParams().height * 0.3f)
        infoButton!!.setText("i", false)
        parentLayout!!.removeView(infoButton!!.getThis())
        infoButton!!.getThis().alpha = 1f
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
        parentLayout!!.removeView(closeButton!!.getThis())
        closeButton!!.getThis().alpha = 1f
        closeButton!!.getThis().setOnClickListener {
            parentLayout!!.removeView(nextButton!!.getThis())
            parentLayout!!.removeView(previousButton!!.getThis())
            parentLayout!!.removeView(closeButton!!.getThis())
            parentLayout!!.removeView(infoButton!!.getThis())
            parentLayout!!.removeView(popupContainerView!!)
            presentationCat!!.removeFromParent()
            parentLayout!!.removeView(mouseCoin!!)
            parentLayout!!.removeView(controlButton!!.getThis())
            parentLayout!!.removeView(catTitleLabel!!.getThis())
            parentLayout!!.removeView(catViewHandler!!)
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
        parentLayout!!.removeView(previousButton!!.getThis())
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
        parentLayout!!.removeView(nextButton!!.getThis())
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
        } else {
            controlButton!!.setText("Get for ${catPrices[displayedCatIndex]}  MCs", false)
            controlButtonColor = ColorOptions.orange
            mouseCoin!!.alpha = 1f
        }
        setCornerRadiusAndBorderWidth((controlButton!!.getOriginalParams().height / 2.0).toInt(),
            0, 7)
    }

    private fun setupContentViewParams() {
        contentViewParams = popupContainerView!!.layoutParams as LayoutParams
    }

    private fun setupSelector() {
        moreCatsButton!!.setOnClickListener {
            parentLayout!!.addView(popupContainerView!!)
            parentLayout!!.addView(infoButton!!.getThis())
            parentLayout!!.addView(closeButton!!.getThis())
            parentLayout!!.addView(previousButton!!.getThis())
            parentLayout!!.addView(nextButton!!.getThis())
            parentLayout!!.addView(catViewHandler!!)
            presentationCat!!.addToParent()
            parentLayout!!.addView(catTitleLabel!!.getThis())
            parentLayout!!.addView(controlButton!!.getThis())
            parentLayout!!.addView(mouseCoin!!)
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

