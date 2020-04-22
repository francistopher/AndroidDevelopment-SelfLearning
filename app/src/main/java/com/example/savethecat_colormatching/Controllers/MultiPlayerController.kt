package com.example.savethecat_colormatching.Controllers

import com.shephertz.app42.gaming.multiplayer.client.WarpClient

class MultiPlayerController {

    private val apiKey:String = "0d060465b202e38b86e0a96ac5ad2063e1cb6c4634bd7e89e26c8afb3aea24bc"
    private val secretKey:String = "0d060465b202e38b86e0a96ac5ad2063e1cb6c4634bd7e89e26c8afb3aea24bc"
    private var playerID:String = ""


    fun connectToClient(playerID:String) {
        this.playerID = playerID
        WarpClient.initialize(apiKey, secretKey)
    }

    fun didGetPlayerID():Boolean {
        return (playerID != "")
    }



}