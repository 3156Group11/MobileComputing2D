package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.EnemyBasicComponent
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.EnemyLineComponent
import com.csd3156.group11.components.PlayerInputComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.VelocityComponent
import com.csd3156.group11.resources.Globals

class EnemySystem : BaseEntitySystem(Aspect.all(EnemyComponent::class.java, EnemyBasicComponent::class.java, VelocityComponent::class.java, TransformComponent::class.java)
) {
    private lateinit var velocityMapper: ComponentMapper<VelocityComponent>
    private lateinit var enemyMapper: ComponentMapper<EnemyComponent>
    private lateinit var transformMapper: ComponentMapper<TransformComponent>
    private lateinit var enemyBasicMapper: ComponentMapper<EnemyBasicComponent>
    private lateinit var playerInputMapper: ComponentMapper<PlayerInputComponent>

    private val entitiesToDelete = mutableListOf<Int>()

    override fun processSystem() {
        // Get player's position
        val playerEntities = world.aspectSubscriptionManager.get(
            Aspect.all(PlayerInputComponent::class.java, TransformComponent::class.java)
        ).entities
        if (playerEntities.size() == 0) return
        val playerPos: Vector2 = transformMapper.get(playerEntities[0]).position

        val entities = subscription.entities
        for (i in 0 until entities.size()) {
            val entity = entities[i]
            val velocity = velocityMapper.get(entity)
            val transform = transformMapper.get(entity)
            // Retrieve the enemy component from one of the mappers.
            val enemy = enemyMapper.get(entity)

            // Handle enemy immunity on spawn.
            if (enemy.isImmune) {
                enemy.ImmuneTime -= world.delta
                val inScale = ((2f - (enemy.ImmuneTime))/2) * 0.75f
                transform.scale = Vector2(inScale,inScale)

                if (enemy.ImmuneTime < 0) {
                    transform.scale = Vector2(0.75f,0.75f)
                    enemy.isImmune = false
                    enemy.isLive = true
                }
                continue
            }
            // Handle dying state.
            else if (enemy.isDying) {
                enemy.DyingTime -= world.delta
                enemy.isLive = false
                val inScale = (((enemy.DyingTime))/2) * 0.75f
                transform.scale = Vector2(inScale,inScale)
                velocity.velocity = Vector2(0f,0f)
                if (enemy.DyingTime < 0) {
                    entitiesToDelete.add(entity)
                }
                continue
            }
            else {
                // Move enemy toward the actual player's position.
                val direction = playerPos.cpy().sub(transform.position).nor()
                velocity.velocity.set(direction.scl(enemy.speed))

            }
        }

        for (entity in entitiesToDelete) {
            world.delete(entity)
        }
        entitiesToDelete.clear()
    }
}

class EnemyLineSystem : BaseEntitySystem(
    Aspect.all(EnemyLineComponent::class.java, EnemyComponent::class.java, VelocityComponent::class.java, TransformComponent::class.java)
) {
    private lateinit var lineMapper: ComponentMapper<EnemyLineComponent>
    private lateinit var enemyMapper: ComponentMapper<EnemyComponent>
    private lateinit var transformMapper: ComponentMapper<TransformComponent>
    private lateinit var velocityMapper: ComponentMapper<VelocityComponent>
    private lateinit var playerInputMapper: ComponentMapper<PlayerInputComponent>

    private val entitiesToDelete = mutableListOf<Int>()

    private val screenWidth = 35f
    private val screenHeight = 35 * Globals.scrHeight / Globals.scrWidth

    override fun processSystem() {
        val playerEntities = world.aspectSubscriptionManager.get(
            Aspect.all(PlayerInputComponent::class.java, TransformComponent::class.java)
        ).entities
        val playerPos: Vector2? = if (playerEntities.size() > 0)
            transformMapper.get(playerEntities[0]).position else null

        val entities = subscription.entities
        for (i in 0 until entities.size()) {
            val entity = entities[i]
            val line = lineMapper.get(entity)
            val enemy = enemyMapper.get(entity)
            val transform = transformMapper.get(entity)
            val velocity = velocityMapper.get(entity)

            if (enemy.isImmune && enemy.formationSpawnComplete) {
                enemy.ImmuneTime -= world.delta
                val inScale = ((2f - (enemy.ImmuneTime))/2) * 0.75f
                transform.scale = Vector2(inScale,inScale)
                if (enemy.ImmuneTime < 0) {
                    transform.scale = Vector2(0.75f,0.75f)
                    enemy.isImmune = false
                    enemy.isLive = true
                }
                continue
            } else if (enemy.isDying) {
                enemy.DyingTime -= world.delta
                enemy.isLive = false
                velocity.velocity = Vector2(0f,0f)
                val inScale = (((enemy.DyingTime))/2) * 0.75f
                transform.scale = Vector2(inScale,inScale)
                if (enemy.DyingTime < 0) {
                    entitiesToDelete.add(entity)
                }
                continue
            }

            // Check if it has reached (or passed) the opposite side.
            else {
                // Check collision with player.
                when (line.spawnEdge) {
                    0 -> { // Spawned on left; moving right. If x > screenWidth, mark as dying.
                        if (transform.position.x > screenWidth) {
                            enemy.isDying = true
                        }
                    }
                    1 -> { // Spawned on right; moving left. If x < 0, mark as dying.
                        if (transform.position.x < 0f) {
                            enemy.isDying = true
                        }
                    }
                    2 -> { // Spawned on top; moving down. If y < 0, mark as dying.
                        if (transform.position.y < 0f) {
                            enemy.isDying = true
                        }
                    }
                    3 -> { // Spawned on bottom; moving up. If y > screenHeight, mark as dying.
                        if (transform.position.y > screenHeight) {
                            enemy.isDying = true
                        }
                    }
                }
            }
        }

        // Remove entities that have finished dying.
        for (entity in entitiesToDelete) {
            world.delete(entity)
        }
        entitiesToDelete.clear()
    }
}

