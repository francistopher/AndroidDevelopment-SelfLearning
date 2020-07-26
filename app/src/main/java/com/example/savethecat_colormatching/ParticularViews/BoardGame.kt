package com.example.savethecat_colormatching.ParticularViews

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import android.widget.ImageButton
import com.example.savethecat_colormatching.Characters.CatButton
import com.example.savethecat_colormatching.Characters.CatButtons
import com.example.savethecat_colormatching.ConcludingViews.GameResults
import com.example.savethecat_colormatching.Controllers.AudioController
import com.example.savethecat_colormatching.Controllers.MPController
import com.example.savethecat_colormatching.CustomViews.CButton
import com.example.savethecat_colormatching.CustomViews.ShrinkType
import com.example.savethecat_colormatching.HeaderViews.AttackMeter
import com.example.savethecat_colormatching.HeaderViews.SettingsMenu
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.SettingsMenu.LeaderBoard
import java.util.*

class BoardGame(boardView: View, parentLayout: AbsoluteLayout, params: LayoutParams) {

    private var boardView: View? = null
    private var originalParams: LayoutParams? = null

    private var currentStage: Int = 1
    private var gridColors: Array<IntArray>? = null

    private var catButtons: CatButtons? = null
    private var gridColorsCount: MutableMap<Int, Int>? = null

    companion object {
        var rowsAndColumns = Pair(0, 0)
        var boardGameContext: Context? = null
        var boardGameLayout: AbsoluteLayout? = null
        var singlePlayerButton: CButton? = null
        var twoPlayerButton: CButton? = null
        var searchMG: SearchMG? = null
    }

    init {
        this.boardView = boardView
        boardGameContext = boardView.context
        boardGameLayout = AbsoluteLayout(boardGameContext)
        this.boardView!!.layoutParams = params
        // Set board game player buttons
        MainActivity.rootLayout!!.addView(boardGameLayout)
        parentLayout.addView(this.boardView!!)
        setOriginalParams(params = params)
        catButtons = CatButtons()
    }

    fun getThis(): View {
        return this.boardView!!
    }

    fun setOriginalParams(params: LayoutParams) {
        originalParams = params
    }

    fun getOriginalParams(): LayoutParams {
        return originalParams!!
    }

    /*
        Reset the cat buttons from the previous round
        Build the grid buttons and the corresponding grid colors
        Create the search multiplayer game view
     */
    var searchMGSideTemplateParams: LayoutParams? = null
    fun buildGame() {
        rowsAndColumns = getRowsAndColumns(currentStage = currentStage)
        ColorOptions.setSelectionColors()
        catButtons!!.removeAll()
        buildGridColors()
        buildGridButtons()
        catButtons!!.loadPreviousCats()
        recordGridColorsUsed()
        if (searchMG == null) {
            searchMGSideTemplateParams = catButtons!!.getCurrentCatButtons()[0].getOriginalParams()
            searchMG = SearchMG(
                button = Button(MainActivity.rootView!!.context),
                parentLayout = MainActivity.rootLayout!!,
                params = LayoutParams(
                    (searchMGSideTemplateParams!!.width / 2.1).toInt(),
                    (searchMGSideTemplateParams!!.height / 2.1).toInt(),
                    (searchMGSideTemplateParams!!.x + ((searchMGSideTemplateParams!!.width -
                            (searchMGSideTemplateParams!!.width / 2.1)) * 0.5).toInt()),
                    (searchMGSideTemplateParams!!.y + ((searchMGSideTemplateParams!!.height -
                            (searchMGSideTemplateParams!!.height / 2.1)) * 0.5).toInt())
                )
            )
        }
    }

    fun getGridColorsCount(): MutableMap<Int, Int> {
        return gridColorsCount!!
    }

    /*
        Returns the number of rows and columns of a game stage
     */
    private var initialStage: Int = 0
    private var rows: Int = 0
    private var columns: Int = 0
    private fun getRowsAndColumns(currentStage: Int): Pair<Int, Int> {
        initialStage = 2
        rows = 1
        columns = 1
        while (currentStage >= initialStage) {
            if (initialStage % 2 == 0) {
                rows += 1
            } else {
                columns += 1
            }
            initialStage += 1
        }
        return Pair(rows, columns)
    }

