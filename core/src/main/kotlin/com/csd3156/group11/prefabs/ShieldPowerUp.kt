package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*

class ShieldPowerUp : Prefab() {
    override fun Create(world: World) {
        ID = world.create()
        world.edit(ID)
            .add(TransformComponent(
                position = Vector2(MathUtils.random(100f, 700f), MathUtils.random(100f, 300f)),
                scale = Vector2(0.5f, 0.5f) // Reduce size to 50%
            ))
            .add(ColliderComponent(radius = 8f)) // Adjust collider size accordingly
            .add(SpriteComponent("textures/Shield.png"))
            .add(PowerUpComponent().apply { hasShield = true })
            .add(VelocityComponent(Vector2(MathUtils.random(-50f, 50f), MathUtils.random(-50f, 50f))))
    }
}
