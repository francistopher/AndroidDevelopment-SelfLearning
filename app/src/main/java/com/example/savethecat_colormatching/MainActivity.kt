package com.example.savethecat_colormatching


import com.example.savethecat_colormatching.Controllers.AREnum
import Reachability
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.savethecat_colormatching.Characters.Enemies
import com.example.savethecat_colormatching.Controllers.AspectRatio
import com.example.savethecat_colormatching.Controllers.AudioController
import com.example.savethecat_colormatching.Controllers.CenterController
import com.example.savethecat_colormatching.ParticularViews.BoardGame
import com.example.savethecat_colormatching.ParticularViews.ColorOptions
import com.example.savethecat_colormatching.ParticularViews.IntroView
import com.google.android.gms.ads.*
import java.util.*


class MainActivity : AppCompatActivity(), Reachability.ConnectivityReceiverListener {

    companion object {
        var staticSelf: MainActivity? = null
        var rootView: View? = null
        var isThemeDark:Boolean = true
        var isInternetReachable:Boolean = false
        var aspectRatio: AREnum? = null
        var decorView:View? = null
        // Display properties
        var dWidth:Double = 0.0
        var dHeight:Double = 0.0
        var adHeight:Double = 0.0
        var dUnitWidth:Double = 0.0
        var dUnitHeight:Double = 0.0
        var dNavigationBarHeight:Double = 0.0
        // Custom Font
        var rootLayout:AbsoluteLayout? = null
        // Absolute Layout Params
        var params:AbsoluteLayout.LayoutParams? = null
        var successGradientView:View? = null
        var enemies:Enemies? = null
        // Board Game
        var boardGame:BoardGame? = null
        var colorOptions:ColorOptions? = null
    }

    var introAnimation:IntroView? = null

    private fun setCurrentTheme() {
//        mainFont = ResourcesCompat.getFont(this, R.font.sleepyfatcat)
        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_NO -> isThemeDark = false
            Configuration.UI_MODE_NIGHT_YES -> isThemeDark = true
            Configuration.UI_MODE_NIGHT_UNDEFINED -> isThemeDark = false
        }
        updateTheme()

    }

    private fun updateTheme() {
        if (isThemeDark) {
            rootView?.setBackgroundColor(Color.BLACK)
        } else {
            rootView?.setBackgroundColor(Color.WHITE)
        }
        introAnimation!!.setStyle()
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
        AspectRatio.setupAspectRatio()
        setupSaveTheCat()
        setCurrentTheme()
        setContentView(rootLayout!!)
    }

    private fun setupSaveTheCat() {
        setupSounds()
        setSuccessGradientViewAndLayer()
        setupEnemies()
        setupIntroAnimation()
        setupAdvertisement()
        setupBoardGame()
        setupColorOptions()
//        AudioController.mozartSonata(play = true, startOver = false)
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
        AudioController.setupGearSpinning(this)
        AudioController.setupHeaven(this)
        AudioController.setupAnimeWowPlayer(this)
        AudioController.setupCoinEarnedPlayer(this)
        AudioController.setupKittenMeowPlayer(this)
        AudioController.setupKittenDiePlayer(this)
        AudioController.setupChopinPrelude(this)
        AudioController.setupMozartSonata(this)
    }

    private fun setupIntroAnimation() {
        val width:Double = dUnitWidth * 9.0
        introAnimation = IntroView(imageView = ImageView(this), parentLayout = rootLayout!!,
            params = AbsoluteLayout.LayoutParams(width.toInt(), width.toInt(), 0, 0))
        introAnimation!!.loadTextImages(lightTextImageR = R.drawable.darkintrotext, darkTextImageR = R.drawable.lightintrotext,
            lightCatImageR = R.drawable.darkcat, darkCatImageR = R.drawable.lightcat)
        CenterController.center(introAnimation!!.getTextImage(), introAnimation!!.getTextParams(), params!!)
        CenterController.center(introAnimation!!.getCatImage(), introAnimation!!.getCatParams(), params!!)
        introAnimation!!.start()
    }

    private fun setSuccessGradientViewAndLayer() {
        successGradientView = View(rootView!!.context)
        successGradientView!!.layoutParams = AbsoluteLayout.LayoutParams(dWidth.toInt(), (dHeight * 0.15).toInt(), 0, 0)
        successGradientView!!.setBackgroundColor(Color.TRANSPARENT)
        rootLayout!!.addView(successGradientView)
        successGradientView!!.setBackgroundDrawable( GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor("#fcd340"), Color.TRANSPARENT)))
        successGradientView!!.alpha = 0.0f
    }

    private fun setupEnemies() {
        enemies = Enemies()
//        enemies!!.hide()
    }

    private fun setupAdvertisement() {
        MobileAds.initialize(this) {}
        setupBannerAds()
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
        // Resetting position
        adView!!.layoutParams = AbsoluteLayout.LayoutParams((adView!!.adSize.width * 2) + 1,
            (adView!!.adSize.height * 2) + 1, 0, (adHeight - (adView!!.adSize.height * 0.25)).toInt())
        adRequest = AdRequest.Builder().build()
        adView!!.loadAd(adRequest)
        rootLayout!!.addView(adView)
    }

    private fun setupBoardGame() {
        val boardGameSideLength:Float = (dUnitHeight * 8.5).toFloat()
        boardGame = BoardGame(boardView = View(this), parentLayout = rootLayout!!,
            params = AbsoluteLayout.LayoutParams(boardGameSideLength.toInt(),
                boardGameSideLength.toInt(), 0,0))
        boardGame!!.getThis().setBackgroundColor(Color.TRANSPARENT)
        CenterController.centerView(childView = boardGame!!.getThis(),
            childParams = boardGame!!.getOriginalParams(),
            parentParams = AbsoluteLayout.LayoutParams(dWidth.toInt(), (adHeight * 1.05).toInt(), 0, 0))
        boardGame!!.setOriginalParams(boardGame!!.getThis().layoutParams as AbsoluteLayout.LayoutParams)
        boardGame!!.buildGame()
        rootLayout!!.addView(BoardGame.boardGameLayout)
    }

    private fun setupColorOptions() {
        val boardGameSideLength:Float = (dUnitHeight * 8.5).toFloat()
        colorOptions = ColorOptions(view = View(this), parentLayout = rootLayout!!, params =
        AbsoluteLayout.LayoutParams(boardGameSideLength.toInt(), (dUnitHeight * 1.5).toInt(),
            boardGame!!.getOriginalParams().x, boardGame!!.getOriginalParams().y + boardGame!!.
            getOriginalParams().height))
        CenterController.centerViewHorizontally(colorOptions!!.getThis(), parentParams = AbsoluteLayout.
        LayoutParams(dWidth.toInt(), (adHeight * 1.05).toInt(), 0, 0), childParams =
        colorOptions!!.getOriginalParams())

    }

}