    /*
        Randomly assigns grid colors to each future cat button
     */
    var gridColorRowIndex: Int = 0
    var gridColorColumnIndex: Int = 0
    var randomGridColor: Int = 0
    var previousGridColumnColor: Int = 0
    var previousGridRowColor: Int = 0
    private fun buildGridColors() {
        //Initialize empty 2 x 2 array
        gridColors = Array(rowsAndColumns.first) { IntArray(rowsAndColumns.second) }
        gridColorRowIndex = 0
        // Iterate through the rows
        while (gridColorRowIndex < gridColors!!.size) {
            gridColorColumnIndex = 0
            // Iterate through the columns
            while (gridColorColumnIndex < gridColors!![0].size) {
                randomGridColor = ColorOptions.selectionColors!!.random()
                if (gridColorRowIndex > 0) {
                    previousGridColumnColor = gridColors!![gridColorRowIndex - 1][gridColorColumnIndex]
                    // Skip assigned color if it is repeated in the previous row
                    if (previousGridColumnColor == randomGridColor) {
                        gridColorRowIndex -= 1
                    }
                }
                if (gridColorColumnIndex > 0) {
                    // Randomly skip assigned color if it is repeated in the previous column
                    previousGridRowColor = gridColors!![gridColorRowIndex][gridColorColumnIndex - 1]
                    if (previousGridRowColor == randomGridColor && (0..1).random() == 0) {
                        gridColorColumnIndex -= 1
                    }
                }
                gridColors!![gridColorRowIndex][gridColorColumnIndex] = randomGridColor
                gridColorColumnIndex += 1
            }
            gridColorRowIndex += 1
        }
    }

    private var gridButtonRowGap: Float = 0.0f
    private var gridButtonColumnGap: Float = 0.0f
    private var gridButtonHeight: Float = 0.0f
    private var gridButtonWidth: Float = 0.0f
    private var gridButtonX: Float = 0.0f
    private var gridButtonY: Float = 0.0f
    private var catButton: CatButton? = null
    private var viewToCatButton:MutableMap<View, CatButton>? = null
    private fun buildGridButtons() {
        gridButtonRowGap = originalParams!!.height * 0.1f / (rowsAndColumns.first + 1.0f)
        gridButtonColumnGap = originalParams!!.width * 0.1f / (rowsAndColumns.second + 1.0f)
        // Sizes
        gridButtonHeight = originalParams!!.width * 0.9f / rowsAndColumns.first.toFloat()
        gridButtonWidth = originalParams!!.height * 0.9f / rowsAndColumns.second.toFloat()
        // Locations
        gridButtonX = 0.0f
        gridButtonY = 0.0f
        // Build the cat buttons
        viewToCatButton = mutableMapOf()
        // Iterate through the rows
        for (rowIndex in (0 until rowsAndColumns.first)) {
            gridButtonY += gridButtonRowGap
            gridButtonX = 0.0f
            // Iterate through the columns
            for (columnIndex in (0 until rowsAndColumns.second)) {
                gridButtonX += gridButtonColumnGap
                catButton = catButtons!!.buildCatButton(imageButton = ImageButton(boardGameContext!!),
                    parentLayout = MainActivity.rootLayout!!, params = LayoutParams(gridButtonWidth.toInt(),
                        gridButtonHeight.toInt(), (gridButtonX + originalParams!!.x).toInt(),
                        (gridButtonY + originalParams!!.y).toInt()
                    ),
                    backgroundColor = gridColors!![rowIndex][columnIndex]
                )
                catButton!!.rowIndex = rowIndex
                catButton!!.columnIndex = columnIndex
                viewToCatButton!![catButton!!.getTouchView()] = catButton!!
                catButton!!.getTouchView().setOnClickListener {
                    // The user has selected a color option
                    if (MainActivity.colorOptions!!.getSelectedColor() != Color.LTGRAY) {
                        val catButton = viewToCatButton!![it]
                        // The button is found and colors match
                        if (!catButton!!.isPodded) {
                            if (MainActivity.colorOptions!!.getSelectedColor() == catButton.getOriginalBackgroundColor()) {
                                // Tranform the cat button as a matched cat button
                                catButton.transitionColor(catButton.getOriginalBackgroundColor())
                                gridColorsCount!![catButton.getOriginalBackgroundColor()] =
                                    gridColorsCount!![catButton.getOriginalBackgroundColor()]!!.minus(1)
                                MainActivity.colorOptions!!.buildColorOptionButtons(setup = false)
                                catButton.pod()
                                MainActivity.staticSelf!!.catsSavedCount += 1
                                MainActivity.staticSelf!!.flashCatsSavedLabel()
                                verifyRemainingCatsArePodded(catButton = catButton)
                            } else {
                                attackCatButton(catButton = catButton)
                            }
                        } else {
                            AudioController.kittenMeow()
                        }
                    } else {
                        if (catButton!!.isAlive) {
                            AudioController.kittenMeow()
                        }
                    }
                }
                // Have the cat buttons appear from no where
                catButton!!.shrunk()
                catButton!!.grow()
                catButton!!.fade(true, false, 0.5f, 0.125f)
                gridButtonX += gridButtonWidth
            }
            gridButtonY += gridButtonHeight
        }
        SettingsMenu.moreCatsButton!!.bringToFront()
    }

