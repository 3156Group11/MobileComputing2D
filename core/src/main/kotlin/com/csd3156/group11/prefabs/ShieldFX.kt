package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType
import com.csd3156.group11.enums.RenderLayers

class ShieldFX(private val followId: Int) : Prefab() {
    override fun Create(world: World):Int {
        ID = world.create()
        world.edit(ID)
            .add(TransformComponent(
                position = Vector2(0f, 0f), // updated by fxsystem
                scale = Vector2(1.5f, 1.5f))// Reduce size to 50%
                )
            .add(SpriteComponent("textures/Circle.png", RenderLayers.FX))
            .add(FXComponent().apply {
                fxType = PowerUpType.SHIELD
                followEntityId = followId
            })

        println("ShieldFX created with ID: $ID following entity $followId")
        return ID
    }
}
