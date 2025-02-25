/**
 * @file PlayerInputComponent.kt
 * @brief  Holds the Player Input Component and its data members and methods
 */
package com.csd3156.group11.components

import com.artemis.Component
import com.badlogic.gdx.math.Vector2

/**
 * @brief Component for capturing player input data.
 *
 * The PlayerInputComponent stores input-related information for the player,
 * such as the tilt direction (e.g. from accelerometer or touch input) and whether a shield power-up is active.
 */
class PlayerInputComponent(
    var tiltDirection: Vector2 = Vector2(),
    var hasShield: Boolean = false
    ): Component()