    /*
        Kills a cat button and disposes it from the grid
     */
    fun attackCatButton(catButton: CatButton) {
        // Reduce next attack delay
        MainActivity.attackMeter!!.updateDuration(-0.75f)
        // Remove a life
        MainActivity.myLivesMeter!!.dropLivesLeftHeart()
        MainActivity.mpController!!.setMyLivesLeft(MainActivity.myLivesMeter!!.getLivesLeftCount().toLong())
        // Reset hairball
        MainActivity.enemies!!.translateToCatAndBack(catButton)
        // Cat button flies away
        catButton.disperseRadially()
        // Update the selection grid colors
        gridColorsCount!![catButton.getOriginalBackgroundColor()] =
            gridColorsCount!![catButton.getOriginalBackgroundColor()]!!.minus(1)
        MainActivity.colorOptions!!.buildColorOptionButtons(setup = false)
        displaceArea(catButton = catButton)
        verifyRemainingCatsArePodded(catButton = catButton)
    }

    /*
        If all cat buttons survive promote the player to the next round
        Demote the player if the player has run out of lives
        Maintain the player in the current round if the player has saved the remaining cats
     */
    private fun verifyRemainingCatsArePodded(catButton: CatButton) {
        MainActivity.attackMeter!!.sendEnemyToStart()
        if (catButtons!!.allSurvived()) {
            promote(catButton = catButton)
        } else if (MainActivity.myLivesMeter!!.getLivesLeftCount() == 0) {
            MainActivity.staticSelf!!.flashCatsSavedLabel()
            MainActivity.staticSelf!!.catsSavedCount = 0
            gameOver()
        } else if (catButtons!!.areAliveAndPodded()) {
            maintain()
        }
    }

