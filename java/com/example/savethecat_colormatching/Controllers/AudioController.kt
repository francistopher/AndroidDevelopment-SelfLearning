package com.example.savethecat_colormatching.Controllers
import android.content.Context
import android.media.MediaPlayer
import com.example.savethecat_colormatching.R
import com.example.savethecat_colormatching.SettingsMenu.Volume

class AudioController {

    companion object {
        // Intialize the media players
        private var gearSpinningPlayer:MediaPlayer? = null
        private var heavenPlayer:MediaPlayer? = null
        private var animeWowPlayer:MediaPlayer? = null

        private var coinEarnedPlayer:MediaPlayer? = null
        private var coinEarnedPlayer2:MediaPlayer? = null
        private var coinEarnedPlayer3:MediaPlayer? = null

        private var kittenMeowPlayer:MediaPlayer? = null
        private var kittenMeowPlayer2:MediaPlayer? = null
        private var kittenMeowPlayer3:MediaPlayer? = null
        private var kittenDiePlayer:MediaPlayer? = null
        
        private var mozartSonataPlayer: MediaPlayer? = null
        private var chopinPreludePlayer:MediaPlayer? = null

        /*
            Sets the volume on or off
         */
        private var volume:Float = 0f
        fun setVolume(on:Boolean) {
            if (on) {
                volume = 1f
            } else {
                volume = 0f
            }
            gearSpinningPlayer?.setVolume(volume, volume)
            heavenPlayer?.setVolume(volume, volume)
            animeWowPlayer?.setVolume(volume, volume)

            coinEarnedPlayer?.setVolume(volume, volume)
            coinEarnedPlayer2?.setVolume(volume, volume)
            coinEarnedPlayer3?.setVolume(volume, volume)

            kittenMeowPlayer?.setVolume(volume, volume)
            kittenMeowPlayer2?.setVolume(volume, volume)
            kittenMeowPlayer3?.setVolume(volume, volume)
            kittenDiePlayer?.setVolume(volume, volume)
            if ((Volume.isVolumeOn && volume == 1f) || volume == 0f) {
                mozartSonataPlayer?.setVolume(volume, volume)
                chopinPreludePlayer?.setVolume(volume, volume)
            }
        }

        fun setupGearSpinning(context: Context) {
            if (gearSpinningPlayer == null) {
                gearSpinningPlayer = MediaPlayer.create(context, R.raw.gearspinning)
                gearSpinningPlayer!!.setVolume(0.25f, 0.25f)
            }
        }

        fun gearSpinning() {
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
            animeWowPlayer!!.start()
        }

        fun setupCoinEarnedPlayers(context: Context) {
            if (coinEarnedPlayer == null) {
                coinEarnedPlayer = MediaPlayer.create(context, R.raw.coinearned)
                coinEarnedPlayer2 = MediaPlayer.create(context, R.raw.coinearned)
                coinEarnedPlayer3 = MediaPlayer.create(context, R.raw.coinearned)
            }
        }

        fun coinEarned() {
            if (coinEarnedPlayer!!.isPlaying) {
                if (coinEarnedPlayer2!!.isPlaying) {
                    coinEarnedPlayer3!!.start()
                } else {
                    coinEarnedPlayer2!!.start()
                }
            } else {
                coinEarnedPlayer!!.start()
            }
        }

        fun setupKittenMeowPlayer(context: Context) {
            if (kittenMeowPlayer == null) {
                kittenMeowPlayer = MediaPlayer.create(context, R.raw.kittenmeow)
                kittenMeowPlayer2 = MediaPlayer.create(context, R.raw.kittenmeow)
                kittenMeowPlayer3 = MediaPlayer.create(context, R.raw.kittenmeow)
            }
        }

        fun kittenMeow() {
            if (kittenMeowPlayer!!.isPlaying) {
                if (kittenMeowPlayer2!!.isPlaying) {
                    kittenMeowPlayer3!!.start()
                } else {
                    kittenMeowPlayer2!!.start()
                }
            } else {
                kittenMeowPlayer!!.start()
            }
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

        fun isMozartSonataPlaying():Boolean {
            return mozartSonataPlayer!!.isPlaying
        }

        fun mozartSonata(play:Boolean, startOver:Boolean) {
            if (play) {
                if (startOver) {
                    mozartSonataPlayer!!.stop()
                    mozartSonataPlayer!!.prepare()
                    mozartSonataPlayer!!.start()
                } else {
                    mozartSonataPlayer!!.start()
                }
            } else {
                mozartSonataPlayer!!.stop()
            }
            if (!Volume.isVolumeOn) {
                mozartSonataPlayer?.setVolume(0f, 0f)
            }
        }

        fun chopinPrelude(play:Boolean, startOver:Boolean) {
            if (play) {
                if (startOver) {
                    chopinPreludePlayer!!.stop()
                    chopinPreludePlayer!!.prepare()
                    chopinPreludePlayer!!.start()
                } else {
                    chopinPreludePlayer!!.start()
                }
            } else {
                chopinPreludePlayer!!.stop()
            }
            if (!Volume.isVolumeOn) {
                chopinPreludePlayer?.setVolume(0f, 0f)
            }
        }

        fun setupChopinPrelude(context: Context) {
            if (chopinPreludePlayer == null) {
                chopinPreludePlayer = MediaPlayer.create(context, R.raw.chopinprelude)
            }
        }

        /*
            Sets the volume for the music/songs on or off
         */
        fun setMusicVolume(on:Boolean) {
            if (on) {
                chopinPreludePlayer?.setVolume(1f, 1f)
                mozartSonataPlayer?.setVolume(1f, 1f)
            } else {
                chopinPreludePlayer?.setVolume(0f, 0f)
                mozartSonataPlayer?.setVolume(0f, 0f)
            }
        }
    }
}
