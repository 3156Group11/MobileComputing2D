import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import kotlin.random.Random

class EmitterSystem(private val screenWidth: Float, private val screenHeight: Float) : BaseEntitySystem(Aspect.all(EmitterComponent::class.java)) {

    private lateinit var mEmitter: ComponentMapper<EmitterComponent>
    private var isFirstRun = true // Flag to check if it's the first frame

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

    private fun spawnParticle(emitter: EmitterComponent, prePopulate: Boolean = false) {
        val entity = world.create()
        val particle = world.edit(entity).create(ParticleComponent::class.java)

        // Spawn at random positions
        particle.position.set(
            Random.nextFloat() * screenWidth,
            Random.nextFloat() * screenHeight
        )

        // Move in random direction
        particle.velocity.set(
            Random.nextFloat() * 2 - 1, // -1 to 1
            Random.nextFloat() * 2 - 1
        ).nor().scl(emitter.particleSpeed)

        particle.lifeTime = emitter.particleLifeTime
        particle.size = Random.nextFloat() * 3f + 2f // Size between 2 and 5

        // If pre-populating, set the elapsed time randomly so particles are in different states
        if (prePopulate) {
            particle.elapsedTime = Random.nextFloat() * particle.lifeTime
        }
    }
}
