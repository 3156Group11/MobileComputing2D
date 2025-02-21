package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType

class SlowFieldSystem : BaseEntitySystem(
    Aspect.all(PowerUpComponent::class.java, TransformComponent::class.java)
) {
    // Component mappers for accessing components
    private lateinit var transformMapper: ComponentMapper<TransformComponent>
    private lateinit var powerUpMapper: ComponentMapper<PowerUpComponent>
    private lateinit var enemyMapper: ComponentMapper<EnemyComponent>
    private lateinit var velocityMapper: ComponentMapper<VelocityComponent>

    override fun processSystem() {
        val entities = subscription.entities

        // Loop through all entities with PowerUpComponent and TransformComponent
        for (i in 0 until entities.size()) {
            val entityId = entities[i]
            val powerUpComp = powerUpMapper[entityId]

            // Access the list of active slow fields
            val slowFields = powerUpComp.slowFields

            // Iterate over slow fields in reverse to safely remove expired ones
            for (j in slowFields.indices.reversed()) {
                val slowField = slowFields[j]

                // Create a new position by updating a copy of the current position
                val newPosition = slowField.position.cpy().add(slowField.direction.cpy().scl(world.delta * 100f))
                // Replace the entire entry with an updated position
                slowFields[j] = slowField.copy(position = newPosition)

                // Apply slow effect on enemies within range
                val enemies = world.aspectSubscriptionManager.get(
                    Aspect.all(EnemyComponent::class.java, TransformComponent::class.java, VelocityComponent::class.java)
                ).entities

                for (k in 0 until enemies.size()) {
                    val enemyId = enemies[k]
                    val enemyTransform = transformMapper[enemyId]
                    val enemyVelocity = velocityMapper[enemyId]

                    // Check if enemy is within collision range of the slow field
                    val distance = slowField.position.dst(enemyTransform.position)
                    if (distance <= 30f) {  // Collision detection range
                        println("Enemy $enemyId hit by Slow Field! Reducing velocity.")
                        enemyVelocity.velocity.scl(0.5f)  // Permanently reduce enemy velocity by 50%
                    }
                }

                // Decrease the remaining active duration of the slow field
                val updatedTimeLeft = slowField.timeLeft - world.delta
                if (updatedTimeLeft <= 0f) {
                    println("Slow Field expired and removed.")
                    slowFields.removeAt(j)  // Safely remove expired slow field
                } else {
                    slowFields[j] = slowField.copy(timeLeft = updatedTimeLeft)  // Update timeLeft without mutation
                }
            }
        }
    }
}
