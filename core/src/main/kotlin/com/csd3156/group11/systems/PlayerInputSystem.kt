package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.csd3156.group11.components.PlayerInputComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.VelocityComponent
import com.csd3156.group11.enums.GameState
import com.csd3156.group11.resources.Globals
import javax.xml.crypto.dsig.Transform

class PlayerInputSystem(private val debugMode: Boolean = false) : IteratingSystem(Aspect.all(PlayerInputComponent::class.java, VelocityComponent::class.java)) {

    private lateinit var inputMapper: ComponentMapper<PlayerInputComponent>
    private lateinit var velocityMapper: ComponentMapper<VelocityComponent>
    private lateinit var transformMapper: ComponentMapper<TransformComponent>

    // NORMAL (Accelerometer) Mode Variables
    private val accelerationFactor = 4f // Determines how fast acceleration builds up
    private val velocityCap = 15f
    private val dampingFactor = 0.95f // For deceleration when no input is given

    // DEBUG MODE (WASD) Variables
    private val debugSpeed = 10f // Speed boost for WASD movement
    

    // Calibration Variables
    var calibratedAccelY: Float
        get() = Globals.calibratedAccelY
        set(value) { Globals.calibratedAccelY = value }
    var calibratedAccelX: Float
        get() = Globals.calibratedAccelX
        set(value) { Globals.calibratedAccelX = value }
    /**
     * Sets the current accelerometer values as the "flat" reference.
     */


    override fun process(entityId: Int) {
        if (Globals.IsStarting) return
        if (Globals.deathScreen) return
        if (Globals.isPausing) return

        val input = inputMapper[entityId]
        val velocity = velocityMapper[entityId]
        val transform = transformMapper[entityId]
        println(transform.position)
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
            // Get adjusted accelerometer values based on calibration
            val rawAccelX = Gdx.input.accelerometerX
            val rawAccelY = Gdx.input.accelerometerY

            val accelX = rawAccelX - calibratedAccelX  // Left (-), Right (+) → Controls Y movement
            val accelY = -(rawAccelY - calibratedAccelY)  // Forward (-), Backward (+) → Controls X movement

            // Apply dead zone to remove noise
            val deadZone = 0.5f
            val adjustedX = if (kotlin.math.abs(accelX) > deadZone) accelX else 0f
            val adjustedY = if (kotlin.math.abs(accelY) > deadZone) accelY else 0f

            // Set tilt direction (inverted signs to match movement)
            input.tiltDirection.set(-adjustedY, -adjustedX)

            // Normalize tilt direction
            val tiltMagnitude = input.tiltDirection.len()
            if (tiltMagnitude > 1f) {
                input.tiltDirection.nor()
            }

            // Scale acceleration dynamically based on tilt intensity
            val dynamicAcceleration = (tiltMagnitude * tiltMagnitude) * accelerationFactor

            // Apply acceleration
            velocity.acceleration.set(input.tiltDirection.x * dynamicAcceleration, input.tiltDirection.y * dynamicAcceleration)

            // Integrate acceleration into velocity
            velocity.velocity.x += velocity.acceleration.x * world.delta
            velocity.velocity.y += velocity.acceleration.y * world.delta

            // Cap velocity to prevent excessive speed
            if (velocity.velocity.len() > velocityCap) {
                velocity.velocity.nor().scl(velocityCap)
            }

            // Apply damping for smooth deceleration
            velocity.velocity.scl(dampingFactor)
        }
        // 🔹 Calculate player rotation based on movement direction
        if (velocity.velocity.len2() > 0.01f) { // Avoid random rotations when nearly stopped
            transform.rotation = Math.toDegrees(kotlin.math.atan2(velocity.velocity.y, velocity.velocity.x)
                .toDouble()).toFloat() - 90f
        }
    }
}
