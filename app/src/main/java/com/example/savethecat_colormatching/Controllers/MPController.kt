package com.example.savethecat_colormatching.Controllers

import android.animation.ValueAnimator
import android.util.Log
import androidx.core.animation.doOnEnd
import com.example.savethecat_colormatching.MainActivity
import com.example.savethecat_colormatching.ParticularViews.BoardGame
import com.google.firebase.database.*

class MPController {

    companion object {
        var roomsCount:Int = -1
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
                roomsCount = ds.children.count()
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
                (dataSnapshot.child("whenCreated").value as Long) < System.currentTimeMillis() - 30000 )
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

    fun connect() {
        setupRoom(roomsCount > 1)
        startValueAnimatorTimer()
    }

    fun disconnect() {
        BoardGame.searchMG!!.stopAnimation()
        removeValues(MainActivity.playerID())
        Log.i("MPCONTROLLER", "DISCONNECT")
    }

    private fun removeValues(playerID:String) {
        if (roomReference != null) {
            database!!.getReference("rooms/$playerID/player1").removeValue()
            database!!.getReference("rooms/$playerID/player2").removeValue()
            database!!.getReference("rooms/$playerID/whenCreated").removeValue()
        }
    }

    private fun startValueAnimatorTimer() {
        valueAnimatorTimer = ValueAnimator.ofFloat(0f, 1f)
        valueAnimatorTimer!!.duration = 30000
        valueAnimatorTimer!!.start()
        valueAnimatorTimer!!.doOnEnd {
            disconnect()
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
                Log.i("MPCONTROLLER", "MY ROOM CREATED")
            }
        }
        if (toJoin) {
           joinRoom()
        } else {
            createRoom()
        }
        addTimeStamp()
        roomReference!!.addValueEventListener(RoomValueListener())
    }

    private fun addTimeStamp() {
        database!!.getReference(
            "rooms/" + MainActivity.playerID() + "/whenCreated"
        ).setValue(System.currentTimeMillis())
    }

    private fun createRoom() {
        roomReference = database!!.getReference(
            "rooms/" + MainActivity.playerID() + "/"
        )
        database!!.getReference(
            "rooms/" + MainActivity.playerID() + "/player1"
        ).setValue(MainActivity.displayName())
    }

    private fun joinRoom() {
        roomReference = database!!.getReference("rooms/" + getRoomNameToJoin() + "/")
        database!!.getReference(
            "rooms/" + MainActivity.playerID() + "/player2"
        ).setValue(MainActivity.displayName())
    }

    private fun getRoomNameToJoin():String {
        return "room"
    }
}