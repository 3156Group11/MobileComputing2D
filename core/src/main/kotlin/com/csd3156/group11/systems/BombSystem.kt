/**
 * @file BombSystem.kt
 * @brief Manages the behavior of bombs within the game, including their activation, timing, and effects on enemies.
 *
 * The BombSystem handles bomb placement, countdown timers, and interactions with enemy entities.
 * When a bomb expires, it checks for nearby enemies and applies damage if they are within range.
 */
package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.PowerUpComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.enums.Tag
import com.csd3156.group11.components.TagComponent

/**
 * @class BombSystem
 * @brief Handles the placement, countdown, and detonation of bombs in the game.
 *
 * This system manages all active bombs, reducing their timers each frame and checking for enemy proximity.
 * When a bomb detonates, it damages or eliminates enemies within its blast radius.
 */
class BombSystem : BaseEntitySystem(
    Aspect.all(PowerUpComponent::class.java)
) {
    private lateinit var powerUpMapper: ComponentMapper<PowerUpComponent>
    private lateinit var transformMapper: ComponentMapper<TransformComponent>
    private lateinit var enemyMapper: ComponentMapper<EnemyComponent>
    private lateinit var tagMapper: ComponentMapper<TagComponent>

    /**
     * @brief Processes all active bombs in the game.
     *
     * Iterates through all bombs, reduces their timers, and checks for nearby enemies upon detonation.
     */
    override fun processSystem() {
        val enemyEntities = world.aspectSubscriptionManager
            .get(Aspect.all(EnemyComponent::class.java, TransformComponent::class.java, TagComponent::class.java))
            .entities

        val entitiesWithPowerUps = subscription.entities
        for (i in 0 until entitiesWithPowerUps.size()) {
            val entityId = entitiesWithPowerUps[i]
            val pwrComp = powerUpMapper[entityId]

            val bombsToRemove = mutableListOf<PowerUpComponent.BombEntry>()

            for (bomb in pwrComp.bombs) {
                bomb.timeLeft -= world.delta

                if (bomb.timeLeft <= 0f) {
                    bombsToRemove.add(bomb)
                    //println("Bomb expired at position: ${bomb.center}")
                    continue
                }

                //println("Active Bomb at ${bomb.center} with radius ${bomb.radius}")

                for (j in 0 until enemyEntities.size()) {
                    val enemyId = enemyEntities[j]
                    val enemyTransform = transformMapper.get(enemyId)
                    val enemyComponent = enemyMapper.get(enemyId)
                    val enemyTag = tagMapper.get(enemyId)

                    // Debug enemy tag
                    //println("Checking Enemy $enemyId - Tag: ${enemyTag.tag}")

                    if (enemyTag.tag != Tag.ENEMY) continue

                    // Debug position values
                    //println("Enemy $enemyId position: ${enemyTransform.position}")

                    val distance = Vector2.dst(
                        bomb.center.x, bomb.center.y,
                        enemyTransform.position.x, enemyTransform.position.y
                    )

                    //println(" Distance from bomb to enemy $enemyId: $distance (Radius: ${bomb.radius})")

                    if (distance < bomb.radius + 1.8f) {
                        if (!enemyComponent.isDying) {
                            enemyComponent.isDying = true
                            //enemyComponent.DyingTime = 0f // Instant kill
                            //world.delete(enemyId) // Directly remove the enemy
                            //println("Enemy $enemyId killed by bomb!")
                        }
                    }
                }
            }

            pwrComp.bombs.removeAll(bombsToRemove)
        }
    }
}
