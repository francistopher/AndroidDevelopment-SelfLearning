package com.example.savethecat_colormatching

import Reachability
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.*
import android.widget.AbsoluteLayout.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import com.example.savethecat_colormatching.Characters.Enemies
import com.example.savethecat_colormatching.Controllers.ARType
import com.example.savethecat_colormatching.Controllers.AspectRatio
import com.example.savethecat_colormatching.Controllers.AspectRatio.Companion.setupAspectRatio
import com.example.savethecat_colormatching.Controllers.AudioController
import com.example.savethecat_colormatching.Controllers.CenterController
import com.example.savethecat_colormatching.ParticularViews.*
import com.google.android.gms.ads.*
import java.util.*


class MainActivity : AppCompatActivity(), Reachability.ConnectivityReceiverListener {

    companion object {
        var staticSelf: MainActivity? = null
        var rootView: View? = null
        var isThemeDark:Boolean = true
        var isInternetReachable:Boolean = false
        var aspectRatio: ARType? = null
        var decorView:View? = null
        // Display properties
        var dWidth:Double = 0.0
        var dHeight:Double = 0.0
        var dUnitWidth:Double = 0.0
        var dUnitHeight:Double = 0.0
        var dNavigationBarHeight:Double = 0.0
        // Custom Font
        var rootLayout:AbsoluteLayout? = null
        // Absolute Layout Params
        var params:LayoutParams? = null
        var successGradientView:View? = null
        var enemies:Enemies? = null
        // Board Game
        var boardGame:BoardGame? = null
        var colorOptions:ColorOptions? = null
        // Settings button
        var settingsButton:SettingsButton? = null
        // Attack meter
        var attackMeter:AttackMeter? = null
        // Lives meters
        var myLivesMeter:LivesMeter? = null
        var opponentLivesMeter:LivesMeter? = null
        // Game results
        var gameResults:GameResults? = null
        // Glove pointer
        var glovePointer: GlovePointer? = null
        // Mouse coin view
        var mouseCoinView:MCView? = null
        // Interstitial ad
        var mInterstitialAd: InterstitialAd? = null
    }

    var introAnimation:IntroView? = null

