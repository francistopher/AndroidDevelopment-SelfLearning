package com.example.savethecat_colormatching

import Reachability
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.savethecat_colormatching.Characters.Enemies
import com.example.savethecat_colormatching.Controllers.ARType
import com.example.savethecat_colormatching.Controllers.AudioController
import com.example.savethecat_colormatching.Controllers.CenterController
import com.example.savethecat_colormatching.ParticularViews.*
import com.google.android.gms.ads.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.drive.Drive
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
        var dStatusBarHeight:Double = 0.0
        // Custom Font
        var rootLayout:AbsoluteLayout? = null
        var successGradientView:View? = null
        var enemies:Enemies? = null
        // Save the cat essential views
        var boardGame:BoardGame? = null
        var colorOptions:ColorOptions? = null
        var settingsButton:SettingsButton? = null
        var attackMeter:AttackMeter? = null
        var myLivesMeter:LivesMeter? = null
        var opponentLivesMeter:LivesMeter? = null
        var gameResults:GameResults? = null
        var glovePointer: GlovePointer? = null
        var mouseCoinView:MCView? = null
        var mInterstitialAd: InterstitialAd? = null

        var dAspectRatio:Double = 0.0
        var params:LayoutParams? = null
        var gameNotification:GameNotification? = null

        var isGooglePlayGameServicesAvailable:Boolean = false
        private var isCatDismissed:Boolean = false
    }

    var introAnimation:IntroView? = null
    private var screenAspectRatio:Double = 0.0

    private fun setupAspectRatio() {
        setStatusBarHeight()
        setupScreenDimension()
        setScreenRatioType()
        setupUnitScreenDimension()
        params = LayoutParams(dWidth.toInt(), dHeight.toInt(), 0, 0)
    }

    private fun setScreenRatioType() {
        screenAspectRatio = (dHeight / dWidth)
        when {
            screenAspectRatio > 2.16 -> {
                dAspectRatio = 2.16
                aspectRatio = ARType.ar19point5by9
            }
            screenAspectRatio > 2.09 -> {
                dAspectRatio = 2.09
                aspectRatio = ARType.ar19by9
            }
            screenAspectRatio > 2.07 -> {
                dAspectRatio = 2.07
                aspectRatio = ARType.ar18point7by9
            }
            screenAspectRatio > 2.05 -> {
                dAspectRatio = 2.05
                aspectRatio = ARType.ar18point5by9
            }
            screenAspectRatio > 1.9 -> {
                dAspectRatio = 1.9
                aspectRatio = ARType.ar18by9
            }
            screenAspectRatio > 1.8 -> {
                dAspectRatio = 1.8
                aspectRatio = ARType.ar19by10
            }
            screenAspectRatio > 1.7 -> {
                dAspectRatio = 1.7
                aspectRatio = ARType.ar16by9
            }
            screenAspectRatio > 1.66 -> {
                dAspectRatio = 1.66
                aspectRatio = ARType.ar5by3
            }
            screenAspectRatio > 1.5 -> {
                dAspectRatio = 1.5
                aspectRatio = ARType.ar16by10
            }
            screenAspectRatio > 1.4 -> {
                dAspectRatio = 1.4
                aspectRatio = ARType.ar3by2
            }
            else -> {
                dAspectRatio = 1.3
                aspectRatio = ARType.ar4by3
            }
        }
    }

    private fun setStatusBarHeight() {
        var resourceId:Int = resources.getIdentifier("status_bar_height",
            "dimen", "android")
        if (resourceId > 0) {
            dStatusBarHeight = resources.getDimensionPixelSize(resourceId).toDouble()
        }
        resourceId = resources.getIdentifier("navigation_bar_height",
            "dimen", "android")
        if (resourceId > 0) {
            dNavigationBarHeight  += resources.getDimensionPixelSize(resourceId).toDouble()
        }
    }

    var displayMetrics:DisplayMetrics? = null
    private fun setupScreenDimension() {
        displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics!!)
        dWidth = displayMetrics!!.widthPixels.toDouble()
        dHeight = displayMetrics!!.heightPixels.toDouble() + dNavigationBarHeight + dStatusBarHeight
    }
    private fun setupUnitScreenDimension() {
        dUnitWidth = dWidth / 18.0
        dUnitHeight = dHeight / 18.0
    }

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
        if (isConnected) {
            isInternetReachable = true
            gameNotification!!.displayYesInternet()
        } else {
            isInternetReachable = false
            gameNotification!!.displayNoInternet()
        }
    }

    override fun onResume() {
        super.onResume()
        Reachability.reachabilityListener = this
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
        setupAspectRatio()
        setupDecorView()
        rootView = window.decorView.rootView
        rootView!!.layoutParams = params
        rootLayout = AbsoluteLayout(this)
        rootLayout!!.layoutParams = params
        staticSelf = this
        setupGameNotificationLabel()
        setupReachability()
        setCurrentTheme()
        setupSaveTheCat()
        setupGamePlayAuthentication()
        setContentView(rootLayout!!)
        startCatPresentation()
    }

    var googleAccount:GoogleSignInAccount? = null
    var signInClient:GoogleSignInClient? = null
    private val RC_SIGN_IN = 9001
    private fun setupGamePlayAuthentication(){
        val signInOptions:GoogleSignInOptions = GoogleSignInOptions.
        Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).
        requestScopes(Drive.SCOPE_APPFOLDER).build()
        signInClient = GoogleSignIn.getClient(this, signInOptions)
        googleAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (googleAccount != null && GoogleSignIn.hasPermissions(googleAccount, Drive.SCOPE_APPFOLDER)) {

        } else {
            val intent: Intent = signInClient!!.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == 0) {
                isCatDismissed = true
                isGooglePlayGameServicesAvailable = false
                gameNotification!!.displayNoGooglePlayGameServices()
            }
        }
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)

        if (requestCode == RC_SIGN_IN) {
            Log.i("GUTEN TAG", "GOOD DAY")
        }
        Log.i("GUTEN Intent", intent.toString())
        Log.i("GUTEN request code", requestCode.toString())

