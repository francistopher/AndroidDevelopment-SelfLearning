package com.example.savethecat_colormatching.Controllers

import android.os.Handler
import android.os.Looper
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.ParticularViews.BoardGame


class MPController {

    private val apiKey:String = "0d060465b202e38b86e0a96ac5ad2063e1cb6c4634bd7e89e26c8afb3aea24bc"
    private val secretKey:String = "0d060465b202e38b86e0a96ac5ad2063e1cb6c4634bd7e89e26c8afb3aea24bc"

    companion object {
        var connected:Boolean = false
        var calledDisconnect:Boolean = false
        var roomID:String = ""
        var displayName:String = ""
        var playerID:String = ""
    }

    fun connectToClient(playerID:String, displayName:String) {
        MPController.playerID = playerID
        MPController.displayName = displayName
    }

    fun didGetPlayerID():Boolean {
        return (playerID != "")
    }

    fun connectionDone() {
        connected = true

    }

    fun connect() {
        if (MainActivity.isGooglePlayGameServicesAvailable && MainActivity.isInternetReachable) {
            // connecting function
        } else {
            displayFailureReason()
        }
    }

    fun disconnect() {
        if (MainActivity.isGooglePlayGameServicesAvailable && MainActivity.isInternetReachable) {
            calledDisconnect = true
            // disconnecting function
            Handler(Looper.getMainLooper()).post {
                BoardGame.searchMG?.stopAnimation()
            }
        } else {
            displayFailureReason()
        }
    }

    fun displayFailureReason() {
        if (!MainActivity.isInternetReachable) {
            MainActivity.gameNotification!!.displayNoInternet()
        }
        if (!MainActivity.isGooglePlayGameServicesAvailable) {
            MainActivity.gameNotification!!.displayNoGooglePlayGameServices()
        }
    }
}