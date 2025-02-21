import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.artemis.WorldConfigurationBuilder
import com.artemis.systems.IntervalEntitySystem
import com.artemis.utils.IntBag
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.EnemySpawnerComponent
import com.csd3156.group11.enums.EnemyFormation
import com.artemis.annotations.Wire

@Wire
class EnemyManagerSystem(
    private val threshold: Int = 20,
    interval: Float = 5f
) : IntervalEntitySystem(Aspect.all(EnemyComponent::class.java), interval) {

    private lateinit var enemyMapper: ComponentMapper<EnemyComponent>

    override fun processSystem() {
        // Use the subscription's entities as your set to process.
        val entities = subscription.entities
        if (entities.size() < threshold) {
            // Randomize formation parameters
            val formationTypes = EnemyFormation.values()
            val randomFormation = formationTypes[MathUtils.random(formationTypes.size - 1)]
            val count = MathUtils.random(10, 20)
            val center = Vector2(MathUtils.random(200f, 600f), MathUtils.random(100f, 300f))

            // Create spawn request
            val spawnEntity = world.create()
            world.edit(spawnEntity).add(EnemySpawnerComponent(randomFormation, count, center))

            println("Spawn request: formation=$randomFormation, count=$count, center=$center")
        }
    }
}


