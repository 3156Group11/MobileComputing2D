package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.systems.IteratingSystem
import com.csd3156.group11.components.PlayerInputComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.VelocityComponent

class PhysicsSystem : IteratingSystem(Aspect.all(TransformComponent::class.java, VelocityComponent::class.java)) {

    override fun process(entityId: Int) {
        val transform = world.getMapper(TransformComponent::class.java)[entityId]
        val velocity = world.getMapper(VelocityComponent::class.java)[entityId]

        // Update the entity's position based on its velocity and the delta time
        velocity.velocity.add(velocity.acceleration.cpy().scl(world.delta))
        transform.position.add(velocity.velocity.cpy().scl(world.delta))
    }
}


