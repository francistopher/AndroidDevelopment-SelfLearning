package com.example.savethecat_colormatching.Controllers

import android.util.Log
import com.example.savethecat_colormatching.MainActivity
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MultiplayerController {

    private var playerID:String = ""
    private var firebaseAuth:FirebaseAuth? = null
    private var authResult:AuthResult? = null

    fun setPlayerID(playerID:String) {
        this.playerID = playerID
        startAuth()
    }

    private fun startAuth() {
        firebaseAuth = FirebaseAuth.getInstance()
        val credential:AuthCredential = GoogleAuthProvider.getCredential(MainActivity.signedInAccount!!.idToken!!, null)
        firebaseAuth!!.signInWithCredential(credential).addOnSuccessListener { authResult ->
            this.authResult = authResult
            Log.i("WEEEEEE", "OK")
        }
    }



}