package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType
import com.csd3156.group11.enums.RenderLayers

class BombFX(private val detonationPos: Vector2) : Prefab() {
    override fun Create(world: World) {
        ID = world.create()
        world.edit(ID)
            .add(TransformComponent(
                position = detonationPos.cpy(),
                scale = Vector2(2.5f, 2.5f))
            )
            .add(SpriteComponent("textures/Circle.png", RenderLayers.FX))
            .add(FXComponent().apply {
                fxType = PowerUpType.BOMB
                // Set a duration for how long the bomb FX stays visible (e.g., 3 seconds)
                duration = 3f
            })


        println("BombFX created with ID: $ID at position $detonationPos")
    }
}
