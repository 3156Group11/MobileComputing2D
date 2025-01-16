package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.csd3156.group11.components.PlayerInputComponent
import com.csd3156.group11.components.VelocityComponent

class PlayerInputSystem : IteratingSystem(Aspect.all(PlayerInputComponent::class.java, VelocityComponent::class.java)) {

    private lateinit var inputMapper: ComponentMapper<PlayerInputComponent>
    private lateinit var velocityMapper: ComponentMapper<VelocityComponent>
    private val speed = 0.1f
    private val velocityCap = 100f

    override fun process(entityId: Int) {
        val input = inputMapper[entityId]
        val velocity = velocityMapper[entityId]

        // Read accelerometer values
        velocity.acceleration.x += (-Gdx.input.accelerometerX)
        velocity.acceleration.y += (-Gdx.input.accelerometerY)

        // Adjust direction based on tilt (you may need to tweak these multipliers for your game)
        input.tiltDirection.set(-velocity.acceleration.x , velocity.acceleration.y) // Normalize for consistent speed

        // Update velocity based on tilt direction
        val speed = 200f // Adjust speed as needed
        velocity.velocity.set(input.tiltDirection.x, input.tiltDirection.y)
        if (velocity.velocity.len() > velocityCap)
        {
            velocity.velocity.x *= velocityCap/velocity.velocity.len()
            velocity.velocity.y *= velocityCap/velocity.velocity.len()
        }
    }
}
