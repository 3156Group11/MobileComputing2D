/**
 * @file EmitterComponent.kt
 * @brief  Holds the Emitter Component and its data members and methods
 */
import com.artemis.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2

/**
 * @brief Component for particle emitters.
 *
 * The EmitterComponent is used to configure particle effects for an entity.
 * It defines properties such as the emission rate, particle lifetime, speed,
 * size, and optionally a texture to use. The emitter can either spawn particles
 * at random positions or at a specified point.
 */
class EmitterComponent : Component() {
    var position = Vector2()
    var emissionRate = 10f // Particles per second
    var particleLifeTime = 1f
    var particleSpeed = 50f
    var lastEmissionTime = 0f
    var particleSize = 3f
    var point = false
    var texture : TextureRegion? = null
}