    private fun setCurrentTheme() {
        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_NO -> isThemeDark = false
            Configuration.UI_MODE_NIGHT_YES -> isThemeDark = true
            Configuration.UI_MODE_NIGHT_UNDEFINED -> isThemeDark = false
        }
        updateTheme()
    }

    private fun updateTheme() {
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (isThemeDark) {
            rootView!!.setBackgroundColor(Color.BLACK)
            window.statusBarColor = this.resources.getColor(R.color.Black)
        } else {
            rootView!!.setBackgroundColor(Color.WHITE)
            window.statusBarColor = this.resources.getColor(R.color.White)
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    override fun onResume() {
        super.onResume()
        Reachability.reachabilityListener = this
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (isConnected) {
            isInternetReachable = true
            print("Internet is reachable")
        } else {
            isInternetReachable = false
            print("Internet is not reachable")
        }
    }

    private fun hideSystemBars(): Int {
        return View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_IMMERSIVE
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            decorView!!.systemUiVisibility = hideSystemBars()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val testDeviceIds = Arrays.asList("33BE2250B43518CCDA7DE426D04EE231")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupDecorView()
        rootView = window.decorView.rootView
        rootLayout = AbsoluteLayout(this)
        staticSelf = this
        setupReachability()
        setupAspectRatio()
        setCurrentTheme()
        setupSaveTheCat()
        setupGamePlayAuthentication()
        setContentView(rootLayout!!)
    }

    private fun setupGamePlayAuthentication(){
        presentSaveTheCat()
    }

    private fun presentSaveTheCat() {
        introAnimation!!.start()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                staticSelf!!.runOnUiThread {
                    introAnimation!!.fadeOut(2.0f)
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            staticSelf!!.runOnUiThread {
                                enemies!!.fadeIn()
                                settingsButton!!.fadeIn()
                                attackMeter!!.fadeIn()
                                boardGame!!.setupSinglePlayerButton()
                                boardGame!!.setupTwoPlayerButton()
                                boardGame!!.buildGame()
                                mouseCoinView!!.fadeIn()
                                myLivesMeter!!.fadeIn()
                                opponentLivesMeter!!.fadeIn()
                                glovePointer!!.sway()
                                enemies!!.sway()
                                adView!!.alpha = 1f
                            }
                        }
                    }, 2000)
                }
            }
        }, 2000)
    }

    private fun setupSaveTheCat() {
        setupSounds()
        setSuccessGradientViewAndLayer()
        setupEnemies()
        setupIntroAnimation()
        setupAdvertisement()
        setupBoardGame()
        setupColorOptions()
        setupLivesMeters()
        setupSettingsButton()
        setupAttackMeter()
        setupMouseCoinView()
        setupGameResults()
        setupGlovePointer()
        AudioController.mozartSonata(play = true, startOver = false)
    }

    private fun setupDecorView() {
        decorView = window.decorView
        decorView!!.setOnSystemUiVisibilityChangeListener {
            decorView!!.systemUiVisibility = hideSystemBars()
        }
    }

    private fun setupReachability() {
        registerReceiver(Reachability(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    private fun setupSounds() {
        AudioController.setupHeaven(this)
        AudioController.setupGearSpinning(this)
        AudioController.setupAnimeWowPlayer(this)
        AudioController.setupCoinEarnedPlayers(this)
        AudioController.setupKittenMeowPlayer(this)
        AudioController.setupKittenDiePlayer(this)
        AudioController.setupChopinPrelude(this)
        AudioController.setupMozartSonata(this)
    }

    private fun setupIntroAnimation() {
        val width:Double = dUnitWidth * 9.0
        introAnimation = IntroView(imageView = ImageView(this), parentLayout = rootLayout!!,
            params = LayoutParams(width.toInt(), width.toInt(), 0, 0))
        introAnimation!!.loadTextImages(lightTextImageR = R.drawable.darkintrotext, darkTextImageR = R.drawable.lightintrotext,
            lightCatImageR = R.drawable.darkcat, darkCatImageR = R.drawable.lightcat)
        CenterController.center(introAnimation!!.getTextImage(), introAnimation!!.getTextParams(),
            params!!)
        CenterController.center(introAnimation!!.getCatImage(), introAnimation!!.getCatParams(),
            params!!)
    }

    private fun setSuccessGradientViewAndLayer() {
        successGradientView = View(rootView!!.context)
        successGradientView!!.layoutParams = LayoutParams(dWidth.toInt(), (dHeight * 0.15).toInt(), 0, 0)
        successGradientView!!.setBackgroundColor(Color.TRANSPARENT)
        rootLayout!!.addView(successGradientView)
        successGradientView!!.setBackgroundDrawable( GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor("#fcd340"), Color.TRANSPARENT)))
        successGradientView!!.alpha = 0.0f
    }

    private fun setupEnemies() {
        enemies = Enemies()
    }

    private fun setupAdvertisement() {
        MobileAds.initialize(this) {}
        setupBannerAds()
        setupInterstitialAds()
    }

    private fun setupInterstitialAds() {
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd!!.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        mInterstitialAd!!.loadAd(AdRequest.Builder().build())
        mInterstitialAd!!.adListener = object: AdListener() {
            override fun onAdClosed() {
                gameResults!!.hideWatchAdButtonChildren()
                glovePointer!!.getThis().alpha = 0f
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        staticSelf!!.runOnUiThread {
                            gameResults!!.giveMouseCoins()
                            GameResults.mouseCoinsEarned += 1
                            GameResults.watchAdButtonWasSelected = true
                        }
                    }
                }, 500)
            }
        }

    }

    private var adView:AdView? = null
    private var adRequest:AdRequest? = null
    private fun setupBannerAds() {
        fun getAdaptiveBannerAdSize():AdSize {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            val density = outMetrics.density
            var adWidthPixels = rootLayout!!.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }
            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationBannerAdSizeWithWidth(this, adWidth)
        }
        adView = AdView(this)
        adView!!.adUnitId = "ca-app-pub-3940256099942544/6300978111"
        adView!!.adSize = getAdaptiveBannerAdSize()
        adRequest = AdRequest.Builder().build()
        adView!!.loadAd(adRequest)
        rootLayout!!.addView(adView)
        adView!!.translationY = ((dUnitHeight * 15.5) - (getAdaptiveBannerAdSize().height)).toFloat()
        adView!!.alpha = 0f
    }

    private fun setupBoardGame() {
        val boardGameSideLength:Float = (dUnitHeight * 8).toFloat()
        boardGame = BoardGame(boardView = View(this), parentLayout = rootLayout!!,
            params = LayoutParams(boardGameSideLength.toInt(), boardGameSideLength.toInt(), 0,0))
        boardGame!!.getThis().setBackgroundColor(Color.TRANSPARENT)
        CenterController.centerView(childView = boardGame!!.getThis(), childParams =
        boardGame!!.getOriginalParams(), parentParams = LayoutParams(dWidth.toInt(),
            (dHeight).toInt(), 0, 0))
        boardGame!!.setOriginalParams(boardGame!!.getThis().layoutParams as LayoutParams)
    }

    private fun setupColorOptions() {
        val boardGameSideLength:Float = (dUnitHeight * 8).toFloat()
        colorOptions = ColorOptions(view = View(this), parentLayout = rootLayout!!, params =
        LayoutParams(boardGameSideLength.toInt(), (dUnitHeight * 1.5).toInt(),
            boardGame!!.getOriginalParams().x, boardGame!!.getOriginalParams().y + boardGame!!.
            getOriginalParams().height))
        CenterController.centerViewHorizontally(colorOptions!!.getThis(), parentParams =
        LayoutParams(dWidth.toInt(), (dHeight).toInt(), 0, 0), childParams =
        colorOptions!!.getOriginalParams())
        colorOptions!!.setOriginalParams(colorOptions!!.getThis().layoutParams as LayoutParams)
        rootLayout!!.addView(ColorOptions.colorOptionsLayout!!)
    }

    private fun setupSettingsButton() {
        val sideLength:Float = (dHeight * ((1.0/300.0) + 0.08)).toFloat()
        settingsButton = SettingsButton(imageButton = ImageButton(this), parentLayout = rootLayout!!,
            params = LayoutParams(sideLength.toInt(), sideLength.toInt(), dUnitWidth.toInt(),
                dUnitHeight.toInt()))
    }

    private fun setupAttackMeter() {
        val height:Float = (dHeight * ((1.0/300.0) + 0.08)).toFloat()
        var width: Float = (dUnitWidth * 6.5).toFloat()
        var y:Float = (dUnitHeight).toFloat()
        var x: Float
        if (AspectRatio.dAspectRatio >= 2.09){
            x = ((dWidth - width) * 0.5).toFloat()
        } else if (AspectRatio.dAspectRatio >= 1.7) {
            x = ((dWidth - width) * 0.5).toFloat()
            x += dUnitWidth.toFloat()
            width += (dUnitWidth * 0.5).toFloat()
        } else {
            width *= 1.4f
            x = ((dWidth - width) * 0.5).toFloat()
            y = (settingsButton!!.getOriginalParams().y +
                    settingsButton!!.getOriginalParams().height).toFloat() +
                    settingsButton!!.borderWidth
        }
        val attackMeterParams = LayoutParams(width.toInt(), height.toInt(),
            x.toInt(), y.toInt())
        attackMeter = AttackMeter(meterView = View(this), parentLayout = rootLayout!!,
            params = attackMeterParams)
    }

    private fun setupMouseCoinView() {
        mouseCoinView = MCView(textView = TextView(rootView!!.context), parentLayout = rootLayout!!,
        params = attackMeter!!.getOriginalParams())
        mouseCoinView!!.setTextSize(mouseCoinView!!.getOriginalParams().height * 0.2f)
        mouseCoinView!!.displayCount()
    }

    private fun setupLivesMeters() {
        setupOpponentLivesMeter()
        setupMyLivesMeter()
    }

    private fun setupMyLivesMeter() {
        myLivesMeter = LivesMeter(meterView = View(this), parentLayout = rootLayout!!,
            params = opponentLivesMeter!!.getOriginalParams(), isOpponent = false)
    }

    private fun setupOpponentLivesMeter() {
        val height:Float = (dHeight * ((1.0/300.0) + 0.08)).toFloat()
        val x:Float = (dWidth - height - dUnitWidth).toFloat()
        opponentLivesMeter = LivesMeter(meterView = View(this), parentLayout = rootLayout!!,
        params = LayoutParams(height.toInt(), height.toInt(), x.toInt(), dUnitHeight.toInt()),
            isOpponent = true)
    }

    private fun setupGameResults() {
        gameResults = GameResults(resultsView = View(rootView!!.context), parentLayout = rootLayout!!,
            params = LayoutParams((dUnitHeight * 7).toInt(), (dUnitHeight * 7.75).toInt(), 0, 0))
        CenterController.centerView(gameResults!!.getThis(), childParams = gameResults!!.getOriginalParams(),
            parentParams = LayoutParams(dWidth.toInt(), (dHeight - dUnitHeight).toInt(), 0, 0))
        gameResults!!.setupOriginalParams(gameResults!!.getThis().layoutParams as LayoutParams)
        gameResults!!.setupContents()
    }

    private fun setupGlovePointer() {
        val sideLength:Int = (dUnitHeight * 1.5).toInt()
        glovePointer = GlovePointer(view = Button(rootView!!.context),
            parentLayout = rootLayout!!,
            params = LayoutParams(sideLength, sideLength, colorOptions!!.getOriginalParams().x -
                    (dUnitHeight * 0.15).toInt(), colorOptions!!.getOriginalParams().y +
                    (dUnitHeight * 0.175).toInt()))
    }
}
