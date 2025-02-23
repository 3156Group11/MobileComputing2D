package com.csd3156.group11.systems

import com.artemis.BaseSystem
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.csd3156.group11.assetManager

class SoundSystem : BaseSystem() {

    private var currentBGM: Music? = null // store a reference to the currently playing BGM
    private var currentBGMFilePath: String? = null // Store the current file path bgm asset
    private var hasPlayedDeathSFX = false

    fun playBGM(filePath: String, loop: Boolean = true, volume: Float = 1.0f) {
        // Check to see if BGM file is withn the assetManager
        if (!assetManager.system().isLoaded(filePath, Music::class.java)) {
            println("BGM not loaded yet: $filePath")
            return
        }

        // Check if the same BGM is already playing
        if (currentBGM != null && currentBGM?.isPlaying == true && currentBGMFilePath == filePath) {
            println("BGM already playing: $filePath")
            return // Do nothing, let it keep playing
        }

        // Stop playuing the previous BGM
        stopBGM()

        val bgm = assetManager.system().get(filePath, Music::class.java)
        bgm.isLooping = loop
        bgm.volume = volume
        bgm.play()
        currentBGM = bgm
        currentBGMFilePath = filePath
    }

    fun stopBGM() {
        currentBGM?.stop()
        currentBGM = null
        currentBGMFilePath = null
    }

    fun playSFX(filePath: String, volume: Float = 1.0f) {
        if (!assetManager.system().isLoaded(filePath, Sound::class.java)) {
            println("SFX not loaded yet: $filePath")
            return
        }

        if (filePath.contains("audio/sfx/fx_Player_Death.wav") && hasPlayedDeathSFX) {
            println("🔄 Player death SFX already played, skipping...")
            return
        }

        val sfx = assetManager.system().get(filePath, Sound::class.java)
        sfx.play(volume)

        // ✅ Mark death sound as played
        if (filePath.contains("audio/sfx/fx_Player_Death.wav")) {
            hasPlayedDeathSFX = true
        }
    }

    fun resetDeathSFXFlag() {
        hasPlayedDeathSFX = false // ✅ Call this when restarting the game
    }

    fun setBGMVolume(volume: Float) {
        currentBGM?.volume = volume
    }

    fun toggleBGM(enable: Boolean) {
        if (enable) {
            currentBGM?.play()
        } else {
            currentBGM?.pause()
        }
    }

    // Need to include this
    override fun processSystem() {}
}
