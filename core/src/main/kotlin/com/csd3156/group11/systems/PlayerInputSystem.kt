package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.csd3156.group11.components.PlayerInputComponent
import com.csd3156.group11.components.VelocityComponent

class PlayerInputSystem(private val debugMode: Boolean = false) : IteratingSystem(Aspect.all(PlayerInputComponent::class.java, VelocityComponent::class.java)) {

    private lateinit var inputMapper: ComponentMapper<PlayerInputComponent>
    private lateinit var velocityMapper: ComponentMapper<VelocityComponent>

    // 🔹 NORMAL (Accelerometer) Mode Variables
    private val accelerationFactor = 500f // Determines how fast acceleration builds up
    private val velocityCap = 1000f
    private val dampingFactor = 0.95f // For deceleration when no input is given

    // 🔹 DEBUG MODE (WASD) Variables
    private val debugSpeed = 600f // Speed boost for WASD movement

    override fun process(entityId: Int) {
        val input = inputMapper[entityId]
        val velocity = velocityMapper[entityId]

        if (debugMode) {
            // DEBUG MODE: WASD Movement with Speed Boost
            val moveX = when {
                Gdx.input.isKeyPressed(Input.Keys.A) -> -1f
                Gdx.input.isKeyPressed(Input.Keys.D) -> 1f
                else -> 0f
            }
            val moveY = when {
                Gdx.input.isKeyPressed(Input.Keys.W) -> 1f
                Gdx.input.isKeyPressed(Input.Keys.S) -> -1f
                else -> 0f
            }

            input.tiltDirection.set(moveX, moveY).nor() // Normalize to avoid diagonal speed boost

            // Apply faster movement speed in debug mode
            velocity.velocity.set(input.tiltDirection.x * debugSpeed, input.tiltDirection.y * debugSpeed)

        } else {
            //RELEASE MODE: Accelerometer-Based Movement
            val accelX = Gdx.input.accelerometerX  // Left (-), Right (+) → Controls Y movement
            val accelY = -Gdx.input.accelerometerY  // Forward (-), Backward (+) → Controls X movement

            // Apply dead zone to filter out small values
            val deadZone = 0.5f
            val adjustedX = if (kotlin.math.abs(accelX) > deadZone) accelX else 0f
            val adjustedY = if (kotlin.math.abs(accelY) > deadZone) accelY else 0f

            // Set tilt direction based on new mapping (invert signs to match movement)
            input.tiltDirection.set(-adjustedY, -adjustedX)

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
}
