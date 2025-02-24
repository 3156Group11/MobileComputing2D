package com.csd3156.group11.systems

import com.artemis.BaseSystem
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.csd3156.group11.assetManager

/**
 * @class SoundSystem
 * @brief Handles background music (BGM) and sound effects (SFX) within the game.
 *
 * The SoundSystem manages playback, stopping, and volume control for both BGM and SFX.
 * It ensures that the same BGM does not restart unnecessarily and prevents specific SFX from
 * playing repeatedly (e.g., player death sound).
 */
class SoundSystem : BaseSystem() {

    private var currentBGM: Music? = null // store a reference to the currently playing BGM
    private var currentBGMFilePath: String? = null // Store the current file path bgm asset
    private var hasPlayedDeathSFX = false // This is to ensure the specific sound is not played repeatedly.

    /**
     * @brief Plays a background music (BGM) file.
     *
     * If the same BGM is already playing, it will not restart.
     *
     * @param filePath The file path of the BGM to be played.
     * @param loop Whether the BGM should loop (default: true).
     * @param volume The volume level of the BGM (default: 1.0).
     */
    fun playBGM(filePath: String, loop: Boolean = true, volume: Float = 1.0f) {
        // Check to see if BGM file is withn the assetManager
        if (!assetManager.system().isLoaded(filePath, Music::class.java)) {
            println("BGM not loaded yet: $filePath")
            return
        }

        // Check if the same BGM is already playing
        if (currentBGM != null && currentBGM?.isPlaying == true && currentBGMFilePath == filePath) {
            //println("BGM already playing: $filePath")
            return // Do nothing, let it keep playing
        }

        // Stop playing the previous BGM
        stopBGM()

        val bgm = assetManager.system().get(filePath, Music::class.java)
        bgm.isLooping = loop
        bgm.volume = volume
        bgm.play()
        currentBGM = bgm
        currentBGMFilePath = filePath
    }

    /**
     * @brief Stops the currently playing background music (BGM).
     */
    fun stopBGM() {
        currentBGM?.stop()
        currentBGM = null
        currentBGMFilePath = null
    }

    /**
     * @brief Plays a sound effect (SFX).
     *
     * If the sound effect is not loaded, it will not play.
     * Prevents multiple instances of the player death sound effect from playing.
     *
     * @param filePath The file path of the SFX to be played.
     * @param volume The volume level of the SFX (default: 1.0).
     */
    fun playSFX(filePath: String, volume: Float = 1.0f) {
        if (!assetManager.system().isLoaded(filePath, Sound::class.java)) {
            println("SFX not loaded yet: $filePath")
            return
        }

        if (filePath.contains("audio/sfx/fx_Player_Death.wav") && hasPlayedDeathSFX) {
            //println("🔄 Player death SFX already played, skipping...")
            return
        }

        val sfx = assetManager.system().get(filePath, Sound::class.java)
        sfx.play(volume)

        // Toggle the death sound flag
        if (filePath.contains("audio/sfx/fx_Player_Death.wav")) {
            hasPlayedDeathSFX = true
        }
    }

    /**
     * @brief Resets the flag for the player death sound effect.
     *
     * This should be called when restarting the game to allow the death sound to be played again.
     */
    fun resetDeathSFXFlag() {
        hasPlayedDeathSFX = false // ✅ Call this when restarting the game
    }

    /**
     * @brief ECS-required method. No continuous processing is needed for this system.
     */
    override fun processSystem() {}
}
