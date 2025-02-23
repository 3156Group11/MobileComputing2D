package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType
import com.csd3156.group11.enums.RenderLayers

class BombFX(private val detonationPos: Vector2, private val radius: Float) : Prefab() {
    override fun Create(world: World):Int {
        ID = world.create()

        val adjustedPosition = detonationPos.cpy().sub(radius, radius) // Adjust to center correctly

        world.edit(ID)
            .add(TransformComponent(
                position = adjustedPosition,  // Center the FX
                scale = Vector2(radius * 2, radius * 2)
            ))
            .add(SpriteComponent("textures/Circle.png", RenderLayers.FX))
            .add(FXComponent().apply {
                fxType = PowerUpType.BOMB
                duration = 3f  // Duration of the FX
            })

        println("🎆 BombFX created at $adjustedPosition with proper radius $radius")
        return ID

    }
}
