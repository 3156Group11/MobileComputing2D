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

                    // Remove the power-up entity from the world
                    world.delete(powerUpEntityId)

                    // Add LightningEntry to the player's PowerUpComponent to trigger the effect
                    val playerPowerUpComp = world.getEntity(playerEntityId).getComponent(PowerUpComponent::class.java)
                    val playerTransform = world.getEntity(playerEntityId).getComponent(TransformComponent::class.java)

                    val lightningStrike = PowerUpComponent.LightningEntry(
                        targetPosition = playerTransform.position.cpy()  // Player's current position
                    )
                    playerPowerUpComp.lightning.add(lightningStrike)
                }

                PowerUpType.SLOW_FIELD -> {
                    println("Player picked up SLOW FIELD powerup!")
                    world.delete(powerUpEntityId)

                    val playerTransform = world.getEntity(playerEntityId).getComponent(TransformComponent::class.java)
                    val playerPos = playerTransform.position.cpy()

                    // Access player's PowerUpComponent to store the slow field data
                    val powerUpComp = world.getEntity(playerEntityId).getComponent(PowerUpComponent::class.java)

                    // Upward beam (faster)
                    val upwardSlowField = PowerUpComponent.SlowFieldEntry(
                        position = playerPos.cpy(),
                        timeLeft = 5f,  // Field lasts for 5 seconds
                        direction = Vector2(0f, 1f)  // Moving upward
                    )
                    powerUpComp.slowFields.add(upwardSlowField)

                    // Downward beam (slower)
                    val downwardSlowField = PowerUpComponent.SlowFieldEntry(
                        position = playerPos.cpy(),
                        timeLeft = 5f,  // Field lasts for 5 seconds
                        direction = Vector2(0f, -1f)  // Moving downward
                    )
                    powerUpComp.slowFields.add(downwardSlowField)

                    // Create visual FX entities with different speeds
                    val upwardFX = SlowFieldFX(playerPos.cpy(), Vector2(0f, 1f), 10f)  // Faster upward beam
                    upwardFX.Create(world)

                    val downwardFX = SlowFieldFX(playerPos.cpy(), Vector2(0f, -1f), 10f)  // Slower downward beam
                    downwardFX.Create(world)

                    //println("Two slow fields spawned with speeds: Upward (150f), Downward (75f)")
                }


                PowerUpType.LASER_3X -> {
                    println("Player picked up LASER powerup!")

                    val playerTransform = world.getEntity(playerEntityId).getComponent(TransformComponent::class.java)
                    val playerPowerUpComp = world.getEntity(playerEntityId).getComponent(PowerUpComponent::class.java)

                    val laserEntry = PowerUpComponent.LaserEntry(
                        startPosition = playerTransform.position.cpy(),
                        direction = Vector2(1f, 0f), // Default forward direction
                        timeLeft = 3f,
                        length = 10f,
                        damage = 10f
                    )

                    playerPowerUpComp.lasers.add(laserEntry)

                    // Debugging print to confirm storage
                    println("Laser stored in PowerUpComponent: Start=${laserEntry.startPosition}, Dir=${laserEntry.direction}")

                    world.delete(powerUpEntityId)
                }

                else -> {
                    // Handle other powerups if needed
                }
            }
        }
    }
}
