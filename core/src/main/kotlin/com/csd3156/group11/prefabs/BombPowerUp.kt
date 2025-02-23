package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType
import com.csd3156.group11.enums.RenderLayers
import com.csd3156.group11.enums.Tag
import com.csd3156.group11.resources.Globals

class BombPowerUp : Prefab() {
    override fun Create(world: World): Int {
        ID = world.create()

        // Calculate screen width and height using aspect ratio
        val screenWidth = 35f
        val screenHeight = 35f * Globals.scrHeight / Globals.scrWidth

        // Generate a random position within screen boundaries with padding
        val randomX = MathUtils.random(2f, screenWidth - 2f)  // Leave 2 units padding from screen edges
        val randomY = MathUtils.random(2f, screenHeight - 2f)
        val spawnPosition = Vector2(randomX, randomY)

        // Create the bomb power-up entity
        world.edit(ID)
            .add(TransformComponent(
                position = spawnPosition, // Use screen-normalized spawn position
                scale = Vector2(1.5f, 1.5f) // Consistent scale
            ))
            .add(ColliderComponent(radius = 1f)) // Match collider size to other power-ups
            .add(SpriteComponent("textures/Bomb.png", RenderLayers.Powerup))
            .add(PowerUpComponent().apply { type = PowerUpType.BOMB })
            .add(VelocityComponent(Vector2(MathUtils.random(-5f, 5f), MathUtils.random(-5f, 5f)))) // Small random movement
            .add(TagComponent(Tag.POWERUP))

        println("BombPowerUp created with ID: $ID at position: $spawnPosition")
        return ID
    }
}
