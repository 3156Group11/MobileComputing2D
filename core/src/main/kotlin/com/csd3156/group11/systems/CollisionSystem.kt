package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.ColliderComponent
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.PlayerInputComponent
import com.csd3156.group11.components.PowerUpComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.TagComponent
import com.csd3156.group11.enums.PowerUpType
import com.csd3156.group11.enums.Tag


class CollisionSystem : BaseEntitySystem(Aspect.all(ColliderComponent::class.java, TransformComponent::class.java)) {

    private lateinit var transformMapper: ComponentMapper<TransformComponent>
    private lateinit var colliderMapper: ComponentMapper<ColliderComponent>
    private lateinit var playerMapper: ComponentMapper<PlayerInputComponent>
    private lateinit var powerUpMapper: ComponentMapper<PowerUpComponent>

    override fun processSystem() {
        val entities = subscription.entities

        for (i in 0 until entities.size()) {
            val entityA = entities[i]
            val transformA = transformMapper[entityA]
            val colliderA = colliderMapper[entityA]

            for (j in i + 1 until entities.size()) {
                val entityB = entities[j]
                val transformB = transformMapper[entityB]
                val colliderB = colliderMapper[entityB]

                if (isColliding(transformA, colliderA, transformB, colliderB)) {
                    handleCollision(entityA, entityB)
                }
            }
        }
    }

    private fun isColliding(
        transformA: TransformComponent, colliderA: ColliderComponent,
        transformB: TransformComponent, colliderB: ColliderComponent
    ): Boolean {
        val distance = Vector2.dst(
            transformA.position.x, transformA.position.y,
            transformB.position.x, transformB.position.y
        )
        return distance < (colliderA.radius + colliderB.radius)
    }

    private fun handleCollision(entityA: Int, entityB: Int) {

        // Player and Powerup
        // 1) Grab each entity's tag (default to NONE if TagComponent is missing)
        val tagA = world.getEntity(entityA).getComponent(TagComponent::class.java)?.tag ?: Tag.NONE
        val tagB = world.getEntity(entityB).getComponent(TagComponent::class.java)?.tag ?: Tag.NONE

        // 2) If one is PLAYER and the other is POWERUP, handle powerup pickup
        if (tagA == Tag.PLAYER && tagB == Tag.POWERUP) {
            pickupPowerUp(entityA, entityB)
        } else if (tagB == Tag.PLAYER && tagA == Tag.POWERUP) {
            pickupPowerUp(entityB, entityA)
        }

        // Player and Enemy
        // Enemy and Destructive objects
        // Player
        if (world.getEntity(entityA).getComponent(PlayerInputComponent::class.java) != null)
        {
            if (world.getEntity(entityB).getComponent(EnemyComponent::class.java) != null)
            {
                val powerUp = world.getEntity(entityA).getComponent(PowerUpComponent::class.java)

                if (powerUp != null && powerUp.hasShield)
                {
                    powerUp.hasShield = false
                    powerUp.shieldBreakEffect = true  // Trigger shield break effect
                    return
                }

                // player dies
                return
            }
            // else if powerup, TODO: Add powerup component
            else if (world.getEntity(entityB).getComponent(PowerUpComponent::class.java) != null)
            {
                val powerUp = world.getEntity(entityB).getComponent(PowerUpComponent::class.java)

                if (powerUp != null && powerUp.hasShield)
                {
                    val playerPowerUp = world.getEntity(entityA).getComponent(PowerUpComponent::class.java)
                    if (playerPowerUp != null) {
                        playerPowerUp.hasShield = true
                    }
                    world.delete(entityB) // Remove the power-up after collection
                }

                // Activate Powerup
                return
            }
        }

        // Player
        else if (world.getEntity(entityB).getComponent(PlayerInputComponent::class.java) != null)
        {
            if (world.getEntity(entityA).getComponent(EnemyComponent::class.java) != null)
            {
                val powerUp = world.getEntity(entityB).getComponent(PowerUpComponent::class.java)

                if (powerUp != null && powerUp.hasShield)
                {
                    powerUp.hasShield = false
                    powerUp.shieldBreakEffect = true  // Trigger shield break effect
                    return
                }

                // player dies
                return
            }
            // else if powerup, TODO: Add powerup component
            else if (world.getEntity(entityA).getComponent(PowerUpComponent::class.java) != null)
            {
                val powerUp = world.getEntity(entityA).getComponent(PowerUpComponent::class.java)

                if (powerUp != null && powerUp.hasShield)
                {
                    val playerPowerUp = world.getEntity(entityB).getComponent(PowerUpComponent::class.java)
                    if (playerPowerUp != null) {
                        playerPowerUp.hasShield = true
                    }
                    world.delete(entityA) // Remove the power-up after collection
                }

                // Activate Powerup
                return
            }
        }
    } // end of handle collision func

    // Helper function:
    private fun pickupPowerUp(playerEntityId: Int, powerUpEntityId: Int) {
        // Get the PowerUpComponent from the collided powerup
        val powerUpComp = world.getEntity(powerUpEntityId).getComponent(PowerUpComponent::class.java)
        if (powerUpComp != null) {
            when (powerUpComp.type) {
                PowerUpType.SHIELD -> {
                    println("Player picked up SHIELD powerup!")
                    // If the player has a PowerUpComponent, mark hasShield = true (or do your own logic)
                    val playerPowerComp = world.getEntity(playerEntityId).getComponent(PowerUpComponent::class.java)
                    if (playerPowerComp != null) {
                        playerPowerComp.hasShield = true
                    } else {
                        // If the player doesn't have a PowerUpComponent, you can add one:
                        world.edit(playerEntityId).add(PowerUpComponent().apply { hasShield = true })
                    }
                }
                else -> {
                    println("Player picked up some other powerup: ${powerUpComp.type}")
                    // For now, do nothing. Later, handle other types (CHAIN_LIGHTNING, BOMB, etc.)
                }
            }

            // Remove the powerup entity from the world
            world.delete(powerUpEntityId)
        }
    }
}
