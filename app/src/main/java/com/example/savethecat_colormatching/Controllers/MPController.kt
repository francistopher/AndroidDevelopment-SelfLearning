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
    }

    private var searchTimeInMilli:Long = 30000
    private var pairedTimeInMilli:Long = 1800000
    private var isPlaying:Boolean = false
    private var isPlayerA:Boolean = false
    private var database: FirebaseDatabase? = null
    private var roomsReference: DatabaseReference? = null
    private var roomReference:DatabaseReference? = null
    private var valueAnimatorTimer:ValueAnimator? = null
    private var roomsReadyToJoin:List<DataSnapshot>? = null

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
        BoardGame.searchMG!!.stopAnimation()
        MainActivity.gameNotification!!.displayGameOpponent()
        MainActivity.boardGame!!.startTwoPlayerMatch()
        Log.i("MPCONTROLLER", "START PLAYING")
    }

    fun disconnect() {
        if (!isPlaying) {
            BoardGame.searchMG!!.stopAnimation()
            removeValues(MainActivity.playerID())
        }
    }

    private fun forcedRemoveValues(playerID: String) {
        database!!.getReference("rooms/$playerID/playerA").removeValue()
        database!!.getReference("rooms/$playerID/playerB").removeValue()
        database!!.getReference("rooms/$playerID/whenCreated").removeValue()
    }

    private fun removeValues(playerID:String) {
        if (roomReference != null) {
            database!!.getReference("rooms/$playerID/playerA").removeValue()
            database!!.getReference("rooms/$playerID/playerB").removeValue()
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
            }
        }
    }

    private fun setupRoom() {
        class RoomValueListener:ValueEventListener {
            override fun onCancelled(de: DatabaseError) {
                Log.i("MPCONTROLLER", "CANCELED ROOM")
                BoardGame.searchMG!!.stopAnimation()
                displayFailureReason()
            }
            override fun onDataChange(ds: DataSnapshot) {
                if (ds.children.count() == 3) {
                    if (isPlayerA) {
                        opponent = ds.child("playerB").value as String
                    } else {
                        opponent = ds.child("playerA").value as String
                    }
                    startPlaying()
                }
            }
        }
        createOrJoinRoom()
        roomReference!!.addValueEventListener(RoomValueListener())
    }

    private fun createOrJoinRoom() {
        if (roomsReadyToJoin!!.count() <= 0) {
            forcedRemoveValues(MainActivity.playerID())
        }
        roomReference = database!!.getReference(
            "rooms/" + getRoomNameToJoin() + "/"
        )
        database!!.getReference(
            "rooms/" + getRoomNameToJoin() + "/${getPlayer()}"
        ).setValue(MainActivity.displayName())
    }

    private fun getPlayer():String {
        return if (roomsReadyToJoin!!.count() > 0) {
            database!!.getReference(
                "rooms/" + getRoomNameToJoin() + "/whenCreated"
            ).setValue(System.currentTimeMillis())
            isPlayerA = false
            "playerB"
        } else {
            database!!.getReference(
                "rooms/" + getRoomNameToJoin() + "/whenCreated"
            ).setValue(System.currentTimeMillis())
            isPlayerA = true
            "playerA"
        }
    }

    private fun getRoomNameToJoin():String {
        if (roomsReadyToJoin!!.count() > 0) {
            return roomsReadyToJoin!!.random().key!!
        } else {
            return MainActivity.playerID()
        }
    }
}