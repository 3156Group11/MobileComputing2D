/**
 * @file SlowField.kt
 * @brief  This file contains the prefab for slow field fx
 */
package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType
import com.csd3156.group11.enums.RenderLayers

/**
 * @class SlowFieldFX
 * @brief Prefab for slow field visual effects.
 *
 * SlowFieldFX creates a horizontal beam effect (using a texture "textures/TimeBeam.png")
 * that represents a slow field power-up. The beam moves with a specified speed and is used for
 * collision detection as well.
 *
 * @param startPosition The starting position of the slow field effect.
 * @param direction The direction in which the beam moves.
 * @param speed The speed at which the beam travels.
 */
class SlowFieldFX(private val startPosition: Vector2, private val direction: Vector2, private val speed: Float) : Prefab() {
    override fun Create(world: World): Int {
        ID = world.create()

        world.edit(ID)
            .add(TransformComponent(
                position = startPosition.cpy(),
                scale = Vector2(3.0f, 0.2f)  // Horizontal beam visual
            ))
            .add(SpriteComponent("textures/TimeBeam.png", RenderLayers.FX))
            .add(FXComponent().apply {
                fxType = PowerUpType.SLOW_FIELD
                duration = 5f  // Beam lasts for 5 seconds
            })
            .add(VelocityComponent(direction.cpy().scl(speed)))  // Set custom speed
            .add(ColliderComponent(radius = 5f))  // Slow field collision detection

        println("🌀 SlowFieldFX created at position: $startPosition, moving at speed: $speed")
        return ID
    }
}
