package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.artemis.systems.IteratingSystem
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.VelocityComponent
import com.csd3156.group11.resources.Globals

class PhysicsSystem : IteratingSystem(Aspect.all(TransformComponent::class.java, VelocityComponent::class.java)) {

    private lateinit var transformMapper: ComponentMapper<TransformComponent>
    private lateinit var velocityMapper: ComponentMapper<VelocityComponent>
    private lateinit var enemyMapper: ComponentMapper<EnemyComponent>

    private val screenWidth = 800f  // Set screen width
    private val screenHeight = 400f // Set screen height

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
        if (transform.position.x <= 0 || transform.position.x >= screenWidth) {
            velocity.velocity.x *= -1 // Reverse X direction
            transform.position.x = transform.position.x.coerceIn(0f, screenWidth) // Keep inside bounds
        }
        if (transform.position.y <= 0 || transform.position.y >= screenHeight) {
            velocity.velocity.y *= -1 // Reverse Y direction
            transform.position.y = transform.position.y.coerceIn(0f, screenHeight) // Keep inside bounds
        }
    }
}
