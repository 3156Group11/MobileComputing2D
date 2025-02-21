package com.csd3156.group11.components

import com.artemis.Component
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.enums.PowerUpType

class PowerUpComponent : Component() {
    var type: PowerUpType = PowerUpType.NONE

    // --------------------------------------
    // SHIELD data
    // --------------------------------------
    var hasShield: Boolean = false //shield active
    var shieldBreakEffect: Boolean = false //shield effect.

    // ----------------------------------------
    // BOMB DATA - store multiple bombs at once
    // ----------------------------------------
    data class BombEntry(
        val center: Vector2,  // The position where the bomb was picked up
        var timeLeft: Float,  // Duration the bomb effect stays active
        val radius: Float     // AoE radius
    )
    val bombs = mutableListOf<BombEntry>()

}
