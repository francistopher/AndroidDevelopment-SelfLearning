package com.example.savethecat_colormatching


import ARatio
import Reachability
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity(), Reachability.ConnectivityReceiverListener {

    companion object {
        var staticSelf: MainActivity? = null
        var rootView: View? = null
        var isThemeDark:Boolean = true
        var isInternetReachable:Boolean = false
        var aspectRatio:ARatio? = null
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rootView = window.decorView.rootView
        staticSelf = this
        setCurrentTheme()
        setupReachability()
        setupAspectRatio()
    }

    private fun setupReachability() {
        registerReceiver(Reachability(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    private val displayMetrics = DisplayMetrics()
    private fun setupAspectRatio() {
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        Log.i("Screen Width", "${displayMetrics.widthPixels}")
        Log.i("Screen Height", "${displayMetrics.heightPixels}")
    }
}
