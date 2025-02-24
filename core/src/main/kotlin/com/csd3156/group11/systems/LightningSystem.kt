/**
 * @file LightningSystem.kt
 * @brief Handles lightning-based power-ups and enemy strikes.
 *
 * The LightningSystem is responsible for detecting and applying lightning strikes
 * to enemies within range, ensuring they are affected in a prioritized order.
 */

package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.PowerUpComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.enums.PowerUpType
import com.csd3156.group11.prefabs.LightningFX

/**
 * @file LightningSystem.kt
 * @brief Handles lightning-based power-ups and enemy strikes.
 */
class LightningSystem : BaseEntitySystem(
    Aspect.all(PowerUpComponent::class.java, TransformComponent::class.java)
) {
    private lateinit var transformMapper: ComponentMapper<TransformComponent>
    private lateinit var powerUpMapper: ComponentMapper<PowerUpComponent>
    private lateinit var enemyMapper: ComponentMapper<EnemyComponent>

    private val lightningRange = 6f  // Only strike enemies within this range
    private val maxStrikes = 3  // Maximum number of targets to strike

    /**
     * @brief Processes lightning power-ups and applies effects to enemies.
     */
    override fun processSystem() {
        val entities = subscription.entities

        for (i in 0 until entities.size()) {
            val entityId = entities[i]
            val powerUpComp = powerUpMapper[entityId]

            // Only process if lightning strikes are queued
            if (powerUpComp.lightning.isNotEmpty()) {
                val playerPosition = transformMapper.get(entityId).position
                strikeNearestEnemies(playerPosition, maxStrikes, powerUpComp)
                powerUpComp.lightning.clear()  // Clear the lightning queue after striking
            }
        }
    }

    /**
     * @brief Strikes the nearest enemies with a lightning effect.
     *
     * This function finds the closest enemies to the given origin within the lightning range
     * and marks them as "dying" while triggering a lightning visual effect.
     *
     * @param origin The starting position of the lightning strike.
     * @param strikesLeft The maximum number of enemies that can be struck.
     * @param powerUpComp The power-up component associated with the lightning attack.
     */
    private fun strikeNearestEnemies(
        origin: Vector2,
        strikesLeft: Int,
        powerUpComp: PowerUpComponent
    ) {
        val enemies = world.aspectSubscriptionManager.get(
            Aspect.all(EnemyComponent::class.java, TransformComponent::class.java)
        ).entities

        val sortedEnemies = (0 until enemies.size())
            .map { enemies[it] }
            .sortedBy { enemyId ->
                val enemyTransform = transformMapper.get(enemyId)
                enemyTransform?.position?.dst(origin) ?: Float.MAX_VALUE
            }

        var strikes = 0
        for (i in 0 until sortedEnemies.size) {
            val enemyId = sortedEnemies[i]
            val enemyTransform = transformMapper.get(enemyId)
            val enemyComponent = enemyMapper.get(enemyId)

            if (enemyTransform != null && enemyComponent != null) {
                val enemyPosition = enemyTransform.position
                val distance = origin.dst(enemyPosition)

                if (distance <= lightningRange) {
                    //println("Striking enemy ID: $enemyId at distance: $distance")

                    // Instead of deleting, set the enemy to dying state
                    if (!enemyComponent.isDying) {
                        enemyComponent.isDying = true
                        //println("Enemy $enemyId marked as dying from lightning strike")
                    }

                    // Create Lightning FX at the enemy position

                    val lightningFX = LightningFX(enemyPosition)
                    lightningFX.Create(world)

                    strikes++
                    if (strikes >= strikesLeft) break
                }
            }
        }
    }
}
