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
            .add(TransformComponent(position = detonationPos.cpy()))
            .add(SpriteComponent("textures/Circle.png", RenderLayers.FX).apply {
                width = 100f
                height = 100f
            })
            .add(FXComponent().apply {
                fxType = PowerUpType.BOMB
                // Set a duration for how long the bomb FX stays visible (e.g., 3 seconds)
                duration = 3f
            })
            .add(ColliderComponent(radius = 50f))


        println("BombFX created with ID: $ID at position $detonationPos")
    }
}
