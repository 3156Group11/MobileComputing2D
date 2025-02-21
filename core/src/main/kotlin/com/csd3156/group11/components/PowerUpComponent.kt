package com.csd3156.group11.components

import com.artemis.Component
import com.csd3156.group11.enums.PowerUpType

class PowerUpComponent: Component() {
    var type: PowerUpType = PowerUpType.NONE
    var hasShield: Boolean = false; // flag for player shield
    var shieldBreakEffect: Boolean = false; // flag for break effect

}
