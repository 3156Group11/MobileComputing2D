package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.prefabs.LaserFX

class LaserSystem : BaseEntitySystem(Aspect.all(PowerUpComponent::class.java, TransformComponent::class.java)) {
    private lateinit var powerUpMapper: ComponentMapper<PowerUpComponent>
    private lateinit var transformMapper: ComponentMapper<TransformComponent>
    private lateinit var enemyMapper: ComponentMapper<EnemyComponent>

    override fun processSystem() {
        val entitiesWithLasers = subscription.entities
        for (i in 0 until entitiesWithLasers.size()) {
            val entityId = entitiesWithLasers[i]
            val pwrComp = powerUpMapper[entityId]
            val playerTransform = transformMapper.get(entityId)

            val lasersToRemove = mutableListOf<PowerUpComponent.LaserEntry>()
            for (laser in pwrComp.lasers) {
                laser.timeLeft -= world.delta
                if (laser.timeLeft <= 0f) {
                    lasersToRemove.add(laser)
                    continue
                }

                // Set laser start position to player position
                laser.startPosition.set(playerTransform.position)

                // Debugging print
                println("Laser fired from ${laser.startPosition} in direction ${laser.direction}")

                // Create Laser FX
                val laserFX = LaserFX(laser.startPosition.cpy(), laser.direction, laser.length)
                laserFX.Create(world)

                // Check for enemy collisions
                val enemyEntities = world.aspectSubscriptionManager
                    .get(Aspect.all(EnemyComponent::class.java, TransformComponent::class.java))
                    .entities

                for (j in 0 until enemyEntities.size()) {
                    val enemyId = enemyEntities[j]
                    val enemyTransform = transformMapper.get(enemyId)

                    val distance = Vector2.dst(
                        laser.startPosition.x, laser.startPosition.y,
                        enemyTransform.position.x, enemyTransform.position.y
                    )

                    if (distance < laser.length) { // Laser hits enemy
                        println("🔥 Enemy $enemyId hit by laser at distance $distance!")
                        world.delete(enemyId) // Destroy enemy
                    }
                }
            }
            pwrComp.lasers.removeAll(lasersToRemove)
        }
    }
}
