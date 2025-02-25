/**
 * @file TransformComponent.kt
 * @brief  Holds the Transform Component and its data members and methods
 */
package com.csd3156.group11.components

import com.artemis.Component
import com.badlogic.gdx.math.Vector2

/**
 * @brief Represents the position, rotation, and scale of an entity in world space.
 *
 * This component provides the basic spatial properties needed for rendering and physics.
 */
class TransformComponent(
    var position: Vector2 = Vector2(),
    var rotation: Float = 0f,
    var scale : Vector2 = Vector2()) :
    Component()
