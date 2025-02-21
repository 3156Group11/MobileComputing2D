package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType
import com.csd3156.group11.enums.RenderLayers

class LightningFX(private val strikePosition: Vector2) : Prefab() {
    override fun Create(world: World) {
        ID = world.create()
        world.edit(ID)
            .add(TransformComponent(
                position = strikePosition.cpy(),
                scale = Vector2(1.5f, 1.5f)  // Adjust scale as needed for visual size
            ))
            .add(SpriteComponent("textures/Circle.png", RenderLayers.FX))
            .add(FXComponent().apply {
                fxType = PowerUpType.LIGHTNING
                duration = 1.5f  // Duration for how long the FX should last (e.g., 1.5 seconds)
            })

        println("LightningFX created with ID: $ID at position $strikePosition")
    }
}
