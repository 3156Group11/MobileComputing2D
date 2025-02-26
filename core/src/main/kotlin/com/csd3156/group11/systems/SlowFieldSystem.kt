/**
 * @file SlowFieldSystem.kt
 * @brief Handles the slow field power-up, applying a slow effect to enemies within range.
 */
package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType

/**
 * @class SlowFieldSystem
 * @brief Manages the movement and effect application of slow fields in the game.
 *
 * This system updates all active slow fields by moving them in their respective directions.
 * It also detects enemy collisions, applying a slow effect that reduces enemy speed for a set duration.
 */
class SlowFieldSystem : BaseEntitySystem(
    Aspect.all(PowerUpComponent::class.java, TransformComponent::class.java)
) {
    private lateinit var transformMapper: ComponentMapper<TransformComponent>
    private lateinit var powerUpMapper: ComponentMapper<PowerUpComponent>
    private lateinit var enemyMapper: ComponentMapper<EnemyComponent>
    private lateinit var velocityMapper: ComponentMapper<VelocityComponent>

    /**
     * @brief Processes slow field updates and applies effects to nearby enemies.
     *
     * - Moves active slow fields in their respective directions.
     * - Checks for collisions between slow fields and enemies.
     * - If an enemy enters a slow field's range, it applies a slow effect for 5 seconds.
     * - Removes expired slow fields from the system.
     */
    override fun processSystem() {
        val entities = subscription.entities

        for (i in 0 until entities.size()) {
            val entityId = entities[i]
            val powerUpComp = powerUpMapper[entityId]
            val slowFields = powerUpComp.slowFields

            for (j in slowFields.indices.reversed()) {
                val slowField = slowFields[j]

                val newPosition = slowField.position.cpy().add(slowField.direction.cpy().scl(world.delta * 100f))
                slowFields[j] = slowField.copy(position = newPosition)

                val enemies = world.aspectSubscriptionManager.get(
                    Aspect.all(EnemyComponent::class.java, TransformComponent::class.java, VelocityComponent::class.java)
                ).entities

                for (k in 0 until enemies.size()) {
                    val enemyId = enemies[k]
                    val enemyTransform = transformMapper[enemyId]
                    val enemyVelocity = velocityMapper[enemyId]
                    val enemyComponent = enemyMapper[enemyId]

                    val distance = slowField.position.dst(enemyTransform.position)
                    if (distance <= 5f) {  // Match collision radius with actual beam width
                        if (!enemyComponent.isSlowed) {
                            println("🌀 Enemy $enemyId hit by Slow Field! Applying slow effect.")
                            enemyComponent.isSlowed = true
                            enemyComponent.slowTimeRemaining = 5f  // Slow effect lasts 5 seconds
                            enemyVelocity.velocity.scl(0.5f)  // Reduce current velocity by 50%
                        }
                    }

                }

                val updatedTimeLeft = slowField.timeLeft - world.delta
                if (updatedTimeLeft <= 0f) {
                    println("Slow Field expired and removed.")
                    slowFields.removeAt(j)
                } else {
                    slowFields[j] = slowField.copy(timeLeft = updatedTimeLeft)
                }
            }
        }
    }
}
