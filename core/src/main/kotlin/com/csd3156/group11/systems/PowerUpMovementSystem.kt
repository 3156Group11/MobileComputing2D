package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.Tag
import com.csd3156.group11.resources.Globals

class PowerUpMovementSystem : BaseEntitySystem(
    Aspect.all(PowerUpComponent::class.java, TransformComponent::class.java, VelocityComponent::class.java, TagComponent::class.java)
) {
    private lateinit var transformMapper: ComponentMapper<TransformComponent>
    private lateinit var velocityMapper: ComponentMapper<VelocityComponent>
    private lateinit var tagMapper: ComponentMapper<TagComponent>

    private val screenWidth = Globals.scrWidth / Globals.UnitSize
    private val screenHeight = Globals.scrHeight / Globals.UnitSize

    override fun processSystem() {
        val entities = subscription.entities

        for (i in 0 until entities.size()) {
            val entityId = entities[i]
            val tag = tagMapper[entityId]

            // Only process power-ups
            if (tag.tag != Tag.POWERUP) continue

            val transform = transformMapper[entityId]
            val velocity = velocityMapper[entityId]

            // Update position based on velocity
            transform.position.add(velocity.velocity.cpy().scl(world.delta))

            // Handle bouncing off the screen boundaries
            handleBoundaryCollision(transform, velocity)
        }
    }

    private fun handleBoundaryCollision(transform: TransformComponent, velocity: VelocityComponent) {
        // Bounce off left and right boundaries
        if (transform.position.x <= 0 || transform.position.x + transform.scale.x >= screenWidth) {
            velocity.velocity.x = -velocity.velocity.x
            println("↔️ Power-Up bounced horizontally at X: ${transform.position.x}")
        }

        // Bounce off bottom boundary
        if (transform.position.y <= 0) {
            velocity.velocity.y = -velocity.velocity.y
            println("🔽 Power-Up bounced off the bottom at Y: ${transform.position.y}")
        }

        // Bounce off top boundary
        if (transform.position.y + transform.scale.y >= screenHeight) {
            velocity.velocity.y = -velocity.velocity.y
            println("🔼 Power-Up bounced off the top at Y: ${transform.position.y}")
        }
    }
}
