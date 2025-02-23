package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType

class LaserFX(private val startPosition: Vector2, private val direction: Vector2, private val length: Float) : Prefab() {
    override fun Create(world: World): Int {
        ID = world.create()

        val endPosition = startPosition.cpy().add(direction.scl(length))

        world.edit(ID)
            .add(TransformComponent(position = startPosition, scale = Vector2(length, 0.5f)))
            .add(SpriteComponent("textures/LaserBullet.png")) // Ensure texture is assigned
            .add(FXComponent().apply {
                fxType = PowerUpType.LASER_3X
                duration = 0.5f
            })

        println("LaserFX created from $startPosition to $endPosition")
        return ID
    }
}
