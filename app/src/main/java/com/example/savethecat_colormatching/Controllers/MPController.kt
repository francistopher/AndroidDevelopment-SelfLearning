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
                roomsCount = ds.children.count()
                val children: List<DataSnapshot> = ds.children.filter { dataSnapshot ->
                    dataSnapshot.key!! == MainActivity.playerID()
                }
                if (children.count() != 0) {
                    database!!.getReference(
                        "rooms/${MainActivity.playerID()}/"
                    ).removeValue()
                }
            }
        }
        roomsReference = database!!.getReference("rooms/")
        roomsReference!!.addValueEventListener(RoomsValueListener())
    }

    fun didGetPlayerID(): Boolean {
        return (database != null)
    }

    fun connect() {
        buildRoom(roomsCount > 1)
        startValueAnimatorTimer()
    }

    fun disconnect() {
        BoardGame.searchMG!!.stopAnimation()
        roomReference?.removeValue()
        Log.i("MPCONTROLLER", "DISCONNECT")
    }

    private fun startValueAnimatorTimer() {
        valueAnimatorTimer = ValueAnimator.ofFloat(0f, 1f)
        valueAnimatorTimer!!.duration = 60000
        valueAnimatorTimer!!.start()
        valueAnimatorTimer!!.doOnEnd {
            disconnect()
        }
    }

    private fun buildRoom(toJoin:Boolean) {
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
        roomReference = if (toJoin) {
            database!!.getReference(
                "rooms/" + getRoomNameToJoin() + "/player2")
        } else {
            database!!.getReference(
                "rooms/" + MainActivity.playerID() + "/player1")
        }
        roomReference!!.addValueEventListener(RoomValueListener())
        roomReference!!.setValue(MainActivity.displayName())
    }

    private fun getRoomNameToJoin():String {
        return "room"
    }
}