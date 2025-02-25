/**
 * @file LaserFX.kt
 * @brief  This file contains the prefab for laser fx
 */
package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType

/**
 * @class LaserFX
 * @brief Prefab for a laser effect.
 *
 * LaserFX creates a laser effect that originates from a specified starting position and extends
 * in a given direction over time. The effect lasts for a set duration or until it exceeds a maximum
 * length, at which point it is removed from the world.
 *
 * @param startPosition The starting position of the laser effect.
 * @param direction The normalized direction in which the laser travels.
 * @param speed The speed at which the laser extends.
 */
class LaserFX(private val startPosition: Vector2, private val direction: Vector2, private val speed: Float) : Prefab() {
    public var currentLength = 0f
    private val maxLength = 1000f // Let the laser fly far enough to go off-screen

    override fun Create(world: World): Int {
        ID = world.create()

        val angle = MathUtils.atan2(direction.y, direction.x) * MathUtils.radiansToDegrees

        world.edit(ID)
            .add(TransformComponent(position = startPosition, scale = Vector2(0f, 0.5f), rotation = angle)) // Start small
            .add(SpriteComponent("textures/LaserBullet.png"))
            .add(FXComponent().apply {
                fxType = PowerUpType.LASER_3X
                duration = 5f // Laser lasts for 5 seconds
            })

        return ID
    }

    /**
     * @brief Updates the laser effect.
     *
     * Increases the laser's current length over time based on the specified speed. Once the maximum
     * length is reached, the laser entity is deleted from the world.
     *
     * @param world The game world.
     * @param deltaTime The time elapsed since the last update.
     */
    fun update(world: World, deltaTime: Float) {
        val transform = world.getMapper(TransformComponent::class.java).get(ID)

        if (currentLength < maxLength) {
            currentLength += speed * deltaTime
            transform.scale.x = currentLength // Stretch the laser length over time
        } else {
            world.delete(ID) // Remove when fully extended
        }
    }
}
