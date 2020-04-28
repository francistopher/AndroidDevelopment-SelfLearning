package com.example.savethecat_colormatching.Controllers

import android.animation.ValueAnimator
import android.util.Log
import androidx.core.animation.doOnEnd
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.ParticularViews.BoardGame
import com.google.firebase.database.*

class MPController {

    companion object {
        fun displayFailureReason() {
            if (!MainActivity.isInternetReachable) {
                MainActivity.gameNotification!!.displayNoInternet()
            }
            if (!MainActivity.isGooglePlayGameServicesAvailable) {
                MainActivity.gameNotification!!.displayNoGooglePlayGameServices()
            }
        }
        var opponent:String = ""
        var isPlaying:Boolean = false
        var isPlayerA:Boolean? = null
    }

    private var searchTimeInMilli:Long = 30000
    private var pairedTimeInMilli:Long = 1800000
    private var database: FirebaseDatabase? = null
    private var roomsReference: DatabaseReference? = null
    private var roomReference:DatabaseReference? = null
    private var valueAnimatorTimer:ValueAnimator? = null
    private var roomsReadyToJoin:List<DataSnapshot>? = null
    private var roomValueListener:RoomValueListener? = null
    private var roomName:String? = null

    fun setup() {
        database = FirebaseDatabase.getInstance()
        setupRoomsReference()
    }

    private fun setupRoomsReference() {
        class RoomsValueListener:ValueEventListener {
            override fun onCancelled(de: DatabaseError) {
                Log.i("MPCONTROLLER", "CANCELED ROOMS")
                displayFailureReason()
            }
            override fun onDataChange(ds: DataSnapshot) {
                if (roomReference == null) {
                   removeOldUsers(ds)
                }
                roomsReadyToJoin = ds.children.filter {
                        dataSnapshot -> !dataSnapshot.hasChild("player2")
                }
            }
        }
        roomsReference = database!!.getReference("rooms/")
        roomsReference!!.addValueEventListener(RoomsValueListener())
    }

    private fun removeOldUsers(ds:DataSnapshot) {
        // Remove your previous self
        var children: List<DataSnapshot> = ds.children.filter { dataSnapshot ->
            dataSnapshot.key!! == MainActivity.playerID()
        }
        fun removeValues() {
            if (children.count() > 0) {
                for (child in children) {
                    forcedRemoveValues(child.key!!)
                }
            }
        }
        if (children.count() != 0) {
            forcedRemoveValues(MainActivity.playerID())
        }
        // Remove others unpaired
        children = ds.children.filter {
                dataSnapshot -> (
                (dataSnapshot.child("whenCreated").value as Long)
                        < System.currentTimeMillis() - searchTimeInMilli
                        && (dataSnapshot.children.count() == 2))
        }
        removeValues()
        // Remove paired
        children = ds.children.filter {
                dataSnapshot -> (
                (dataSnapshot.child("whenCreated").value as Long)
                        < System.currentTimeMillis() - pairedTimeInMilli
                        && (dataSnapshot.children.count() > 2))
        }
        removeValues()
    }

    fun didGetPlayerID(): Boolean {
        return (database != null)
    }

    fun startSearching() {
        setupRoom()
        startValueAnimatorTimer()
    }

    fun startPlaying() {
        isPlaying = true
        setLivesLeft(1)
        BoardGame.searchMG!!.stopAnimation()
        MainActivity.gameNotification!!.displayGameOpponent()
        MainActivity.boardGame!!.startTwoPlayerMatch()
        Log.i("MPCONTROLLER", "START PLAYING")
    }

    fun setLivesLeft(livesLeft:Long) {
        if (isPlaying) {
            if (isPlayerA!!) {
                database!!.getReference(
                    "rooms/${getRoomNameToJoin()}/playerALives"
                ).setValue(livesLeft)
            } else {
                database!!.getReference(
                    "rooms/${getRoomNameToJoin()}/playerBLives"
                ).setValue(livesLeft)
            }
        }
    }

    fun disconnect() {
        if (!isPlaying) {
            roomReference?.removeEventListener(roomValueListener!!)
            BoardGame.searchMG?.stopAnimation()
            roomReference = null
        }
    }

    fun closeRoom() {
        if (!isPlaying) {
            removeValues(MainActivity.playerID())
        }
    }