//        val result: GoogleSignInResult? = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
//        if (result!!.isSuccess) {
//            // The signed in account is stored in the result.
//            val signedInAccount: GoogleSignInAccount? = result.signInAccount
//        } else {
//            var message: String? = result.status.statusMessage
//            if (message == null || message.isEmpty()) {
//                message = "Sign in error!!!"
//            }
//            AlertDialog.Builder(this).setMessage(message).setNeutralButton(android.R.string.ok, null).show()
//        }
    }

    private fun setupGameNotificationLabel() {
        gameNotification = GameNotification(view = View(this), parentLayout = rootLayout!!,
        params = LayoutParams((dUnitWidth * 12).toInt(), (dUnitHeight * 1.5).toInt(),
            ((dWidth - (dUnitWidth * 12)) * 0.5).toInt(),
            (dStatusBarHeight * 1.2).toInt()))
    }

    private fun startCatPresentation() {
        introAnimation!!.start()
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    staticSelf!!.runOnUiThread {
                        val handler = Handler()
                        handler.postDelayed(object : Runnable {
                            override fun run() {
                                if (isCatDismissed) {
                                    isCatDismissed = false
                                    startCatDismissal()
                                    handler.removeCallbacksAndMessages(null)
                                }
                                handler.postDelayed(this, 10)
                            }
                        }, 0)
                    }
                }
            }, 2000)
    }

    private fun startCatDismissal() {
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
        introAnimation = IntroView(imageView = ImageButton(this), parentLayout = rootLayout!!,
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
        adView!!.translationY = (dHeight - (getAdaptiveBannerAdSize().height * 2)).toFloat()
        adView!!.alpha = 0f
    }

    private fun setupBoardGame() {
        val boardGameSideLength:Float = (dUnitHeight * 9).toFloat()
        boardGame = BoardGame(boardView = View(this), parentLayout = rootLayout!!,
            params = LayoutParams(boardGameSideLength.toInt(), boardGameSideLength.toInt(), 0,0))
        boardGame!!.getThis().setBackgroundColor(Color.TRANSPARENT)
        CenterController.centerView(childView = boardGame!!.getThis(), childParams =
        boardGame!!.getOriginalParams(), parentParams = params!!)
        boardGame!!.setOriginalParams(boardGame!!.getThis().layoutParams as LayoutParams)
    }

    private fun setupColorOptions() {
        val boardGameSideLength:Float = (dUnitHeight * 9).toFloat()
        colorOptions = ColorOptions(view = View(this), parentLayout = rootLayout!!, params =
        LayoutParams(boardGameSideLength.toInt(), (dUnitHeight * 1.5).toInt(),
            boardGame!!.getOriginalParams().x, boardGame!!.getOriginalParams().y + boardGame!!.
            getOriginalParams().height))
        CenterController.centerViewHorizontally(colorOptions!!.getThis(), parentParams = params!!,
            childParams = colorOptions!!.getOriginalParams())
        colorOptions!!.setOriginalParams(colorOptions!!.getThis().layoutParams as LayoutParams)
        rootLayout!!.addView(ColorOptions.colorOptionsLayout!!)
    }

    private fun setupSettingsButton() {
        val sideLength:Float = (dHeight * ((1.0/300.0) + 0.085)).toFloat()
        settingsButton = SettingsButton(imageButton = ImageButton(this), parentLayout = rootLayout!!,
            params = LayoutParams(sideLength.toInt(), sideLength.toInt(), dUnitWidth.toInt(),
                dUnitHeight.toInt()))
    }

    private fun setupAttackMeter() {
        val height:Float = (dHeight * ((1.0/300.0) + 0.085)).toFloat()
        var width: Float = (dUnitWidth * 9).toFloat()
        var y:Float = (dUnitHeight).toFloat()
        var x: Float
        Log.i("ALRIGHT", "${dAspectRatio}")
        if (dAspectRatio >= 2.09){
            x = ((dWidth - width) * 0.5).toFloat()
            y = ((settingsButton!!.getOriginalParams().y +
                    settingsButton!!.getOriginalParams().height) +
                    (settingsButton!!.borderWidth * 2.0)).toFloat()
        } else if (dAspectRatio >= 1.7) {
            x = ((dWidth - width) * 0.5).toFloat()
            x += dUnitWidth.toFloat()
            width += (dUnitWidth * 0.5).toFloat()
        } else {
            width *= 1.4f
            x = ((dWidth - width) * 0.5).toFloat()
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
        val height:Float = (dHeight * ((1.0/300.0) + 0.085)).toFloat()
        val x:Float = (dWidth - height - dUnitWidth).toFloat()
        opponentLivesMeter = LivesMeter(meterView = View(this), parentLayout = rootLayout!!,
        params = LayoutParams(height.toInt(), height.toInt(), x.toInt(), dUnitHeight.toInt()),
            isOpponent = true)
    }

    private fun setupGameResults() {
        gameResults = GameResults(resultsView = View(rootView!!.context), parentLayout = rootLayout!!,
            params = LayoutParams((dUnitHeight * 7).toInt(), (dUnitHeight * 7.75).toInt(), 0, 0))
        CenterController.centerView(gameResults!!.getThis(), childParams = gameResults!!.getOriginalParams(),
            parentParams = LayoutParams(dWidth.toInt(), (dHeight).toInt(), 0, 0))
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
