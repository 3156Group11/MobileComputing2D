import com.artemis.Component
import com.badlogic.gdx.math.Vector2

class ParticleComponent : Component() {
    var position = Vector2()
    var velocity = Vector2()
    var lifeTime = 1f // Particle lifespan in seconds
    var elapsedTime = 0f
    var size = 1f
}
