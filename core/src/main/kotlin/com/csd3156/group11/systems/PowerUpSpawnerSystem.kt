/**
 * @file PowerUpSpawnerSystem.kt
 * @brief Manages the spawning of power-up entities in the game.
 */
package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.badlogic.gdx.math.MathUtils
import com.csd3156.group11.components.PowerUpComponent
import com.csd3156.group11.components.TagComponent
import com.csd3156.group11.enums.Tag
import com.csd3156.group11.prefabs.*
import com.csd3156.group11.resources.Globals
import com.csd3156.group11.enums.GameState

/**
 * @class PowerUpSpawnerSystem
 * @brief Handles the automatic spawning of power-ups during gameplay.
 *
 * Ensures that a maximum of 3 power-ups exist at any given time in the game.
 * Power-ups are spawned randomly based on predefined types when there are fewer than 3 active.
 */
class PowerUpSpawnerSystem : BaseEntitySystem(
    Aspect.all(PowerUpComponent::class.java, TagComponent::class.java)
) {
    private lateinit var powerUpMapper: ComponentMapper<PowerUpComponent>
    private lateinit var tagMapper: ComponentMapper<TagComponent>

    private val maxPowerUps = 3  // Max number of active power-ups on screen

    /**
     * @brief Processes the power-up spawning logic.
     *
     * Ensures that power-ups are only spawned when the game is active and not paused or in a death state.
     * Counts the current number of active power-ups and spawns new ones if the count is below the maximum limit.
     */
    override fun processSystem() {
        // Prevent spawning if game is not active
        if (Globals.IsStarting) return
        if (Globals.currentState != GameState.GAME_STAGE) return
        if (Globals.deathScreen) return
        if (Globals.isPausing) return

        // Count current power-ups on the field
        val entities = subscription.entities
        var currentPowerUps = 0

        for (i in 0 until entities.size()) {
            val entity = entities[i]
            val tag = tagMapper.get(entity)
            if (tag.tag == Tag.POWERUP) {
                currentPowerUps++
            }
        }

        // If there are fewer than 3 power-ups, spawn until there are 3
        while (currentPowerUps < maxPowerUps) {
            spawnRandomPowerUp()
            currentPowerUps++
        }
    }

    /**
     * @brief Spawns a random power-up in the game.
     *
     * Selects a random power-up type from Shield, Bomb, Lightning, and Slow Field,
     * then creates and adds it to the world.
     */
    private fun spawnRandomPowerUp() {
        // Generate a random number between 0 and 4 for random power-up type
        val randomPowerUp = MathUtils.random(0, 3)

        // Spawn corresponding power-up based on random number
        when (randomPowerUp) {
            0 -> {
                println("Spawning Shield Power-Up")
                val shieldPowerUp = ShieldPowerUp()
                shieldPowerUp.Create(world)
            }
            1 -> {
                println("Spawning Bomb Power-Up")
                val bombPowerUp = BombPowerUp()
                bombPowerUp.Create(world)
            }
            2 -> {
                println("Spawning Lightning Power-Up")
                val lightningPowerUp = LightningPowerUp()
                lightningPowerUp.Create(world)
            }
            3 -> {
                println("Spawning Slow Field Power-Up")
                val slowFieldPowerUp = SlowFieldPowerUp()
                slowFieldPowerUp.Create(world)
            }
        }
    }
}
