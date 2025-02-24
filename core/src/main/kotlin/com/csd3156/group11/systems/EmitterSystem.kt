/**
 * @file EmitterSystem.kt
 * @brief Manages particle emissions and effects in the game.
 *
 * The EmitterSystem is responsible for creating and updating particles, ensuring
 * dynamic visual effects such as explosions, sparks, and other environmental feedback.
 */

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.csd3156.group11.resources.Globals
import kotlin.random.Random

/**
 * @class EmitterSystem
 * @brief Handles the generation and updating of particle effects.
 *
 * This system creates particle effects at specified locations and manages their
 * behavior over time, adjusting movement, lifetime, and appearance.
 */
class EmitterSystem(private val screenWidth: Float, private val screenHeight: Float) : BaseEntitySystem(Aspect.all(EmitterComponent::class.java)) {

    private lateinit var mEmitter: ComponentMapper<EmitterComponent>
    private var isFirstRun = true // Flag to check if it's the first frame

    /**
     * @brief Processes particle emissions every frame.
     *
     * This method updates active particles, emits new ones based on the emitter rate,
     * and ensures smooth visual transitions.
     */
    override fun processSystem() {
        val delta = world.delta
        val entities = subscription.entities

        for (i in 0 until entities.size()) {
            val emitter = mEmitter[entities[i]]

            if (isFirstRun) {
                // Pre-populate the screen with particles
                for (k in 0 until (emitter.emissionRate * emitter.particleLifeTime).toInt()) {
                    spawnParticle(emitter, prePopulate = true)
                }
                isFirstRun = false
            }

            emitter.lastEmissionTime += delta
            val emissionInterval = 1f / emitter.emissionRate

            while (emitter.lastEmissionTime >= emissionInterval) {
                emitter.lastEmissionTime -= emissionInterval
                spawnParticle(emitter)
            }
        }
    }

    /**
     * @brief Spawns a new particle with randomized movement and properties.
     * @param emitter The emitter component responsible for spawning particles.
     * @param prePopulate Whether this particle should be pre-initialized to create a smoother effect.
     */
    private fun spawnParticle(emitter: EmitterComponent, prePopulate: Boolean = false) {
        val entity = world.create()
        val particle = world.edit(entity).create(ParticleComponent::class.java)

        // Spawn at random positions
        if (emitter.point == false) {
            particle.position.set(
                Random.nextFloat() * screenWidth,
                Random.nextFloat() * screenHeight
            )
        }
        else
        {
            particle.position.set(Globals.WorldToScreen(emitter.position))
        }
        // Move in random direction
        particle.velocity.set(
            Random.nextFloat() * 2 - 1, // -1 to 1
            Random.nextFloat() * 2 - 1
        ).nor().scl(emitter.particleSpeed)

        particle.lifeTime = emitter.particleLifeTime
        particle.size = emitter.particleSize

        if (emitter.texture != null)
        {
            particle.texture = emitter.texture
        }
        // If pre-populating, set the elapsed time randomly so particles are in different states
        if (prePopulate) {
            particle.elapsedTime = Random.nextFloat() * particle.lifeTime
        }
    }
}
