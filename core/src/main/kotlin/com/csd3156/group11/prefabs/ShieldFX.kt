package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.createCircleTexture
import com.csd3156.group11.enums.PowerUpType
import com.csd3156.group11.enums.RenderLayers

class ShieldFX(private val followId: Int) : Prefab() {
    override fun Create(world: World):Int {
        ID = world.create()

        val texture = createCircleTexture(256, Color.BLUE)

        world.edit(ID)
            .add(TransformComponent(
                position = Vector2(0f, 0f), // Position will update in FXSystem
                scale = Vector2(1.5f, 1.5f)  // Adjust the size for visibility
            ))
            .add(SpriteComponent("textures/Circle.png", RenderLayers.FX))  // Ensure this texture exists
            .add(FXComponent().apply {
                fxType = PowerUpType.SHIELD
                followEntityId = followId
                //duration = 9999f  // Set long duration for testing visibility
            })
        world.getMapper(SpriteComponent::class.java).get(ID).region = texture

        // Link FX entity ID to player's PowerUpComponent for later removal
        val playerPowerUp = world.getEntity(followId).getComponent(PowerUpComponent::class.java)
        playerPowerUp.shieldFXEntityId = ID

        println("🛡️ ShieldFX created with ID: $ID following entity $followId")
        return ID
    }
}