    private fun forcedRemoveValues(playerID: String) {
        database!!.getReference("rooms/$playerID/playerA").removeValue()
        database!!.getReference("rooms/$playerID/playerB").removeValue()
        database!!.getReference("rooms/$playerID/playerALives").removeValue()
        database!!.getReference("rooms/$playerID/playerBLives").removeValue()
        database!!.getReference("rooms/$playerID/whenCreated").removeValue()
    }

    private fun removeValues(playerID:String) {
        if (roomReference != null) {
            database!!.getReference("rooms/$playerID/playerA").removeValue()
            database!!.getReference("rooms/$playerID/playerB").removeValue()
            database!!.getReference("rooms/$playerID/playerALives").removeValue()
            database!!.getReference("rooms/$playerID/playerBLives").removeValue()
            database!!.getReference("rooms/$playerID/whenCreated").removeValue()
        }
    }

    private fun startValueAnimatorTimer() {
        valueAnimatorTimer = ValueAnimator.ofFloat(0f, 1f)
        valueAnimatorTimer!!.duration = 30000
        valueAnimatorTimer!!.start()
        valueAnimatorTimer!!.doOnEnd {
            if (!isPlaying) {
                disconnect()
                forcedRemoveValues(MainActivity.playerID())
            }
        }
    }

    class RoomValueListener:ValueEventListener {
        override fun onCancelled(de: DatabaseError) {
            MainActivity.mpController!!.disconnect()
            MainActivity.mpController!!.closeRoom()
        }

        private fun updateOpponentLivesMeter(livesLeft: Long) {
            if (MainActivity.opponentLivesMeter!!.getLivesLeftCount() < livesLeft) {
                MainActivity.opponentLivesMeter!!.incrementLivesLeftCount()
            }

            if (MainActivity.opponentLivesMeter!!.getLivesLeftCount() > livesLeft) {
                MainActivity.opponentLivesMeter!!.dropLivesLeftHeart()
            }
        }

        override fun onDataChange(ds: DataSnapshot) {
            if (ds.children.count() == 3) {
                opponent = if (isPlayerA!!) {
                    ds.child("playerB").value as String
                } else {
                    ds.child("playerA").value as String
                }
                MainActivity.mpController!!.startPlaying()
            } else if (ds.children.count() == 5) {
                if (isPlayerA!!) {
                    if ((ds.child("playerBLives").value as Long) < 1) {
                        MainActivity.boardGame!!.wonMultiPlayer()
                    } else {
                        updateOpponentLivesMeter(ds.child("playerBLives").value as Long)
                    }
                } else {
                    if ((ds.child("playerALives").value as Long) < 1) {
                        MainActivity.boardGame!!.wonMultiPlayer()
                    } else {
                        updateOpponentLivesMeter(ds.child("playerALives").value as Long)
                    }
                }
            }
        }
    }

    private fun setupRoom() {
        if (roomReference != null || roomsReadyToJoin!!.count() <= 0) {
            roomReference?.removeEventListener(roomValueListener!!)
            forcedRemoveValues(MainActivity.playerID())
        }
        createOrJoinRoom()
        roomValueListener = RoomValueListener()
        roomReference!!.addValueEventListener(roomValueListener!!)
    }

    private fun createOrJoinRoom() {
        roomReference = database!!.getReference(
            "rooms/" + getRoomNameToJoin() + "/"
        )
        database!!.getReference(
            "rooms/" + getRoomNameToJoin() + "/${getPlayer()}"
        ).setValue(MainActivity.displayName())
    }

    private fun getPlayer():String {
        return if (isPlayerA!!) {
            setTimeCreated()
            "playerA"
        } else {
            setTimeCreated()
            "playerB"
        }
    }

    private fun setTimeCreated() {
        database!!.getReference(
            "rooms/" + getRoomNameToJoin() + "/whenCreated"
        ).setValue(System.currentTimeMillis())
    }

    private fun getRoomNameToJoin():String {
        return if (isPlayerA == null) {
            if (roomsReadyToJoin!!.count() > 0) {
                isPlayerA = false
                roomName = roomsReadyToJoin!!.random().key!!
                roomName!!
            } else {
                isPlayerA = true
                roomName = MainActivity.playerID()
                roomName!!
            }
        } else {
            roomName!!
        }
    }
}