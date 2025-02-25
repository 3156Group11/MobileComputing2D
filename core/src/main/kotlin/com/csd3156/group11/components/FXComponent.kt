/**
 * @file FXComponent.kt
 * @brief  Holds the FX Component and its data members and methods
 */
package com.csd3156.group11.components

import com.artemis.Component
import com.csd3156.group11.enums.PowerUpType

/**
 * @brief Component for special effects related to power-ups.
 *
 * The FXComponent designates that an entity has an associated visual or other special effect,
 * typically linked to a power-up (such as a shield). It stores the type of effect via fxType,
 * the ID of an entity to follow (for instance, the player for a shield effect), and the duration of the effect.
 */
class FXComponent : Component() {
    var fxType: PowerUpType = PowerUpType.NONE

    //specify an entity this effect should follow (like the player for a shield)
    var followEntityId: Int = -1

    var duration: Float = 0f
}
