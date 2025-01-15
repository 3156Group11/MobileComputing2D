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

    override fun process(entityId: Int) {
        val input = inputMapper[entityId]
        val velocity = velocityMapper[entityId]

        // Read accelerometer values
        val accelX = Gdx.input.accelerometerX
        val accelY = Gdx.input.accelerometerY

        // Adjust direction based on tilt (you may need to tweak these multipliers for your game)
        input.tiltDirection.set(-accelY, accelX).nor() // Normalize for consistent speed

        // Update velocity based on tilt direction
        val speed = 200f // Adjust speed as needed
        velocity.velocity.set(input.tiltDirection.x * speed, input.tiltDirection.y * speed)
    }
}
