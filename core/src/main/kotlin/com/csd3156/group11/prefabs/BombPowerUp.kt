// BombPowerUp.kt
package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType
import com.csd3156.group11.enums.Tag

class BombPowerUp : Prefab() {
    override fun Create(world: World) {
        ID = world.create()
        world.edit(ID)
            .add(TransformComponent(
                position = Vector2(MathUtils.random(100f, 700f), MathUtils.random(100f, 300f)),
                scale = Vector2(1.5f, 1.5f)
            ))
            .add(ColliderComponent(radius = 10f))
            .add(SpriteComponent("textures/Bomb.png"))
            .add(PowerUpComponent().apply { type = PowerUpType.BOMB })
            .add(VelocityComponent(Vector2(MathUtils.random(-50f, 50f), MathUtils.random(-50f, 50f))))
            .add(TagComponent(Tag.POWERUP))

        println("BombPowerUp created with ID: $ID")
    }
}
