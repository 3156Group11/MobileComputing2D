/**
 * @file ShieldSystem.kt
 * @brief Handles shield mechanics for the player, including collision detection and shield effects.
 */
package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.csd3156.group11.components.PlayerInputComponent
import com.csd3156.group11.components.PowerUpComponent
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.ColliderComponent
import com.csd3156.group11.components.TransformComponent
import com.badlogic.gdx.math.Vector2

/**
 * @class ShieldSystem
 * @brief Manages the player's shield, detecting collisions with enemies and applying effects.
 *
 * This system ensures that when the player has an active shield, it absorbs enemy hits.
 * Once the shield is broken, the player gains temporary invulnerability before being vulnerable again.
 */
class ShieldSystem : BaseEntitySystem(
    Aspect.all(PowerUpComponent::class.java, PlayerInputComponent::class.java, TransformComponent::class.java)
) {
    private lateinit var powerUpMapper: ComponentMapper<PowerUpComponent>
    private lateinit var transformMapper: ComponentMapper<TransformComponent>
    private lateinit var colliderMapper: ComponentMapper<ColliderComponent>
    private lateinit var enemyMapper: ComponentMapper<EnemyComponent>

    /**
     * @brief Processes shield mechanics for the player.
     *
     * If the player's shield is active, this function detects collisions with enemies.
     * When a collision occurs, the shield breaks, a temporary invulnerability window is applied,
     * and any shield visual effects are removed.
     */
    override fun processSystem() {
        val playerEntities = subscription.entities

        if (playerEntities.isEmpty()) return

        val playerId = playerEntities[0]
        val playerPowerUp = powerUpMapper[playerId]
        val playerTransform = transformMapper[playerId]
        val playerCollider = colliderMapper[playerId]

        // Handle shield cooldown (temporary invincibility after shield breaks)
        if (playerPowerUp.invulnerability > 0f) {
            playerPowerUp.invulnerability -= world.delta
            return
        }

        if (!playerPowerUp.hasShield) return // Skip if shield is not active

        // Check for collisions with enemies
        val enemyEntities = world.aspectSubscriptionManager.get(
            Aspect.all(EnemyComponent::class.java, TransformComponent::class.java, ColliderComponent::class.java)
        ).entities

        for (i in 0 until enemyEntities.size()) {
            val enemyId = enemyEntities[i]
            val enemyTransform = transformMapper[enemyId]
            val enemyCollider = colliderMapper[enemyId]

            if (isColliding(playerTransform, playerCollider, enemyTransform, enemyCollider)) {
                // Shield absorbs the hit and breaks
                playerPowerUp.hasShield = false
                playerPowerUp.shieldBreakEffect = true
                playerPowerUp.invulnerability = 1.0f // Add a 1-second invincibility window

                // Remove Shield FX immediately
                if (playerPowerUp.shieldFXEntityId != -1) {
                    world.delete(playerPowerUp.shieldFXEntityId)
                    playerPowerUp.shieldFXEntityId = -1 // Reset the FX ID
                }

                println(" Shield absorbed the hit, broke, and FX has been removed.")
                return
            }
        }
    }

    /**
     * @brief Checks for collisions between the player and an enemy.
     *
     * Determines if the player and enemy are within a collision radius based on their respective colliders.
     *
     * @param playerTransform The transform component of the player.
     * @param playerCollider The collider component of the player.
     * @param enemyTransform The transform component of the enemy.
     * @param enemyCollider The collider component of the enemy.
     * @return `true` if the player and enemy are colliding, otherwise `false`.
     */
    private fun isColliding(
        playerTransform: TransformComponent, playerCollider: ColliderComponent,
        enemyTransform: TransformComponent, enemyCollider: ColliderComponent
    ): Boolean {
        val distance = Vector2.dst(
            playerTransform.position.x, playerTransform.position.y,
            enemyTransform.position.x, enemyTransform.position.y
        )
        return distance < (playerCollider.radius + enemyCollider.radius)
    }
}
