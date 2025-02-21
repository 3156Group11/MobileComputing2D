import com.artemis.Component
import com.badlogic.gdx.math.Vector2

class EmitterComponent : Component() {
    var position = Vector2()
    var emissionRate = 10f // Particles per second
    var particleLifeTime = 1f
    var particleSpeed = 50f
    var lastEmissionTime = 0f
    var point = false
}
