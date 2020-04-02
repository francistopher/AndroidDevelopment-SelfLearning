package com.example.savethecat_colormatching.Controllers
import android.content.Context
import android.media.MediaPlayer
import android.provider.MediaStore
import com.example.savethecat_colormatching.R

class AudioController {

    companion object {
        private var gearSpinningPlayer:MediaPlayer? = null
        private var heavenPlayer:MediaPlayer? = null
        private var animeWowPlayer:MediaPlayer? = null
        private var coinEarnedPlayer:MediaPlayer? = null
        private var kittenMeowPlayer:MediaPlayer? = null
        private var kittenDiePlayer:MediaPlayer? = null
        private var mozartSonataPlayer: MediaPlayer? = null
        private var chopinPreludePlayer:MediaPlayer? = null

        fun setupGearSpinning(context: Context) {
            if (gearSpinningPlayer == null) {
                gearSpinningPlayer = MediaPlayer.create(context, R.raw.gearspinning)
            }
        }

        fun gearSpinning() {
            gearSpinningPlayer!!.stop()
            gearSpinningPlayer!!.start()
        }

        fun setupHeaven(context: Context) {
            if (heavenPlayer == null) {
                heavenPlayer = MediaPlayer.create(context, R.raw.heaven)
            }
        }

        fun heaven() {
            heavenPlayer!!.start()
        }

        fun setupAnimeWowPlayer(context: Context) {
            if (animeWowPlayer == null) {
                animeWowPlayer = MediaPlayer.create(context, R.raw.animewow)
            }
        }

        fun animeWow() {
            animeWowPlayer!!.stop()
            animeWowPlayer!!.start()
        }

        fun setupCoinEarnedPlayer(context: Context) {
            if (coinEarnedPlayer == null) {
                coinEarnedPlayer = MediaPlayer.create(context, R.raw.coinearned)
            }
        }

        fun coinEarned() {
            coinEarnedPlayer!!.start()
        }

        fun setupKittenMeowPlayer(context: Context) {
            if (kittenMeowPlayer == null) {
                kittenMeowPlayer = MediaPlayer.create(context, R.raw.kittenmeow)
            }
        }

        fun kittenMeow() {
            kittenMeowPlayer!!.start()
        }

        fun setupKittenDiePlayer(context: Context) {
            if (kittenDiePlayer == null) {
                kittenDiePlayer = MediaPlayer.create(context, R.raw.kittendie)
            }
        }

        fun kittenDie() {
            kittenDiePlayer!!.start()
        }

        fun setupMozartSonata(context: Context) {
            if (mozartSonataPlayer == null) {
                mozartSonataPlayer = MediaPlayer.create(context, R.raw.mozartsonata)
                mozartSonataPlayer!!.isLooping = true
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

        fun setupChopinPrelude(context: Context) {
            if (chopinPreludePlayer == null) {
                chopinPreludePlayer = MediaPlayer.create(context, R.raw.chopinprelude)
            }
        }

    }

}
