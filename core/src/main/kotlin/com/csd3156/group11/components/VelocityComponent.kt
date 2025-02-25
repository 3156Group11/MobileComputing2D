/**
 * @file VelocityComponent.kt
 * @brief  Holds the Velocity Component and its data members and methods
 */
package com.csd3156.group11.components

import com.artemis.Component
import com.badlogic.gdx.math.Vector2

/**
 * @brief Stores the velocity and acceleration of an entity.
 *
 * This component provides dynamic movement information for entities, enabling
 * systems such as physics and movement controllers to update positions.
 */
class VelocityComponent(
    var velocity: Vector2 = Vector2(),
    var acceleration: Vector2 = Vector2()
)
    : Component()
