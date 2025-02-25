/**
 * @file ParticleComponent.kt
 * @brief  Holds the Particle Component and its data members and methods
 */
import com.artemis.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2

/**
 * @brief Component representing an individual particle in the particle system.
 *
 * The ParticleComponent holds all the necessary data for a particle's behavior,
 * including its position, velocity, lifespan, elapsed time, size, and the texture region used for rendering.
 */
class ParticleComponent : Component() {
    var position = Vector2()
    var velocity = Vector2()
    var lifeTime = 1f // Particle lifespan in seconds
    var elapsedTime = 0f
    var size = 1f
    var texture : TextureRegion? = null
}
