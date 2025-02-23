package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType
import com.csd3156.group11.enums.RenderLayers

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
