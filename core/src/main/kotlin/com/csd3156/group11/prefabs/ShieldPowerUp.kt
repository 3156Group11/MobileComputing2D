package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType
import com.csd3156.group11.enums.RenderLayers
import com.csd3156.group11.enums.Tag
import com.csd3156.group11.resources.Globals

class ShieldPowerUp : Prefab() {
    override fun Create(world: World):Int {
        ID = world.create()

        // Calculate screen width and height based on aspect ratio
        val screenWidth = 35f
        val screenHeight = 35f * Globals.scrHeight / Globals.scrWidth

        // Random position within screen bounds, with padding to avoid edges
        val randomX = MathUtils.random(2f, screenWidth - 2f)  // Padding of 2 units from left/right
        val randomY = MathUtils.random(2f, screenHeight - 2f)  // Padding of 2 units from top/bottom
        val spawnPosition = Vector2(randomX, randomY)

        world.edit(ID)
            .add(TransformComponent(
                position = spawnPosition, // Randomly generated position
                scale = Vector2(1.5f, 1.5f) // Consistent size
            ))
            .add(ColliderComponent(radius = 1f)) // Collider radius consistent with player
            .add(SpriteComponent("textures/Shield.png", RenderLayers.Powerup))
            .add(PowerUpComponent().apply { type = PowerUpType.SHIELD })
            .add(VelocityComponent(Vector2(MathUtils.random(-5f, 5f), MathUtils.random(-5f, 5f)))) // Small random movement
            .add(TagComponent(Tag.POWERUP))

        println("ShieldPowerUp created with ID: $ID at position: $spawnPosition")
        return ID
    }
}
