package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType
import com.csd3156.group11.enums.RenderLayers

class SlowFieldFX(private val startPosition: Vector2, private val direction: Vector2) : Prefab() {
    override fun Create(world: World) {
        ID = world.create()
        world.edit(ID)
            .add(TransformComponent(
                position = startPosition.cpy(),
                scale = Vector2(3.0f, 0.2f)  // Horizontal bar effect
            ))
            .add(SpriteComponent("textures/circle.png", RenderLayers.FX))
            .add(FXComponent().apply {
                fxType = PowerUpType.SLOW_FIELD
                duration = 5f  // Active for 5 seconds
            })
            .add(VelocityComponent(direction.cpy().scl(100f)))  // Move in the specified direction
            .add(ColliderComponent(radius = 5f))  // Collision detection

        println("SlowFieldFX created at position: $startPosition, moving in direction: $direction")
    }
}
