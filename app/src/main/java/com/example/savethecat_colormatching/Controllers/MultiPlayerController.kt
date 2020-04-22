package com.example.savethecat_colormatching.Controllers

import android.content.Context
import android.util.Log
import android.widget.AbsoluteLayout
import android.widget.AbsoluteLayout.LayoutParams
import android.widget.Button
import com.example.savethecat_colormatching.MainActivity
import com.shephertz.app42.gaming.multiplayer.client.WarpClient
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener

class MultiPlayerController {

    private val apiKey:String = "0d060465b202e38b86e0a96ac5ad2063e1cb6c4634bd7e89e26c8afb3aea24bc"
    private val secretKey:String = "0d060465b202e38b86e0a96ac5ad2063e1cb6c4634bd7e89e26c8afb3aea24bc"
    private var playerID:String = ""
    private var myGame:WarpClient? = null


    companion object {
        var connected:Boolean = false
        var calledDisconnect:Boolean = false
    }

    fun connectToClient(playerID:String) {
        this.playerID = playerID
        WarpClient.initialize(apiKey, secretKey)
        myGame = WarpClient.getInstance()
        myGame!!.addConnectionRequestListener(ConnectionListener())
    }

    fun didGetPlayerID():Boolean {
        return (playerID != "")
    }

    fun connect() {
        if (MainActivity.isGooglePlayGameServicesAvailable && MainActivity.isInternetReachable) {
            myGame!!.connectWithUserName(MainActivity.localPlayer!!.displayName)
//            myGame!!.joinRoomInRange(2, 2, true)
        } else {
            displayFailureReason()
        }
    }

    fun disconnect() {
        if (MainActivity.isGooglePlayGameServicesAvailable && MainActivity.isInternetReachable) {
            calledDisconnect = true
            myGame!!.disconnect()
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

class ConnectionListener:ConnectionRequestListener {
    override fun onDisconnectDone(p0: ConnectEvent?) {
        MultiPlayerController.calledDisconnect = true
        Log.i("CONNECTION STATE", "DISCONNECT")
    }

    override fun onConnectDone(p0: ConnectEvent?) {
        MultiPlayerController.connected = true
        Log.i("CONNECTION STATE", "CONNECT")
    }

    override fun onInitUDPDone(p0: Byte) {
        Log.i("CONNECTION STATE", "UDP")
    }
}

enum class  MGPosition {
    TOPLEFT,
    TOPRIGHT,
    BOTTOMLEFT,
    BOTTOMRIGHT,
    CENTER
}

class SearchMG(button: Button,
               parentLayout:AbsoluteLayout,
               params:LayoutParams,
               topLeftCorner:Pair<Int, Int>,
               bottomRightCorner:Pair<Int, Int>) {

    private var buttonMG:Button? = null
    private var searchContext: Context? = null
    private var originalParams:LayoutParams? = null
    private var parentLayout:AbsoluteLayout? = null
    private var targetParams:MutableMap<MGPosition, LayoutParams>? = mutableMapOf()

    init {
        setupMGButton(button)
        setupOriginalParams(params)
        setupParentLayout(parentLayout)
        setupTargetParams(topLeftCorner, bottomRightCorner)

    }

    private fun setupTargetParams(topLeft:Pair<Int, Int>, bottomRight:Pair<Int, Int>) {
        targetParams!![MGPosition.TOPLEFT] = LayoutParams(originalParams!!.width,
            originalParams!!.height, topLeft.first, topLeft.second)
        targetParams!![MGPosition.TOPRIGHT] = LayoutParams(originalParams!!.width,
            originalParams!!.height, bottomRight.first - originalParams!!.width, topLeft.second)
        targetParams!![MGPosition.BOTTOMLEFT] = LayoutParams(originalParams!!.width,
            originalParams!!.height, topLeft.first, bottomRight.second - originalParams!!.height)
        targetParams!![MGPosition.BOTTOMRIGHT] = LayoutParams(originalParams!!.width,
            originalParams!!.height, bottomRight.first - originalParams!!.width,
            bottomRight.second - originalParams!!.height)
    }

    private  fun setupMGButton(button:Button) {
        buttonMG = button
        searchContext = button.context
    }

    private fun setupOriginalParams(params: LayoutParams) {
        originalParams = params
        buttonMG!!.layoutParams = params
        targetParams!![MGPosition.CENTER] = params
    }

    private fun setupParentLayout(layout:AbsoluteLayout) {
        parentLayout = layout
        layout.addView(buttonMG!!)
    }


}


//class UISearchMagnifyGlass:UICButton {
//
//    var label:UICLabel?
//    var targetFrames:[Target:CGRect] = [:];
//    var previousTarget:Target?
//    var nextTarget:Target?
//    var transitionAnimation:UIViewPropertyAnimator?
//
//    init(parentView:UIView, frame:CGRect) {
//        super.init(parentView: parentView, frame: frame, backgroundColor: UIColor.clear);
//        self.layer.borderWidth = 0.0;
//        self.alpha = 0.0;
//        setupLabel();
//        setupTargetFrames(parentView);
//        setupTransitionAnimation();
//        setThisStyle();
//    }
//

//
//    func setupLabel() {
//        let height:CGFloat = frame.height * 0.25;
//        label = UICLabel(parentView: self, x: 0.0, y: frame.height * 0.9, width: frame.width, height: height * 2.0)
//        label!.backgroundColor = UIColor.clear;
//        label!.numberOfLines = 2;
//        label!.lineBreakMode = NSLineBreakMode.byWordWrapping;
//        label!.text = "Searching for\nOpponent";
//        label!.font = UIFont.boldSystemFont(ofSize: height * 0.65);
//    }
//
//    func setNextTarget() {
//        var targets:[Target] = [.topLeft, .topRight, .bottomLeft, .bottomRight, .center];
//        var index:Int = -1;
//        if (nextTarget != nil) {
//            index = targets.firstIndex(of: nextTarget!)!;
//            targets.remove(at: index);
//            if (previousTarget != nil) {
//                index = targets.firstIndex(of: previousTarget!)!;
//                targets.remove(at: index);
//            }
//            previousTarget = nextTarget!;
//        }
//        nextTarget = targets.randomElement()!;
//    }
//
//    func setupTransitionAnimation() {
//        setNextTarget();
//        transitionAnimation = UIViewPropertyAnimator(duration: 1.5, curve: .easeInOut, animations: {
//            self.frame = self.targetFrames[self.nextTarget!]!;
//        })
//        transitionAnimation!.addCompletion({ _ in
//                self.setupTransitionAnimation();
//            self.transitionAnimation!.startAnimation();
//        })
//    }
//
//    func startAnimation() {
//        self.superview!.bringSubviewToFront(self);
//        self.transitionAnimation!.startAnimation();
//        self.alpha = 1.0;
//    }
//
//    func setThisStyle() {
//        if (UIScreen.main.traitCollection.userInterfaceStyle.rawValue == 1) {
//            self.setImage(UIImage(named: "lightMagnifyGlass.png"), for: .normal);
//            label!.textColor = UIColor.black;
//        } else {
//            self.setImage(UIImage(named: "darkMagnifyGlass.png"), for: .normal);
//            label!.textColor = UIColor.white;
//        }
//        self.imageView!.contentMode = UIView.ContentMode.scaleAspectFit;
//    }
//
//    required init?(coder: NSCoder) {
//        fatalError("init(coder:) has not been implemented")
//    }
//}