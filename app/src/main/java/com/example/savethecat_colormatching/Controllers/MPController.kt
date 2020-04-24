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
    }

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
                Log.i("MPController", "Rooms Count" + roomsReadyToJoin!!.count().toString())
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
        if (children.count() != 0) {
            database!!.getReference("rooms/${MainActivity.playerID()}/player1").removeValue()
            database!!.getReference("rooms/${MainActivity.playerID()}/player2").removeValue()
            database!!.getReference("rooms/${MainActivity.playerID()}/whenCreated").removeValue()
        }
        // Remove others
        children = ds.children.filter {
                dataSnapshot -> (
                (dataSnapshot.child("whenCreated").value as Long) < System.currentTimeMillis() - 30000
                        && (dataSnapshot.child("whenCreated").value as Long) != (-1).toLong()
                )
        }
        if (children.count() > 0) {
            for (child in children) {
                database!!.getReference("rooms/${child.key!!}/player1").removeValue()
                database!!.getReference("rooms/${child.key!!}/player2").removeValue()
                database!!.getReference("rooms/${child.key!!}/whenCreated").removeValue()
            }
        }
    }

    fun didGetPlayerID(): Boolean {
        return (database != null)
    }

    fun startSearching() {
        setupRoom(roomsReadyToJoin!!.count() > 0)
        startValueAnimatorTimer()
    }

    fun startPlaying() {
        BoardGame.searchMG!!.stopAnimation()
        Log.i("MPCONTROLLER", "START PLAYING")
    }

    fun disconnect() {
        if (!isPlaying) {
            BoardGame.searchMG!!.stopAnimation()
            removeValues(MainActivity.playerID())
        }
    }

    private fun removeValues(playerID:String) {
        if (roomReference != null) {
            database!!.getReference("rooms/$playerID/playerA").removeValue()
            database!!.getReference("rooms/$playerID/playerB").removeValue()
            database!!.getReference("rooms/$playerID/whenCreated").removeValue()
        }
    }

    private var isPlaying:Boolean = false
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

    private fun setupRoom(toJoin:Boolean) {
        class RoomValueListener:ValueEventListener {
            override fun onCancelled(de: DatabaseError) {
                Log.i("MPCONTROLLER", "CANCELED ROOM")
                BoardGame.searchMG!!.stopAnimation()
                displayFailureReason()
            }
            override fun onDataChange(ds: DataSnapshot) {
                if (ds.children.count() == 3) {
                    isPlaying = true
                    startPlaying()
                }
            }
        }
        createOrJoinRoom()
        roomReference!!.addValueEventListener(RoomValueListener())
    }

    private fun createOrJoinRoom() {
        roomReference = database!!.getReference(
            "rooms/" + getRoomNameToJoin() + "/"
        )
        database!!.getReference(
            "rooms/" + getRoomNameToJoin() + "/${getPlayer()}"
        ).setValue(getRoomNameToJoin())
    }

    private fun getPlayer():String {
        return if (roomsReadyToJoin!!.count() > 0) {
            database!!.getReference(
                "rooms/" + getRoomNameToJoin() + "/whenCreated"
            ).setValue((-1).toLong())
            "playerB"
        } else {
            database!!.getReference(
                "rooms/" + getRoomNameToJoin() + "/whenCreated"
            ).setValue(System.currentTimeMillis())
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