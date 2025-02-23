package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType
import com.csd3156.group11.enums.RenderLayers
import com.csd3156.group11.enums.Tag
import com.csd3156.group11.resources.Globals

class LightningPowerUp : Prefab() {
    override fun Create(world: World):Int {
        ID = world.create()

        // Calculate screen width and height based on aspect ratio
        val screenWidth = 35f
        val screenHeight = 35f * Globals.scrHeight / Globals.scrWidth

        // Random spawn position within screen bounds with padding
        val randomX = MathUtils.random(2f, screenWidth - 2f)
        val randomY = MathUtils.random(2f, screenHeight - 2f)
        val spawnPosition = Vector2(randomX, randomY)

        world.edit(ID)
            .add(TransformComponent(
                position = spawnPosition, // Use normalized screen space
                scale = Vector2(1.5f, 1.5f)  // Consistent scale with other power-ups
            ))
            .add(ColliderComponent(radius = 1f))  // Consistent collider radius
            .add(SpriteComponent("textures/Lightning.png", RenderLayers.Powerup))
            .add(PowerUpComponent().apply { type = PowerUpType.LIGHTNING })
            .add(VelocityComponent(Vector2(MathUtils.random(-5f, 5f), MathUtils.random(-5f, 5f))))  // Small random movement
            .add(TagComponent(Tag.POWERUP))

        println("LightningPowerUp created with ID: $ID at position: $spawnPosition")
        return ID
    }
}
