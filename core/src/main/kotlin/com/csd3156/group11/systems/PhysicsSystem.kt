/**
 * @file PhysicsSystem.kt
 * @brief Handles entity movement and collision with screen boundaries.
 */
package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.artemis.systems.IteratingSystem
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.VelocityComponent
import com.csd3156.group11.resources.Globals

/**
 * @class PhysicsSystem
 * @brief Manages physics-based movement and collision handling for entities.
 *
 * This system updates the position of all entities that have `TransformComponent` and `VelocityComponent`
 * based on their velocity. It also enforces screen boundaries by reversing velocity upon collision.
 * Entities with an `EnemyComponent` do not reflect their velocity upon collision.
 */
class PhysicsSystem : IteratingSystem(Aspect.all(TransformComponent::class.java, VelocityComponent::class.java)) {

    private lateinit var transformMapper: ComponentMapper<TransformComponent>
    private lateinit var velocityMapper: ComponentMapper<VelocityComponent>
    private lateinit var enemyMapper: ComponentMapper<EnemyComponent>

    private val screenWidth = 800f  // Set screen width
    private val screenHeight = 400f // Set screen height

    /**
     * @brief Processes physics updates for a given entity.
     *
     * Updates the entity's position based on its velocity. If the entity is an enemy,
     * it does not reflect velocity upon hitting boundaries. Non-enemy entities reverse
     * their velocity upon colliding with the screen edges.
     *
     * @param entityId The ID of the entity being processed.
     */
    override fun process(entityId: Int) {
        if (Globals.IsStarting) return
        if (Globals.deathScreen) return
        if (Globals.isPausing) return

        val transform = transformMapper[entityId]
        val velocity = velocityMapper[entityId]

        // Move entity based on velocity
        transform.position.add(velocity.velocity.cpy().scl(world.delta))

        // enemy does not reflect velocity
        val enemy = enemyMapper.get(entityId)
        if (enemy != null) {
            return
        }

        // Check collision with screen edges and reflect velocity
        if (transform.position.x <= 0 || transform.position.x >= 34) {
            velocity.velocity.x *= -1 // Reverse X direction
            transform.position.x = transform.position.x.coerceIn(0f, 34f) // Keep inside bounds
        }
        if (transform.position.y <= 0 || transform.position.y >= 35 * screenHeight/screenWidth - 3) {
            velocity.velocity.y *= -1 // Reverse Y direction
            transform.position.y = transform.position.y.coerceIn(0f, 35f * screenHeight/screenWidth - 3) // Keep inside bounds
        }
    }
}
