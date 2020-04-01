package com.example.savethecat_colormatching.Controllers
import android.content.Context
import android.media.MediaPlayer
import com.example.savethecat_colormatching.R

class AudioController {

    companion object {
        private var mozartSonataPlayer: MediaPlayer? = null


        fun setupMozartSonata(context: Context) {
            if (mozartSonataPlayer == null) {
                mozartSonataPlayer = MediaPlayer.create(context, R.raw.mozartsonata)
            }
        }

        fun mozartSonata(play:Boolean, startOver:Boolean) {
            if (play) {
                if (startOver) {
                    mozartSonataPlayer!!.stop()
                    mozartSonataPlayer!!.start()
                } else {
                    mozartSonataPlayer!!.start()
                }
            } else {
                mozartSonataPlayer!!.stop()
            }
        }



    }

}
