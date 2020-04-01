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
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity(), Reachability.ConnectivityReceiverListener {

    companion object {
        var staticSelf: MainActivity? = null
        var rootView: View? = null
        var isThemeDark:Boolean = true
        var isInternetReachable:Boolean = false
        var aspectRatio:ARatio? = null
        var decorView:View? = null
    }

    private fun setCurrentTheme() {
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
        staticSelf = this
        setCurrentTheme()
        setupReachability()
        setupAspectRatio()
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

        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenAspectRatio = (displayMetrics.heightPixels.toDouble() + getNavigationBarHeight().toDouble()) / (displayMetrics.widthPixels)
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
    }
}
