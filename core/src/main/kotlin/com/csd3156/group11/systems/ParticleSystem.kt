import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2

class ParticleSystem(private val batch: SpriteBatch, private val texture: TextureRegion) :
    BaseEntitySystem(Aspect.all(ParticleComponent::class.java)) {

    private lateinit var mParticle: ComponentMapper<ParticleComponent>

    override fun processSystem() {
        val delta = world.delta
        val entities = subscription.entities

        batch.begin()
        for (i in 0 until entities.size()) {
            val particle = mParticle[entities[i]]

            // Update particle position
            particle.position.add(particle.velocity.x * delta, particle.velocity.y * delta)

            // Update lifetime
            particle.elapsedTime += delta
            if (particle.elapsedTime >= particle.lifeTime) {
                world.delete(entities[i]) // Remove expired particle
                continue
            }

            // Draw particle
            if (particle.texture == null)
                batch.draw(texture, particle.position.x, particle.position.y, particle.size, particle.size)
            else
                batch.draw(particle.texture, particle.position.x, particle.position.y, particle.size, particle.size)
        }
        batch.end()
    }
}
