/**
 * @file EnemyManagerSystem.kt
 * @brief Manages enemy spawning and tracking enemy states in the game.
 *
 * This system controls the spawning of enemies based on thresholds, manages enemy kills,
 * and applies different spawn formations depending on player positioning and difficulty levels.
 */
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

/**
 * @class EnemySpawnerSystem
 * @brief Spawns enemies in different formations and patterns.
 *
 * This system processes enemy spawners, determines spawn positions, and creates enemies
 * based on predefined formations. It ensures that enemies appear in strategic locations
 * relative to the player and the game world.
 */
class EnemyManagerSystem(
    private val threshold: Int = 10,
    interval: Float = 5f
) : IntervalEntitySystem(Aspect.all(EnemyComponent::class.java), interval) {

    var easeInEnemy: Int = 8 // Number of enemies to spawn gradually at the start.
    var easeInEnemyCounter: Int = 0 // Counter for tracking spawned enemies during the ease-in phase.

    /**
     * @brief Processes enemy spawning logic.
     *
     * This method removes dying enemies, tracks enemy kill counts,
     * and spawns new enemies based on the game's progression.
     */
    override fun processSystem() {
        if (Globals.currentState != GameState.GAME_STAGE) return
        if (Globals.IsStarting) return
        if (Globals.deathScreen) return

        // Query for active enemy entities.
        val enemyEntities: IntBag =
            world.aspectSubscriptionManager.get(Aspect.all(EnemyComponent::class.java)).entities

        val enemyMapper = world.getMapper(EnemyComponent::class.java)

        // 🔹 Track enemy kills before deletion
        for (i in 0 until enemyEntities.size()) {
            val enemyId = enemyEntities[i]
            val enemy = enemyMapper.get(enemyId)

            if (enemy.isDying) { // If enemy is in dying state before deletion
                Globals.enemiesKilled++ // ✅ Increase enemy kill count
                world.delete(enemyId) // ✅ Remove enemy from world
            }
        }

        // Get player position to adjust enemy spawn location.
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

        // Handle ease-in phase enemy spawning.
        if (easeInEnemyCounter <= easeInEnemy) {
            val formation = EnemyFormation.NONE
            val count = 1  // spawn one enemy at a time
            val spawnInterval = 5f  // slower spawn rate for ease-in
            val spawnEntity = world.create()
            world.edit(spawnEntity).add(EnemySpawnerComponent(formation, count, playerCenter))
            // Override the spawn interval for this request.
            val spawner = world.getMapper(EnemySpawnerComponent::class.java).get(spawnEntity)
            spawner.spawnInterval = spawnInterval
            println("Ease-in spawn request: formation=$formation, count=$count, center=$playerCenter, interval=$spawnInterval")
            easeInEnemyCounter += count
            return  // Do not process other spawns during ease-in.
        }

        // If enemy count is below threshold, spawn a new formation.
        if (enemyEntities.size() < threshold) {
           /* val defaultRoll = MathUtils.random(0f, 1f)
            val defaultFormation: EnemyFormation = when {
                defaultRoll < 0.8f -> EnemyFormation.NONE
                defaultRoll < 0.9f -> EnemyFormation.CIRCLE
                else -> EnemyFormation.GRID
            }*/
            /*val defaultCount = MathUtils.random(8, 12)
            val defaultCenter: Vector2 = if (defaultFormation == EnemyFormation.CIRCLE || defaultFormation == EnemyFormation.GRID) {
                playerCenter
            } else {
                Vector2(17.5f, 3f)
            }*/

            val defaultRoll = MathUtils.random(0f, 1f)
            if (defaultRoll < 0.8f) {
                val defaultSpawnEntity = world.create()
                val defaultCount = 1
                world.edit(defaultSpawnEntity)
                    .add(EnemySpawnerComponent(EnemyFormation.NONE, defaultCount, Vector2(17.5f, 3f)))
            }

            val specialCandidates = listOf(
                EnemyFormation.TOP_BOTTOM,
                EnemyFormation.LEFT_RIGHT,
                EnemyFormation.ALL_EDGES,
                EnemyFormation.CIRCLE,
                EnemyFormation.GRID
            )

            val roll = MathUtils.random(0f, 1f)
            if (roll < 0.5f) {
                val specialFormation = specialCandidates.random()
                val defaultCount = MathUtils.random(8, 12)
                val specialCenter: Vector2 = if (specialFormation == EnemyFormation.CIRCLE || specialFormation == EnemyFormation.GRID) {
                    playerCenter
                } else {
                    Vector2(17.5f, 3f)
                }
                val specialSpawnEntity = world.create()
                world.edit(specialSpawnEntity)
                    .add(EnemySpawnerComponent(specialFormation, defaultCount, specialCenter))
            }
        }
    }
}


