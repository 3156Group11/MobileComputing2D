/**
 * @file SoundComponent.kt
 * @brief  Holds the Sound Component and its data members and methods
 */
package com.csd3156.group11.components

import com.artemis.Component

/**
 * @brief Component for managing sound settings and playback paths.
 *
 * The SoundComponent holds information for background music and sound effects,
 * including file paths, volume levels, and flags that indicate whether music or sound effects are enabled.
 */
class SoundComponent : Component() {
    var bgmPath: String? = null
    var sfxPath: String? = null
    var bgmVolume: Float = 1.0f
    var sfxVolume: Float = 1.0f
    var isMusicEnabled: Boolean = true
    var isSfxEnabled: Boolean = true
}
