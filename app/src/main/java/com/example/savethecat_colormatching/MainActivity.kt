package com.example.savethecat_colormatching


import ARatio
import Reachability
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.savethecat_colormatching.Controllers.AudioController
import com.example.savethecat_colormatching.Controllers.CenterController
import com.example.savethecat_colormatching.CustomViews.CButton
import com.example.savethecat_colormatching.CustomViews.CImageView
import com.example.savethecat_colormatching.CustomViews.CLabel
import com.example.savethecat_colormatching.ParticularViews.IntroView


class MainActivity : AppCompatActivity(), Reachability.ConnectivityReceiverListener {

    companion object {
        var staticSelf: MainActivity? = null
        var rootView: View? = null
        var isThemeDark:Boolean = true
        var isInternetReachable:Boolean = false
        var aspectRatio:ARatio? = null
        var decorView:View? = null
        // Display properties
        var dWidth:Double = 0.0
        var dHeight:Double = 0.0
        var dUnitWidth:Double = 0.0
        var dUnitHeight:Double = 0.0
        // Custom Font
        var rootLayout:AbsoluteLayout? = null
        // Absolute Layout Params
        var params:AbsoluteLayout.LayoutParams? = null
    }

    var introAnimation:IntroView? = null

    private fun setCurrentTheme() {
//        mainFont = ResourcesCompat.getFont(this, R.font.sleepyfatcat)
        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_NO -> isThemeDark = false
            Configuration.UI_MODE_NIGHT_YES -> isThemeDark = true
            Configuration.UI_MODE_NIGHT_UNDEFINED -> isThemeDark = false
        }
        if (isThemeDark) {
            rootView?.setBackgroundColor(Color.BLACK)
        } else {
            rootView?.setBackgroundColor(Color.WHITE)
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

    fun hideSystemBars(): Int {
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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupDecorView()
        rootView = window.decorView.rootView
        rootLayout = AbsoluteLayout(this)
        staticSelf = this
        setCurrentTheme()
        setupReachability()
        setupAspectRatio()
        setupSounds()
        setupSomething()
        setupIntroAnimation()
        AudioController.mozartSonata(true, false)
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

    private val displayMetrics:DisplayMetrics = DisplayMetrics()
    private var screenAspectRatio:Double = 0.0;
    private fun setupAspectRatio() {
        fun getNavigationBarHeight(): Int {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                val metrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(metrics)
                val usableHeight = metrics.heightPixels
                windowManager.defaultDisplay.getRealMetrics(metrics)
                val realHeight = metrics.heightPixels
                return if (realHeight > usableHeight) realHeight - usableHeight else 0
            }
            return 0
        }
        fun setupScreenDimension() {
            dWidth = displayMetrics.widthPixels.toDouble()
            dHeight = displayMetrics.heightPixels.toDouble()
        }
        fun setupUnitScreenDimension() {
            dUnitWidth = dWidth / 18.0
            dUnitHeight = dHeight / 18.0
        }
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        screenAspectRatio = (dHeight / dWidth)
        when {
            screenAspectRatio > 2.16 -> {
                aspectRatio = ARatio.ar19point5by9
            }
            screenAspectRatio > 2.09 -> {
                aspectRatio = ARatio.ar19by9
            }
            screenAspectRatio > 2.07 -> {
                aspectRatio = ARatio.ar18point7by9
            }
            screenAspectRatio > 2.05 -> {
                aspectRatio = ARatio.ar18point5by9
            }
            screenAspectRatio > 1.9 -> {
                aspectRatio = ARatio.ar18by9
            }
            screenAspectRatio > 1.8 -> {
                aspectRatio = ARatio.ar19by10
            }
            screenAspectRatio > 1.7 -> {
                aspectRatio = ARatio.ar16by9
            }
            screenAspectRatio > 1.66 -> {
                aspectRatio = ARatio.ar5by3
            }
            screenAspectRatio > 1.5 -> {
                aspectRatio = ARatio.ar16by10
            }
            screenAspectRatio > 1.4 -> {
                aspectRatio = ARatio.ar3by2
            }
            else -> {
                // screen aspect ratio > 1.3
                aspectRatio = ARatio.ar4by3
            }
        }
        if (aspectRatio != ARatio.ar19point5by9) {
            dHeight *= 1.2
            setupScreenDimension()
            params = AbsoluteLayout.LayoutParams(dWidth.toInt(), dHeight.toInt(), 0, 0)
            setupUnitScreenDimension()
        }
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
        setContentView(rootLayout!!)
        introAnimation!!.start()
    }

    private fun setupSomething() {


    }



}
