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
import com.csd3156.group11.components.PlayerInputComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.enums.GameState
import com.csd3156.group11.resources.Globals

class EnemyManagerSystem(
    private val threshold: Int = 20,
    interval: Float = 2f
) : IntervalEntitySystem(Aspect.all(EnemyComponent::class.java), interval) {

    override fun processSystem() {
        if (Globals.currentState != GameState.GAME_STAGE) return
        if (Globals.IsStarting) return
        if (Globals.deathScreen) return


        // Query for active enemy entities.
        val enemyEntities: IntBag =
            world.aspectSubscriptionManager.get(Aspect.all(EnemyComponent::class.java)).entities
        val playerEntities = world.aspectSubscriptionManager
            .get(
                Aspect.all(
                    PlayerInputComponent::class.java,
                    TransformComponent::class.java
                )
            ).entities
        val playerCenter: Vector2 = if (playerEntities.size() > 0) {
            val playerTransform = world.getMapper(TransformComponent::class.java)
                .get(playerEntities.get(0))
            playerTransform.position.cpy()
        } else {
            Vector2(17.5f, 3f)
        }

        // If enemy count is below threshold, spawn a new formation.
        if (enemyEntities.size() < threshold) {
            val defaultRoll = MathUtils.random(0f, 1f)
            val defaultFormation: EnemyFormation = when {
                defaultRoll < 0.8f -> EnemyFormation.NONE
                defaultRoll < 0.9f -> EnemyFormation.CIRCLE
                else -> EnemyFormation.GRID
            }
            val defaultCount = MathUtils.random(10, 20)
            val defaultCenter: Vector2 = if (defaultFormation == EnemyFormation.CIRCLE || defaultFormation == EnemyFormation.GRID) {
                playerCenter
            } else {
                Vector2(17.5f, 3f)
            }
            val defaultSpawnEntity = world.create()
            world.edit(defaultSpawnEntity)
                .add(EnemySpawnerComponent(defaultFormation, defaultCount, defaultCenter))

            val specialCandidates = listOf(
                EnemyFormation.TOP_BOTTOM,
                EnemyFormation.LEFT_RIGHT,
                EnemyFormation.ALL_EDGES
            )
            val specialChance = 0.2f
            val roll = MathUtils.random(0f, 1f)
            if (roll < specialChance) {
                val specialFormation = specialCandidates.random()
                val specialCount = MathUtils.random(10, 20)
                val specialCenter = Vector2(17.5f, 3f)
                val specialSpawnEntity = world.create()
                world.edit(specialSpawnEntity)
                    .add(EnemySpawnerComponent(specialFormation, specialCount, specialCenter))
            }
        }
    }
}


