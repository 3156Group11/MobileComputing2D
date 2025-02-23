package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType

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
