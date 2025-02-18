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
    private val accelerationFactor = 500f // Determines how fast acceleration builds up
    private val velocityCap = 1000f
    private val dampingFactor = 0.95f // For deceleration when no input is given

    override fun process(entityId: Int) {
        val input = inputMapper[entityId]
        val velocity = velocityMapper[entityId]

        // Read accelerometer values
        val accelX = Gdx.input.accelerometerX  // Left (-), Right (+) → Controls Y movement
        val accelY = -Gdx.input.accelerometerY  // Forward (-), Backward (+) → Controls X movement

        // Apply dead zone to filter out small values
        val deadZone = 0.5f
        val adjustedX = if (kotlin.math.abs(accelX) > deadZone) accelX else 0f
        val adjustedY = if (kotlin.math.abs(accelY) > deadZone) accelY else 0f

        // Set tilt direction based on new mapping
        input.tiltDirection.set(-adjustedY, -adjustedX) // Invert signs to match desired movement

        // Normalize tilt direction for consistent acceleration
        if (input.tiltDirection.len() > 1f) {
            input.tiltDirection.nor()
        }

        // Apply acceleration based on tilt direction
        velocity.acceleration.set(input.tiltDirection.x * accelerationFactor, input.tiltDirection.y * accelerationFactor)

        // Integrate acceleration into velocity
        velocity.velocity.x += velocity.acceleration.x * world.delta
        velocity.velocity.y += velocity.acceleration.y * world.delta

        // Cap velocity to prevent it from exceeding the maximum
        if (velocity.velocity.len() > velocityCap) {
            velocity.velocity.nor().scl(velocityCap)
        }

        // Apply damping to velocity for smoother deceleration
        velocity.velocity.scl(dampingFactor)
    }

}
