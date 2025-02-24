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

    override fun processSystem() {
        val entities = subscription.entities

        for (i in 0 until entities.size()) {
            val entityId = entities[i]
            val tag = tagMapper[entityId]

            // Only process power-ups
            if (tag.tag != Tag.POWERUP) continue

            val transform = transformMapper[entityId]
            // Update position based on velocity
            // Handle bouncing off the screen boundaries
            transform.rotation += world.delta * 30
        }
    }
}
