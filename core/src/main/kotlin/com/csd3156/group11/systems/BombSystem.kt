package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.PowerUpComponent
import com.csd3156.group11.components.TransformComponent

class BombSystem : BaseEntitySystem(
    Aspect.all(PowerUpComponent::class.java)
) {
    private lateinit var powerUpMapper: ComponentMapper<PowerUpComponent>
    private lateinit var transformMapper: ComponentMapper<TransformComponent>

    override fun processSystem() {
        // 1) Gather all enemies
        val enemyEntities = world.aspectSubscriptionManager
            .get(Aspect.all(EnemyComponent::class.java, TransformComponent::class.java))
            .entities

        // 2) Loop over each entity that has a PowerUpComponent (e.g., player)
        val entitiesWithPowerUps = subscription.entities
        for (i in 0 until entitiesWithPowerUps.size()) {
            val entityId = entitiesWithPowerUps[i]
            val pwrComp = powerUpMapper[entityId]

            // We'll collect bombs that should be removed after timeLeft <= 0
            val bombsToRemove = mutableListOf<PowerUpComponent.BombEntry>()

            // 3) For each bomb in the bombs list
            for (bomb in pwrComp.bombs) {
                // Decrement timer
                bomb.timeLeft -= world.delta
                if (bomb.timeLeft <= 0f) {
                    bombsToRemove.add(bomb)
                    continue
                }

                // 4) If still active, kill enemies in radius
                for (j in 0 until enemyEntities.size()) {
                    val enemyId = enemyEntities[j]
                    val enemyTransform = transformMapper.get(enemyId)

                    val dist = Vector2.dst(
                        bomb.center.x, bomb.center.y,
                        enemyTransform.position.x, enemyTransform.position.y
                    )

                    if (dist < bomb.radius) {
                        // kill the enemy
                        world.delete(enemyId)
                    }
                }
            }

            // 5) Remove bombs that ended
            pwrComp.bombs.removeAll(bombsToRemove)
        }
    }
}
