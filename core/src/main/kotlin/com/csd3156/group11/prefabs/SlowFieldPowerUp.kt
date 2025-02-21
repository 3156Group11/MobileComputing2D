package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType
import com.csd3156.group11.enums.RenderLayers
import com.csd3156.group11.enums.Tag

class SlowFieldPowerUp : Prefab() {
    override fun Create(world: World) {
        ID = world.create()
        world.edit(ID)
            .add(TransformComponent(
                position = Vector2(
                    MathUtils.random(100f, 700f),  // Random X position
                    MathUtils.random(100f, 300f)   // Random Y position
                )
            ))
            .add(ColliderComponent(radius = 10f))  // Collision radius for pickup detection
            .add(SpriteComponent("textures/Time.png", RenderLayers.Powerup))  // Visual representation
            .add(PowerUpComponent().apply { type = PowerUpType.SLOW_FIELD })  // Assign slow field type
            .add(VelocityComponent(Vector2(MathUtils.random(-50f, 50f), MathUtils.random(-50f, 50f))))  // Random movement for collectible
            .add(TagComponent(Tag.POWERUP))  // Mark as a power-up entity

        println("SlowFieldPowerUp created with ID: $ID at random position.")
    }
}
