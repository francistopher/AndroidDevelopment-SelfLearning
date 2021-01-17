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

    /*
        Setup the variabes that store and edit game data
     */
    private fun setupSharedPreferences() {
        gameSPData = MainActivity.staticSelf!!.getSharedPreferences(
            "SVTHCT_SP${MainActivity.playerID()}",
            Context.MODE_PRIVATE)
        gameSPEditor = gameSPData!!.edit()
    }

    /*
        Sets the theme style and saves it
     */
    fun saveThemeState(state:Int) {
        gameSPEditor!!.putInt("themeState", state)
        commitUpload()
        Ads.themeState = state
        SettingsMenu.adsButton!!.setStyle()
    }

    /*
        Load the game data such as the mouse coins and the cats purchased
     */
    private fun loadGameData() {
        Ads.themeState = gameSPData!!.getInt("themeState", 2)
        MainActivity.mouseCoinView!!.startMouseCoinCount(
            gameSPData!!.getInt("mouseCoins",
                0))
        SettingsMenu.moreCatsButton!!.loadMyCatsData(
            gameSPData!!.getString("myCats",
                "sdd+1bdg00tco00etn00spR00ccn00col00nna00fat00"))
        MainActivity.staticSelf!!.setAllStyles(Ads.themeState)
    }

    /*
        Update mouse coin count data
     */
    fun uploadMouseCoinCount() {
        try {
            gameSPEditor!!.putInt("mouseCoins", mouseCoinCount)
            commitUpload()
        } catch (e: Exception) {
            MPController.displayFailureReason()
        }
    }

    /*
        Update the cats purchased data
     */
    fun uploadMyCatsData() {
        try {
            gameSPEditor!!.putString("myCats", myCatsString)
            commitUpload()
        } catch (e: Exception) {
        } catch (e: Exception) {
            MPController.displayFailureReason()
        }
    }

    /*
        Finalize the updates
     */
    private fun commitUpload() {
        if (!gameSPEditor!!.commit()) {
            displayFailureReason()
        }
    }

    /*
        Display an ingame notification for the data upload
     */
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