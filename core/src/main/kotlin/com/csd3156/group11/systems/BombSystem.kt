package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.PowerUpComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.enums.PowerUpType

/**
 * This system loops over all "players" (or any entity with PowerUpComponent).
 * If bombActive == true, it kills enemies within bombRadius of bombPos,
 * and decrements bombTimer until 0.
 */
class BombSystem : BaseEntitySystem(
    Aspect.all(PowerUpComponent::class.java, TransformComponent::class.java)
) {
    private lateinit var powerUpMapper: ComponentMapper<PowerUpComponent>
    private lateinit var transformMapper: ComponentMapper<TransformComponent>

    override fun processSystem() {
        // We'll gather all enemies once
        val enemyEntities = world.aspectSubscriptionManager.get(
            Aspect.all(EnemyComponent::class.java, TransformComponent::class.java)
        ).entities

        val bombers = subscription.entities
        for (i in 0 until bombers.size()) {
            val entityId = bombers[i]
            val pwrComp = powerUpMapper[entityId]

            // Only handle it if it's a BOMB effect that's active
            if (pwrComp.type == PowerUpType.BOMB && pwrComp.bombActive) {
                // Decrement bomb timer
                pwrComp.bombTimer -= world.delta
                if (pwrComp.bombTimer <= 0f) {
                    // Bomb effect ends
                    pwrComp.bombActive = false
                    pwrComp.type = PowerUpType.NONE
                    continue
                }

                // Check for enemies inside bombRadius from bombPos
                for (j in 0 until enemyEntities.size()) {
                    val enemyId = enemyEntities[j]
                    val enemyTransform = transformMapper.get(enemyId)

                    val dist = Vector2.dst(
                        pwrComp.bombPos.x,
                        pwrComp.bombPos.y,
                        enemyTransform.position.x,
                        enemyTransform.position.y
                    )

                    if (dist <= pwrComp.bombRadius) {
                        world.delete(enemyId)  // kill the enemy
                    }
                }
            }
        }
    }
}
