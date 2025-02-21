package com.csd3156.group11.components

import com.artemis.Component

class SoundComponent : Component() {
    var bgmPath: String? = null
    var sfxPath: String? = null
    var bgmVolume: Float = 1.0f
    var sfxVolume: Float = 1.0f
    var isMusicEnabled: Boolean = true
    var isSfxEnabled: Boolean = true
}
