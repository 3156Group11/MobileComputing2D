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
    var invulnerability: Float = 0f //invulnerability timer after shield breaks
    var shieldFXEntityId: Int = -1

    // ----------------------------------------
    // BOMB DATA - store multiple bombs at once
    // ----------------------------------------
    data class BombEntry(
        val center: Vector2,  // The position where the bomb was picked up
        var timeLeft: Float,  // Duration the bomb effect stays active
        val radius: Float    // AoE radius
    )
    val bombs = mutableListOf<BombEntry>()

    // ----------------------------------------
    //  Lightning Data  - store multiple Lightning at once
    // ----------------------------------------
    data class LightningEntry(
        val targetPosition: Vector2,


    )

    val lightning = mutableListOf<LightningEntry>()

    // ----------------------------------------
    //  Slow Field Data  - store multiple slow fields at once
    // ----------------------------------------
    data class SlowFieldEntry(
        val  position: Vector2,
        val timeLeft: Float,
        val direction: Vector2
    )

    val slowFields = mutableListOf<SlowFieldEntry>()
}