    /*
        The player won the multiplayer match
        Display the succes screen
     */
    fun wonMultiPlayer() {
        MainActivity.settingsButton!!.forceSettingsMenuExpansion()
        MPController.isPlaying = false
        MainActivity.mpController!!.disconnect()
        // SET OPPOSITION LIVES METER COUNT
        MainActivity.opponentLivesMeter!!.translate(false)
        MainActivity.attackMeter!!.sendEnemyToStart()
        AttackMeter.didNotInvokeRelease = true
        // PROMOTE ALL THE CATS
        LeaderBoard.examineScore(GameResults.savedCatButtonsCount.toLong())
        // SHOW GLOVE POINTER
        val watchAdButtonParams: LayoutParams =
            MainActivity.gameResults!!.getWatchAdButton().getOriginalParams()
        MainActivity.glovePointer!!.translate(
            watchAdButtonParams.x - (watchAdButtonParams.width * 0.1125).toInt(),
            watchAdButtonParams.y - (watchAdButtonParams.height * 0.05).toInt()
        )
        MainActivity.glovePointer!!.fadeIn()
        // HIDE LIVES METER
        MainActivity.myLivesMeter!!.hideCurrentHeartButton()
        MainActivity.opponentLivesMeter!!.hideCurrentHeartButton()
        // SHOW SINGLE AND TWO PLAYER BUTTON
        singlePlayerButton!!.backgroundColor = null
        singlePlayerButton!!.targetBackgroundColor = null
        singlePlayerButton!!.setStyle()
        singlePlayerButton!!.shrunk()
        singlePlayerButton!!.grow(1f, 0.125f)
        singlePlayerButton!!.fadeIn()
        twoPlayerButton!!.backgroundColor = null
        twoPlayerButton!!.targetBackgroundColor = null
        twoPlayerButton!!.setStyle()
        twoPlayerButton!!.shrunk()
        twoPlayerButton!!.grow(1f, 0.125f)
        twoPlayerButton!!.fadeIn()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                MainActivity.staticSelf!!.runOnUiThread {
                    singlePlayerButton!!.getParentLayout().addView(singlePlayerButton!!.getThis())
                    singlePlayerButton!!.getParentLayout().addView(twoPlayerButton!!.getThis())
                    singlePlayerButton!!.getThis().bringToFront()
                    twoPlayerButton!!.getThis().bringToFront()
                }
            }
        }, 250)
        // SET ALL CAT BUTTONS AS PODDED
        catButtons!!.saveAllCats()
        GameResults.savedCatButtonsCount = catButtons!!.aliveCount()
        // SHOW WINNING VIEW
        MainActivity.successResults!!.fadeIn()
        // SHRINK COLOR OPTION BUTTONS
        MainActivity.colorOptions!!.resetSelectedColor()
        gridColors = null
        MainActivity.colorOptions!!.shrinkAllColorOptionButtons()
        MainActivity.colorOptions!!.loadSelectionToSelectedButtons()
        // Build the game and hide the cat button
        currentStage = 1
        buildGame()
        catButtons!!.getCurrentCatButtons()[0].fade(
            false, true,
            0f, 0f
        )
        // Submit mouse coin count
        MainActivity.mouseCoinView!!.submitMouseCoinCount()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                MainActivity.staticSelf!!.runOnUiThread {
                    MainActivity.mpController!!.closeRoom()
                }
            }
        }, 500)
    }

    /*
        The player has lost, show the game over screen
     */
    private fun gameOver() {
        MainActivity.settingsButton!!.forceSettingsMenuExpansion()
        MPController.isPlaying = false
        MainActivity.mpController!!.disconnect()
        MainActivity.opponentLivesMeter!!.translate(false)
        // Examine the score
        LeaderBoard.examineScore(GameResults.savedCatButtonsCount.toLong())
        // Glove pointer on watch ad button
        val watchAdButtonParams: LayoutParams =
            MainActivity.gameResults!!.getWatchAdButton().getOriginalParams()
        MainActivity.glovePointer!!.translate(
            watchAdButtonParams.x - (watchAdButtonParams.width * 0.1125).toInt(),
            watchAdButtonParams.y - (watchAdButtonParams.height * 0.05).toInt()
        )
        MainActivity.glovePointer!!.fadeIn()
        // Hide my lives meter heart button
        MainActivity.myLivesMeter!!.hideCurrentHeartButton()
        MainActivity.opponentLivesMeter!!.hideCurrentHeartButton()
        // Show single and multi player button
        singlePlayerButton!!.backgroundColor = null
        singlePlayerButton!!.targetBackgroundColor = null
        singlePlayerButton!!.setStyle()
        singlePlayerButton!!.shrunk()
        singlePlayerButton!!.grow(1f, 0.125f)
        singlePlayerButton!!.fadeIn()
        twoPlayerButton!!.backgroundColor = null
        twoPlayerButton!!.targetBackgroundColor = null
        twoPlayerButton!!.setStyle()
        twoPlayerButton!!.shrunk()
        twoPlayerButton!!.grow(1f, 0.125f)
        twoPlayerButton!!.fadeIn()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                MainActivity.staticSelf!!.runOnUiThread {
                    singlePlayerButton!!.getParentLayout().addView(singlePlayerButton!!.getThis())
                    singlePlayerButton!!.getParentLayout().addView(twoPlayerButton!!.getThis())
                    singlePlayerButton!!.getThis().bringToFront()
                    twoPlayerButton!!.getThis().bringToFront()
                }
            }
        }, 250)
        catButtons!!.setAllCatButtonsDead()
        GameResults.deadCatButtonsCount += catButtons!!.deadCount()
        MainActivity.gameResults!!.fadeIn()
        MainActivity.glovePointer!!.getThis().bringToFront()
        AttackMeter.didNotInvokeRelease = true
        MainActivity.colorOptions!!.resetSelectedColor()
        gridColors = null
        MainActivity.colorOptions!!.shrinkAllColorOptionButtons()
        MainActivity.colorOptions!!.loadSelectionToSelectedButtons()
        AudioController.mozartSonata(play = false, startOver = false)
        AudioController.chopinPrelude(play = true, startOver = true)
        // Build the game and hide the cat button
        currentStage = 1
        buildGame()
        catButtons!!.getCurrentCatButtons()[0].fade(
            false, true,
            0f, 0f
        )
        // Submit mouse coin count
        MainActivity.mouseCoinView!!.submitMouseCoinCount()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                MainActivity.staticSelf!!.runOnUiThread {
                    try {
                        MainActivity.mpController!!.closeRoom()
                    } catch (e: Exception) {
                        MPController.displayFailureReason()
                    }
                }
            }
        }, 500)
    }

    private fun unveilHeaven() {
        AudioController.heaven()
        MainActivity.successGradientView!!.alpha = 1f
    }

    /*
        Fills in the gaps where cat buttons once lived
     */
    private var rowOfAliveCats: MutableList<CatButton>? = null
    private fun displaceArea(catButton: CatButton) {
        rowOfAliveCats = catButtons!!.getRowOfAliveCats(rowIndex = catButton.rowIndex)
        // Row is still occupied
        if (rowOfAliveCats!!.size > 0) {
            disperseRow(aliveCats = rowOfAliveCats!!)
        } else {
            disperseColumns()
        }
    }

    /*
        Fills in the rows where cat buttons once lived
        Just share the height of the game board equally (vertical)
     */
    private var x: Float = 0f
    private var y: Float = 0f
    private var columnGap: Float = 0f
    private var buttonWidth: Float = 0f
    private fun disperseRow(aliveCats: MutableList<CatButton>) {
        x = 0f
        y = aliveCats[0].getOriginalParams().y.toFloat()
        columnGap = originalParams!!.width * 0.1f / (aliveCats.size + 1).toFloat()
        buttonWidth = originalParams!!.height * 0.9f / (aliveCats.size).toFloat()
        for (aliveCat in aliveCats) {
            x += columnGap
            aliveCat.transformTo(LayoutParams(buttonWidth.toInt(),
                aliveCat.getOriginalParams().height, (originalParams!!.x + x).toInt(), y.toInt()))
            x += buttonWidth
        }
    }

    /*
        Fill in the columns where the cat buttons once lived
        Share the width of the game board equally (horizontal)
     */
    private var rowIndexCatAliveCount: MutableMap<Int, Int>? = null
    private var rowsLeftCount: Int = 0
    private var maxCatsInRowCount: Int = 0
    private fun disperseColumns() {
        rowIndexCatAliveCount = catButtons!!.getRowIndexAliveCatCount()
        // No rows, cancel the operation
        rowsLeftCount = rowIndexCatAliveCount!!.size
        if (rowsLeftCount == 0) {
            return
        }
        // Max count of cats in row
        maxCatsInRowCount = rowIndexCatAliveCount!!.maxBy { it.value }!!.value
        y = originalParams!!.y.toFloat()
        gridButtonRowGap = (originalParams!!.height * 0.1f) / (rowsLeftCount.toFloat() + 1f)
        gridButtonHeight = (originalParams!!.width * 0.9f) / rowsLeftCount.toFloat()

        fun resetCatButtonsPosition(rowIndex: Int) {
            y += gridButtonRowGap
            for (catButton in catButtons!!.getRowOfAliveCats(rowIndex = rowIndex)) {
                catButton.transformTo(LayoutParams(catButton.getOriginalParams().width,
                        gridButtonHeight.toInt(), catButton.getOriginalParams().x, y.toInt()))
            }
            y += gridButtonHeight
        }
        // Share the space or update the dimensions to share the space
        if (maxCatsInRowCount <= rowsLeftCount) {
            for (rowIndex in rowIndexCatAliveCount!!.keys.sorted()) {
                resetCatButtonsPosition(rowIndex)
            }
        } else {
            gridButtonRowGap = (originalParams!!.height * 0.1f) / (rowsLeftCount.toFloat() + 1f)
            gridButtonHeight = (originalParams!!.width * 0.9f) / rowsLeftCount.toFloat()
            for (rowIndex in rowIndexCatAliveCount!!.keys.sorted()) {
                resetCatButtonsPosition(rowIndex)
            }
        }
    }

    /*
        Keep the player at an obtainable grid level
        if the user doesn't loose and fails to save all the cats
     */
    private var countOfAliveCatButtons: Int = 0
    private var newRound: Int = 0
    private var product: Int = 0
    private var newRowsAndColumns: Pair<Int, Int>? = null
    private fun maintain() {
        // Update attack meter, and Game Stats
        MainActivity.attackMeter!!.updateDuration(0.025f)
        AttackMeter.didNotInvokeRelease = true
        unveilHeaven()
        MainActivity.colorOptions!!.resetSelectedColor()
        countOfAliveCatButtons = catButtons!!.aliveCount()
        GameResults.savedCatButtonsCount += catButtons!!.aliveCount()
        GameResults.deadCatButtonsCount += catButtons!!.deadCount()
        // Select a grid complexity that is adequite for the player
        newRound = 1
        while (true) {
            newRowsAndColumns = getRowsAndColumns(newRound)
            product = newRowsAndColumns!!.first * newRowsAndColumns!!.second
            if (countOfAliveCatButtons < product) {
                currentStage = newRound - 1
                break
            }
            newRound += 1
        }
        reset(true)
        // Clear the color selection buttons
        MainActivity.colorOptions!!.shrinkAllColorOptionButtons()
        MainActivity.colorOptions!!.loadSelectionToSelectedButtons()
        // Build the next round
        Timer().schedule(object : TimerTask() {
            override fun run() {
                MainActivity.staticSelf!!.runOnUiThread {
                    buildGame()
                    startGame()
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            MainActivity.staticSelf!!.runOnUiThread {
                                MainActivity.successGradientView!!.alpha = 0f
                            }
                        }
                    }, 1125)
                }
            }
        }, 1250)
    }

    /*
        Advances the player to the next stage (with an extra row or column)
     */
    private fun promote(catButton: CatButton) {
        // Update attack meter, and Game Stats
        MainActivity.glovePointer!!.hide()
        GameResults.savedCatButtonsCount += catButtons!!.aliveCount()
        MainActivity.myLivesMeter!!.incrementLivesLeftCount(
            catButton = catButton, forOpponent = false)
        MainActivity.attackMeter!!.updateDuration(0.075f)
        AttackMeter.didNotInvokeRelease = true
        unveilHeaven()
        MainActivity.colorOptions!!.resetSelectedColor()
        reset(true)
        // Clear the color selection buttons
        MainActivity.colorOptions!!.shrinkAllColorOptionButtons()
        MainActivity.colorOptions!!.loadSelectionToSelectedButtons()
        // Build the next round
        Timer().schedule(object : TimerTask() {
            override fun run() {
                MainActivity.staticSelf!!.runOnUiThread {
                    currentStage += 1
                    buildGame()
                    startGame()
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            MainActivity.staticSelf!!.runOnUiThread {
                                MainActivity.successGradientView!!.alpha = 0f
                            }
                        }
                    }, 1125)
                }
            }
        }, 1250)
    }

    /*
        Translates all the cats to heaven and/or
        removes them from the list they were stored
     */
    private fun reset(allSurvived: Boolean) {
        if (allSurvived) {
            catButtons!!.disperseVertically()
        }
        gridColors = null
        catButtons!!.removeAll()
    }

    fun getCatButtons(): CatButtons {
        return this.catButtons!!
    }

    /*
        Updates the grid colors used for each match
     */
    private var recordedColor: Int = 0
    private fun recordGridColorsUsed() {
        // Create a list (storage) to save the colors used
        if (gridColorsCount == null) {
            gridColorsCount = mutableMapOf()
        } else {
            gridColorsCount!!.clear()
        }
        // Iterate through each cat button to record colors used up
        for (catButton in catButtons!!.getCurrentCatButtons()) {
            recordedColor = catButton.getOriginalBackgroundColor()
            if (gridColorsCount!![recordedColor] == null) {
                gridColorsCount!![recordedColor] = 1
            } else {
                gridColorsCount!![recordedColor] = gridColorsCount!![recordedColor]!! + 1
            }
        }
    }

    /*
        Creates the single player button that starts the game
     */
    fun setupSinglePlayerButton() {
        singlePlayerButton = CButton(button = Button(boardGameContext), parentLayout = boardGameLayout!!,
            params = LayoutParams((originalParams!!.width * 0.425).toInt(), (MainActivity.dUnitHeight
                    * 1.5 * 0.8).toInt(), (originalParams!!.x + (originalParams!!.width * 0.05)).toInt(),
                (originalParams!!.y + originalParams!!.height + (-MainActivity.dUnitHeight * 1.5 * 0.475)
                        + (originalParams!!.height * 0.1)).toInt()))
        singlePlayerButton!!.setCornerRadiusAndBorderWidth(
                (singlePlayerButton!!.getOriginalParams().height / 5.0).toInt(), ((kotlin.math.sqrt(
                singlePlayerButton!!.getOriginalParams().width * 0.01) * 10.0) * 0.65).toInt())
        singlePlayerButton!!.setTextSize((singlePlayerButton!!.getOriginalParams().height * 0.175).toFloat())
        singlePlayerButton!!.setText("Single Player", false)
        // When the button is clicked, start the game
        singlePlayerButton!!.getThis().setOnClickListener {
            if (!singlePlayerButton!!.growWidthAndChangeColorIsRunning) {
                resetTopViews()
                decreaseMouseCoinsGiven()
                resetGlovePointer()
                playStartingAudio()
                singlePlayerButton!!.targetBackgroundColor = gridColors!![0][0]
                singlePlayerButton!!.growWidth((originalParams!!.width * 0.9).toFloat())
                twoPlayerButton!!.shrink()
                // Just show the game board, settings menu, ad, and selection colors
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        MainActivity.staticSelf!!.runOnUiThread {
                            MainActivity.successGradientView!!.alpha = 0f
                            MainActivity.glovePointer!!.fadeIn()
                            MainActivity.gameResults!!.fadeOut()
                            MainActivity.successResults!!.fadeOut()
                            startGame()
                        }
                    }
                }, 1250)
            }
        }
        singlePlayerButton!!.shrunk()
        singlePlayerButton!!.grow(1f, 0.125f)
        singlePlayerButton!!.fadeIn()
    }

    private fun playStartingAudio() {
        if (!AudioController.isMozartSonataPlaying()) {
            AudioController.mozartSonata(play = true, startOver = true)
            AudioController.chopinPrelude(play = false, startOver = false)
        }
    }

    private fun resetGlovePointer() {
        val x: Int = MainActivity.colorOptions!!.getOriginalParams().x -
                (MainActivity.dUnitHeight * 0.15).toInt()
        val y: Int = MainActivity.colorOptions!!.getOriginalParams().y +
                (MainActivity.dUnitHeight * 0.175).toInt()
        MainActivity.glovePointer!!.translate(x, y)
    }

    private fun decreaseMouseCoinsGiven() {
        // Decrease mouse coins given
        if (GameResults.watchAdButtonWasSelected) {
            GameResults.watchAdButtonWasSelected = false
        }
        if (GameResults.mouseCoinsEarned > 5 && !GameResults.watchAdButtonWasSelected) {
            GameResults.mouseCoinsEarned -= 1
        }
    }

    private fun resetTopViews() {
        MainActivity.settingsButton!!.forceSettingsMenuContraction()
        MainActivity.attackMeter!!.resetDisplacementDuration()
        MainActivity.myLivesMeter!!.resetLivesLeftCount()
        MainActivity.myLivesMeter!!.showCurrentHeartButton()
        MainActivity.opponentLivesMeter!!.resetLivesLeftCount()
        MainActivity.opponentLivesMeter!!.showCurrentHeartButton()
    }

    fun startGame() {
        catButtons!!.getCurrentCatButtons()[0].fade(true, false, 1f, 0.125f)
        MainActivity.colorOptions!!.buildColorOptionButtons(setup = true)
        MainActivity.attackMeter!!.invokeRelease()
    }

    /*
        Create the multiplayer button that start the 2 player match
     */
    fun setupTwoPlayerButton() {
        twoPlayerButton = CButton(button = Button(boardGameContext), parentLayout = boardGameLayout!!,
            params = LayoutParams((originalParams!!.width * 0.425).toInt(),
                    (MainActivity.dUnitHeight * 1.5 * 0.8).toInt(),
                (originalParams!!.x + (originalParams!!.width * 0.525)).toInt(),
                    (originalParams!!.y + (-MainActivity.dUnitHeight * 1.5 * 0.475) +
                        originalParams!!.height + (originalParams!!.height * 0.1)).toInt()))
        twoPlayerButton!!.setCornerRadiusAndBorderWidth(
            (twoPlayerButton!!.getOriginalParams().height / 5.0).toInt(), ((kotlin.math.sqrt(
                twoPlayerButton!!.getOriginalParams().width * 0.01
            ) * 10.0) * 0.65).toInt())
        twoPlayerButton!!.setTextSize((twoPlayerButton!!.getOriginalParams().height * 0.175).toFloat())
        twoPlayerButton!!.isTwoPlayerButton = true
        twoPlayerButton!!.shrinkType = ShrinkType.right
        twoPlayerButton!!.setText("Multi Player", false)
        twoPlayerButton!!.shrunk()
        twoPlayerButton!!.grow(1f, 0.125f)
        twoPlayerButton!!.fade(true, false, 0.5f, 0.125f)
        // When the button is selected, display the start searching animation
        twoPlayerButton!!.getThis().setOnClickListener {
            if (MainActivity.mpController!!.didGetPlayerID()) {
                searchMG!!.startSearchingAnimation()
                MainActivity.mpController!!.startSearching()
            } else {
                MPController.displayFailureReason()
            }
        }
    }

    /*
        Display the lives left meter for the opponent as well
     */
    fun startTwoPlayerMatch() {
        if (!twoPlayerButton!!.growWidthAndChangeColorIsRunning) {
            resetTopViews()
            decreaseMouseCoinsGiven()
            resetGlovePointer()
            playStartingAudio()
            MainActivity.opponentLivesMeter!!.translate(true)
            twoPlayerButton!!.targetBackgroundColor = gridColors!![0][0]
            twoPlayerButton!!.growWidth((originalParams!!.width * 0.9).toFloat())
            singlePlayerButton!!.shrink()
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    MainActivity.staticSelf!!.runOnUiThread {
                        MainActivity.successGradientView!!.alpha = 0f
                        MainActivity.glovePointer!!.fadeIn()
                        MainActivity.gameResults!!.fadeOut()
                        MainActivity.successResults!!.fadeOut()
                        startGame()
                    }
                }
            }, 1250)
        }
    }

    /*
        Returns count of grid colors that exist
        and are not 0
     */
    private var nonZeroCount: Int = 0
    fun nonZeroGridColorsCount(): Int {
        nonZeroCount = 0
        for ((_, count) in gridColorsCount!!) {
            if (count > 0) {
                nonZeroCount += 1
            }
        }
        return nonZeroCount
    }

    fun setButtonsBackgroundColorTransparent() {
        catButtons!!.setBackgroundTransparent()
    }

    /*
        Sets the style of the board game based on
        the current theme of the operating system
     */
    fun setStyle() {
        for (catButton in getCatButtons().getCurrentCatButtons()) {
            catButton.setStyle()
        }
        if (singlePlayerButton != null) {
            singlePlayerButton!!.setStyle()
        }
        if (twoPlayerButton != null) {
            twoPlayerButton!!.setStyle()
        }
        if (searchMG != null) {
            searchMG!!.setStyle()
        }
    }
}