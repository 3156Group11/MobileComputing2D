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

class LightningSystem : BaseEntitySystem(
    Aspect.all(PowerUpComponent::class.java, TransformComponent::class.java)
) {
    private lateinit var transformMapper: ComponentMapper<TransformComponent>
    private lateinit var powerUpMapper: ComponentMapper<PowerUpComponent>
    private lateinit var enemyMapper: ComponentMapper<EnemyComponent>

    private val lightningRange = 300f  // Only strike enemies within this range
    private val maxStrikes = 3  // Maximum number of targets to strike

    override fun processSystem() {
        val entities = subscription.entities

        for (i in 0 until entities.size()) {
            val entityId = entities[i]
            val powerUpComp = powerUpMapper[entityId]

            if (powerUpComp.type == PowerUpType.LIGHTNING) {
                val playerPosition = transformMapper.get(entityId).position
                strikeNearestEnemies(playerPosition, maxStrikes, powerUpComp)
                powerUpComp.type = PowerUpType.NONE  // Reset power-up state
            }
        }
    }

    private fun strikeNearestEnemies(
        origin: Vector2,
        strikesLeft: Int,
        powerUpComp: PowerUpComponent
    ) {
        // Fetch all enemies with required components
        val enemies = world.aspectSubscriptionManager.get(
            Aspect.all(EnemyComponent::class.java, TransformComponent::class.java)
        ).entities

        // Sort enemies by distance from the origin using IntBag access
        val sortedEnemies = (0 until enemies.size())
            .map { enemies[it] }  // Get each enemy entity ID
            .sortedBy { enemyId ->
                val enemyTransform = transformMapper.get(enemyId)
                enemyTransform?.position?.dst(origin) ?: Float.MAX_VALUE
            }

        // Strike up to 3 enemies within range
        var strikes = 0
        for (i in 0 until sortedEnemies.size) {
            val enemyId = sortedEnemies[i]
            val enemyTransform = transformMapper.get(enemyId)

            if (enemyTransform != null) {
                val enemyPosition = enemyTransform.position
                val distance = origin.dst(enemyPosition)

                if (distance <= lightningRange) {
                    println("Striking enemy ID: $enemyId at distance: $distance")
                    world.delete(enemyId)  // Remove the enemy

                    // Create visual FX for the strike
                    val lightningFX = LightningFX(enemyPosition)
                    lightningFX.Create(world)

                    strikes++
                    if (strikes >= strikesLeft) break
                }
            }
        }
    }
}

