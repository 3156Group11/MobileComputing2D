package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.MathUtils
import com.csd3156.group11.components.ColliderComponent
import com.csd3156.group11.components.EnemyBasicComponent
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.PlayerInputComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.VelocityComponent
import javax.xml.crypto.dsig.Transform

class EnemySystem : BaseEntitySystem(Aspect.all(VelocityComponent::class.java, TransformComponent::class.java)
    .one(EnemyComponent::class.java, EnemyBasicComponent::class.java)) {

    private lateinit var velocityMapper: ComponentMapper<VelocityComponent>
    private lateinit var enemyMapper: ComponentMapper<EnemyComponent>
    private lateinit var transformMapper: ComponentMapper<TransformComponent>
    private lateinit var enemyBasicMapper: ComponentMapper<EnemyBasicComponent>
    private lateinit var playerInputMapper: ComponentMapper<PlayerInputComponent>

    private val entitiesToDelete = mutableListOf<Int>()

    override fun processSystem() {
        // Get player's position (assuming one player entity exists)
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
            val enemy = enemyMapper.get(entity) ?: enemyBasicMapper.get(entity) ?: continue

            // Handle enemy immunity on spawn.
            if (enemy.isImmune) {
                enemy.ImmuneTime -= world.delta
                if (enemy.ImmuneTime < 0) {
                    enemy.isImmune = false
                    enemy.isLive = true
                }
                continue
            }
            // Handle dying state.
            else if (enemy.isDying) {
                enemy.DyingTime -= world.delta
                if (enemy.DyingTime < 0) {
                    enemy.isDying = false
                    enemy.isLive = false
                }
                // Optionally play dying animation here before deletion.
                entitiesToDelete.add(entity)
                continue
            }
            else {
                // Move enemy toward the actual player's position.
                val direction = playerPos.cpy().sub(transform.position).nor()
                velocity.velocity.set(direction.scl(30f))  // Adjust speed as needed.

                // Check if enemy has reached the player.
                val collisionThreshold = 20f  // Adjust based on your game's scale.
                if (transform.position.dst(playerPos) < collisionThreshold) {
                    enemy.isDying = true
                    enemy.isLive = false
                }
            }
        }

        // Delete enemies marked for removal.
        for (entity in entitiesToDelete) {
            world.delete(entity)
        }
        entitiesToDelete.clear()
    }
}
