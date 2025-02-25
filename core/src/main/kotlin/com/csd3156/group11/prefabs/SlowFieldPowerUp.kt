/**
 * @file SlowFieldPowerUp.kt
 * @brief  This file contains the prefab for slow field power up
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
 * @class SlowFieldPowerUp
 * @brief Prefab for slow field power-up entities.
 *
 * SlowFieldPowerUp spawns a power-up that grants the player a slow field ability.
 * It is randomly positioned within the game world with padding to avoid the screen edges,
 * and includes movement, collision, and rendering components.
 */
class SlowFieldPowerUp : Prefab() {
    override fun Create(world: World):Int {
        ID = world.create()

        // Screen-normalized spawn dimensions
        val screenWidth = 35f
        val screenHeight = 35f * Globals.scrHeight / Globals.scrWidth

        // Generate random spawn position within screen bounds with padding
        val randomX = MathUtils.random(2f, screenWidth - 2f)  // 2 units of padding
        val randomY = MathUtils.random(2f, screenHeight - 2f)
        val spawnPosition = Vector2(randomX, randomY)

        world.edit(ID)
            .add(TransformComponent(
                position = spawnPosition,
                scale = Vector2(1.5f, 1.5f)  // Consistent size
            ))
            .add(ColliderComponent(radius = 1f))  // Consistent collision detection
            .add(SpriteComponent("textures/Time.png", RenderLayers.Powerup))
            .add(PowerUpComponent().apply { type = PowerUpType.SLOW_FIELD })
            .add(VelocityComponent(Vector2(MathUtils.random(-5f, 5f), MathUtils.random(-5f, 5f))))  // Small random movement
            .add(TagComponent(Tag.POWERUP))

        println("SlowFieldPowerUp created with ID: $ID at position: $spawnPosition")
        return ID
    }
}
