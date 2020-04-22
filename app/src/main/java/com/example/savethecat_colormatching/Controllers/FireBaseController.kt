package com.example.savethecat_colormatching.Controllers

import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.ParticularViews.MCView
import com.example.savethecat_colormatching.ParticularViews.SettingsMenu
import com.example.savethecat_colormatching.SettingsMenu.MoreCats
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MultiPlayerController {

    private var playerID:String = ""
    private var firestoreDb:FirebaseFirestore? = null
    private var collectionReference:CollectionReference? = null
    private var documentReference:DocumentReference? = null

    fun setPlayerID(playerID:String) {
        this.playerID = playerID
        setupFireStore()
        getDocumentData()
    }

    fun didGetPlayerID(): Boolean {
        return (playerID != "")
    }

    private fun setupFireStore() {
        firestoreDb = Firebase.firestore
        collectionReference = firestoreDb!!.collection("user_data")
        documentReference = collectionReference!!.document(this.playerID)
    }

    fun setDocumentData() {
        if (MainActivity.isInternetReachable && MainActivity.isGooglePlayGameServicesAvailable) {
            val userData = hashMapOf(
                "displayName" to MainActivity.localPlayer!!.displayName,
                "mouseCoins" to MCView.mouseCoinCount,
                "myCats" to MoreCats.myCatsString
            )
            documentReference!!.set(userData).addOnFailureListener {
                MainActivity.gameNotification!!.displayFirebaseTrouble()
            }
        } else {
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

    fun getDocumentData() {
        if (MainActivity.isInternetReachable && MainActivity.isGooglePlayGameServicesAvailable) {
            documentReference!!.get().addOnSuccessListener { document ->
                MainActivity.gameNotification!!.displayFirebaseConnected()
                if (document != null) {
                    MainActivity.mouseCoinView!!.startMouseCoinCount(
                        document.get("mouseCoins").toString().toInt()
                    )
                    SettingsMenu.moreCatsButton!!.loadMyCatsData(
                        document.get("myCats")!!.toString()
                    )
                } else {
                    MainActivity.mouseCoinView!!.startMouseCoinCount(0)
                    SettingsMenu.moreCatsButton!!.loadMyCatsData(
                        "sdd+1bdg00tco00etn00spR00ccn00col00nna00fat00"
                    )
                }
            }
        } else {
            displayFailureReason()
        }
    }
}