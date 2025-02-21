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

    // --------------------------------------
    // BOMB data
    // --------------------------------------
    var bombActive: Boolean = false //bomb active
    var bombTimer: Float = 0f   //bomb duration
    var bombPos: Vector2 = Vector2() //bomb pos
    var bombRadius: Float = 0f  //bomb rad

}
