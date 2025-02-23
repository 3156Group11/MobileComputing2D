// LaserPowerUp.kt
package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType
import com.csd3156.group11.enums.RenderLayers
import com.csd3156.group11.enums.Tag
import com.csd3156.group11.resources.Globals

class LaserPowerUp : Prefab() {
    override fun Create(world: World): Int {
        ID = world.create()

        // Calculate spawn boundaries
        val screenWidth = 35f
        val screenHeight = 35f * Globals.scrHeight / Globals.scrWidth
        val randomX = MathUtils.random(2f, screenWidth - 2f)
        val randomY = MathUtils.random(2f, screenHeight - 2f)
        val spawnPosition = Vector2(randomX, randomY)

        // Create laser power-up entity
        world.edit(ID)
            .add(TransformComponent(position = spawnPosition, scale = Vector2(1.5f, 1.5f)))
            .add(ColliderComponent(radius = 1f))
            .add(SpriteComponent("textures/Laser.png", RenderLayers.Powerup))
            .add(PowerUpComponent().apply { type = PowerUpType.LASER_3X })
            .add(VelocityComponent(Vector2(MathUtils.random(-3f, 3f), MathUtils.random(-3f, 3f))))
            .add(TagComponent(Tag.POWERUP))

        println("LaserPowerUp created with ID: $ID at position: $spawnPosition")
        return ID
    }
}
