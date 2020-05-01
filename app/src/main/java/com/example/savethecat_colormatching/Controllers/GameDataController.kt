package com.example.savethecat_colormatching.Controllers

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.savethecat_colormatching.HeaderViews.SettingsMenu
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.ParticularViews.MCView.Companion.mouseCoinCount
import com.example.savethecat_colormatching.SettingsMenu.Ads
import com.example.savethecat_colormatching.SettingsMenu.MoreCats.Companion.myCatsString

class GameDataController {

    private var gameSPData:SharedPreferences? = null
    private var gameSPEditor: SharedPreferences.Editor? = null

    fun setup() {
        setupSharedPreferences()
        loadGameData()
    }

    fun didSetupAlready(): Boolean {
        return (gameSPData != null)
    }

    private fun setupSharedPreferences() {
        gameSPData = MainActivity.staticSelf!!.getSharedPreferences(
            "SVTHCT_SP${MainActivity.playerID()}",
            Context.MODE_PRIVATE)
        gameSPEditor = gameSPData!!.edit()
    }

    fun saveThemeState(state:Int) {
        gameSPEditor!!.putInt("themeState", state)
        commitUpload()
        Ads.themeState = state
        SettingsMenu.adsButton!!.setStyle()
    }

    private fun loadGameData() {
        Ads.themeState = gameSPData!!.getInt("themeState", 2)
        Log.i("AMAZING", Ads.themeState.toString())
        MainActivity.mouseCoinView!!.startMouseCoinCount(
            gameSPData!!.getInt("mouseCoins",
                0))
        SettingsMenu.moreCatsButton!!.loadMyCatsData(
            gameSPData!!.getString("myCats",
                "sdd+1bdg00tco00etn00spR00ccn00col00nna00fat00"))
        MainActivity.staticSelf!!.setAllStyles(Ads.themeState)
    }

    fun uploadMouseCoinCount() {
        try {
            gameSPEditor!!.putInt("mouseCoins", mouseCoinCount)
            commitUpload()
        } catch (e: Exception) {
            MPController.displayFailureReason()
        }
    }

    fun uploadMyCatsData() {
        try {
            gameSPEditor!!.putString("myCats", myCatsString)
            commitUpload()
        } catch (e: Exception) {
        } catch (e: Exception) {
            MPController.displayFailureReason()
        }
    }

    private fun commitUpload() {
        if (!gameSPEditor!!.commit()) {
            displayFailureReason()
        }
    }

    private fun displayFailureReason() {
        if (!MainActivity.isInternetReachable) {
            MainActivity.gameNotification!!.displayNoInternet()
        }
        if (!MainActivity.isGooglePlayGameServicesAvailable) {
            MainActivity.gameNotification!!.displayNoGooglePlayGameServices()
        }
        MainActivity.gameNotification!!.displayFirebaseTrouble()
    }
}