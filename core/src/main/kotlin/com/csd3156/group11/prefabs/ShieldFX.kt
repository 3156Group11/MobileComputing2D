package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType
import com.csd3156.group11.enums.RenderLayers

class ShieldFX(private val followId: Int) : Prefab() {
    override fun Create(world: World) {
        ID = world.create()
        world.edit(ID)
            .add(TransformComponent(position = Vector2(0f, 0f))) // will be updated by FXSystem
            .add(SpriteComponent("textures/Circle.png", RenderLayers.FX).apply {
                // Set appropriate size for your shield effect
                width = 100f
                height = 100f
            })
            .add(FXComponent().apply {
                fxType = PowerUpType.SHIELD
                followEntityId = followId
            })
            .add(ColliderComponent(radius = 25f))

        println("ShieldFX created with ID: $ID following entity $followId")
    }
}
