import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import kotlin.random.Random

class EmitterSystem(private val screenWidth: Float, private val screenHeight: Float) : BaseEntitySystem(Aspect.all(EmitterComponent::class.java)) {

    private lateinit var mEmitter: ComponentMapper<EmitterComponent>

    override fun processSystem() {
        val delta = world.delta
        val entities = subscription.entities

        for (i in 0 until entities.size()) {
            val emitter = mEmitter[entities[i]]

            emitter.lastEmissionTime += delta
            val emissionInterval = 1f / emitter.emissionRate

            while (emitter.lastEmissionTime >= emissionInterval) {
                emitter.lastEmissionTime -= emissionInterval
                spawnParticle(emitter)
            }
        }
    }

    private fun spawnParticle(emitter: EmitterComponent) {
        val entity = world.create()
        val particle = world.edit(entity).create(ParticleComponent::class.java)
        if (emitter.point == true)
        {
            particle.position.set(emitter.position)
        }
        else
        {
            particle.position.set(
                Random.nextFloat() * screenWidth,
                Random.nextFloat() * screenHeight
            )
        }
        particle.velocity.set(
            Random.nextFloat() * 2 - 1, // Random direction (-1 to 1)
            Random.nextFloat() * 2 - 1
        ).nor().scl(emitter.particleSpeed)

        particle.lifeTime = emitter.particleLifeTime
        particle.size = 5f // Fixed particle size
    }
}
