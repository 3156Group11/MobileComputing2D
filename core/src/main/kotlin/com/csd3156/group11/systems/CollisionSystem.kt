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
import com.csd3156.group11.prefabs.ShieldFX
import com.csd3156.group11.prefabs.BombFX
import com.csd3156.group11.prefabs.LightningFX
import com.csd3156.group11.prefabs.SlowFieldFX
import com.csd3156.group11.resources.Globals


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
        val tagA = world.getEntity(entityA).getComponent(TagComponent::class.java)?.tag ?: Tag.NONE
        val tagB = world.getEntity(entityB).getComponent(TagComponent::class.java)?.tag ?: Tag.NONE

        if (tagA == Tag.PLAYER && tagB == Tag.POWERUP) {
            pickupPowerUp(entityA, entityB)
        } else if (tagB == Tag.PLAYER && tagA == Tag.POWERUP) {
            pickupPowerUp(entityB, entityA)
        }

        // Player and Enemy collision detection (Check for invincibility)
        if (world.getEntity(entityA).getComponent(PlayerInputComponent::class.java) != null) {
            if (world.getEntity(entityB).getComponent(EnemyComponent::class.java) != null) {
                val powerUp = world.getEntity(entityA).getComponent(PowerUpComponent::class.java)

                // Skip if player is in shield cooldown (temporary invincibility)
                if (powerUp != null && powerUp.invulnerability > 0f) {
                    println("Player is temporarily invincible due to shield cooldown.")
                    return
                }

                // If no shield or invincibility, player dies
                Globals.deathScreen = true
                Globals.deathScreenInit = true

                return
            }
        }
    }


    // Helper function:
    private fun pickupPowerUp(playerEntityId: Int, powerUpEntityId: Int) {
        val powerUpComp =
            world.getEntity(powerUpEntityId).getComponent(PowerUpComponent::class.java)
        if (powerUpComp != null) {
            when (powerUpComp.type) {
                PowerUpType.SHIELD -> {
                    println("Player picked up SHIELD powerup!")
                    val playerPowerUpComp = world.getEntity(playerEntityId).getComponent(PowerUpComponent::class.java)
                    playerPowerUpComp.hasShield = true
                    val shieldFX = ShieldFX(playerEntityId)
                    shieldFX.Create(world)
                    world.delete(powerUpEntityId)
                }

                PowerUpType.BOMB -> {
                    println("Player picked up BOMB powerup!")

                    val bombTransform = world.getEntity(powerUpEntityId).getComponent(TransformComponent::class.java)
                    val bombPickupPos = bombTransform.position.cpy()
                    val bombRadius = 5f  // Set to 10 units

                    // Ensure adding bomb to player's PowerUpComponent
                    val playerPowerUpComp = world.getEntity(playerEntityId).getComponent(PowerUpComponent::class.java)

                    playerPowerUpComp.bombs.add(
                        PowerUpComponent.BombEntry(
                            center = bombPickupPos,
                            timeLeft = 3f,
                            radius = bombRadius
                        )
                    )

                    // Trigger FX
                    val bombFX = BombFX(bombPickupPos, bombRadius)
                    bombFX.Create(world)

                    // Delete power-up entity after pickup
                    world.delete(powerUpEntityId)
                }

                PowerUpType.LIGHTNING -> {
                    println("Player picked up LIGHTNING powerup!")
                    // Remove the collectible from the world
                    world.delete(powerUpEntityId)

                    // Get the position where the pickup occurred
                    val lightningTransform = world.getEntity(powerUpEntityId)
                        .getComponent(TransformComponent::class.java)
                    val lightningPickupPos = lightningTransform.position.cpy()

                    // Trigger LightningFX and strikes instantly
                    val lightningFX = LightningFX(lightningPickupPos)
                    lightningFX.Create(world)
                }

                PowerUpType.SLOW_FIELD -> {
                    println("Player picked up SLOW FIELD powerup!")
                    world.delete(powerUpEntityId)

                    val playerTransform = world.getEntity(playerEntityId).getComponent(TransformComponent::class.java)
                    val playerPos = playerTransform.position.cpy()

                    // Add two SlowFieldEntry instances to the PowerUpComponent (moving upward and downward)
                    val powerUpComp = world.getEntity(playerEntityId).getComponent(PowerUpComponent::class.java)

                    // Upward moving field
                    val upwardSlowField = PowerUpComponent.SlowFieldEntry(
                        position = playerPos.cpy(),
                        timeLeft = 5f,  // Field lasts for 5 seconds
                        direction = Vector2(0f, 1f)  // Moving upward
                    )
                    powerUpComp.slowFields.add(upwardSlowField)

                    // Downward moving field
                    val downwardSlowField = PowerUpComponent.SlowFieldEntry(
                        position = playerPos.cpy(),
                        timeLeft = 5f,  // Field lasts for 5 seconds
                        direction = Vector2(0f, -1f)  // Moving downward
                    )
                    powerUpComp.slowFields.add(downwardSlowField)

                    // Create visual FX entities for each slow field
                    val upwardFX = SlowFieldFX(playerPos.cpy(), Vector2(0f, 1f))
                    upwardFX.Create(world)

                    val downwardFX = SlowFieldFX(playerPos.cpy(), Vector2(0f, -1f))
                    downwardFX.Create(world)

                    println("Spawned two slow fields with visual FX from position: $playerPos")
                }

                else -> {
                    // Handle other powerups if needed
                }
            }
        }
    }
}
