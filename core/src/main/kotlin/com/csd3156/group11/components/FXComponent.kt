package com.csd3156.group11.components

import com.artemis.Component
import com.csd3156.group11.enums.PowerUpType

class FXComponent : Component() {
    var fxType: PowerUpType = PowerUpType.NONE

    //specify an entity this effect should follow (like the player for a shield)
    var followEntityId: Int = -1

    var duration: Float = 0f
}
