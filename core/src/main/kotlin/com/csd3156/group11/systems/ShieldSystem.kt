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

class ShieldSystem : BaseEntitySystem(
    Aspect.all(PowerUpComponent::class.java, PlayerInputComponent::class.java, TransformComponent::class.java)
) {
    private lateinit var powerUpMapper: ComponentMapper<PowerUpComponent>
    private lateinit var transformMapper: ComponentMapper<TransformComponent>
    private lateinit var colliderMapper: ComponentMapper<ColliderComponent>
    private lateinit var enemyMapper: ComponentMapper<EnemyComponent>

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

                println("🛡️ Shield absorbed the hit, broke, and FX has been removed.")
                return
            }
        }
    }

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
