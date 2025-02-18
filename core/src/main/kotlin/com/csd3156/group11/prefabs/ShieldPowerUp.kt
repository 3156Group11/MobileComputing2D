package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*

class ShieldPowerUp : Prefab() {
    override fun Create(world: World) {
        ID = world.create()
        world.edit(ID)
            .add(TransformComponent(Vector2(MathUtils.random(100f, 700f), MathUtils.random(100f, 300f)))) // Random spawn position
            .add(ColliderComponent(radius = 16f))
            .add(SpriteComponent("textures/Shield.png"))
            .add(PowerUpComponent().apply { hasShield = true })
            .add(VelocityComponent(Vector2(MathUtils.random(-50f, 50f), MathUtils.random(-50f, 50f)))) // Random initial velocity
    }
}
