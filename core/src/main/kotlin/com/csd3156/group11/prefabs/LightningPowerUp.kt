/**
 * @file LightningPowerUp.kt
 * @brief  This file contains the prefab for Lightning power up
 */
package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType
import com.csd3156.group11.enums.RenderLayers
import com.csd3156.group11.enums.Tag
import com.csd3156.group11.resources.Globals

/**
 * @class LightningPowerUp
 * @brief Prefab for the lightning power-up entity.
 *
 * The LightningPowerUp prefab spawns a power-up entity that grants the player a triple-laser
 * attack. It randomly positions the entity within safe boundaries of the game world, and assigns
 * necessary components for collision, rendering, and movement.
 */
class LightningPowerUp : Prefab() {
    override fun Create(world: World): Int {
        ID = world.create()

        // Calculate screen width and height based on aspect ratio
        val screenWidth = 35f
        val screenHeight = 35f * Globals.scrHeight / Globals.scrWidth

        // Random spawn position within screen bounds
        val randomX = MathUtils.random(2f, screenWidth - 2f)
        val randomY = MathUtils.random(2f, screenHeight - 2f)
        val spawnPosition = Vector2(randomX, randomY)

        // Spawn the power-up without triggering any effects yet
        world.edit(ID)
            .add(TransformComponent(
                position = spawnPosition,
                scale = Vector2(1.5f, 1.5f)
            ))
            .add(ColliderComponent(radius = 1f))
            .add(SpriteComponent("textures/Lightning.png", RenderLayers.Powerup))
            .add(PowerUpComponent().apply { type = PowerUpType.LIGHTNING })
            .add(VelocityComponent(Vector2(MathUtils.random(-5f, 5f), MathUtils.random(-5f, 5f))))
            .add(TagComponent(Tag.POWERUP))

        println("⚡ LightningPowerUp spawned with ID: $ID at position: $spawnPosition")
        return ID
    }
}
