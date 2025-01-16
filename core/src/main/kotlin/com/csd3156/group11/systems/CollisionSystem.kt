package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.MathUtils
import com.csd3156.group11.components.ColliderComponent
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.PlayerInputComponent
import com.csd3156.group11.components.TransformComponent

class CollisionSystem : BaseEntitySystem(Aspect.all(ColliderComponent::class.java, TransformComponent::class.java)) {

    private lateinit var transformMapper: ComponentMapper<TransformComponent>
    private lateinit var colliderMapper: ComponentMapper<ColliderComponent>

    override fun processSystem() {
        val entities = subscription.entities

        for (i in 0 until entities.size()) {
            val entityA = entities[i]
            val transformA = transformMapper[entityA]
            val colliderA = colliderMapper[entityA]

            for (j in i + 1 until entities.size()) {
                val entityB = entities[j]
                val transformB = transformMapper[entityB]
                val colliderB = colliderMapper[entityB]

                if (isColliding(transformA, colliderA, transformB, colliderB)) {
                    handleCollision(entityA, entityB)
                }
            }
        }
    }

    private fun isColliding(
        transformA: TransformComponent, colliderA: ColliderComponent,
        transformB: TransformComponent, colliderB: ColliderComponent
    ): Boolean {
        val distance = Vector2.dst(
            transformA.position.x, transformA.position.y,
            transformB.position.x, transformB.position.y
        )
        return distance < (colliderA.radius + colliderB.radius)
    }

    private fun handleCollision(entityA: Int, entityB: Int) {

        // Player and Powerup
        // Player and Enemy
        // Enemy and Destructive objects
        // Player
        if (world.getEntity(entityA).getComponent(PlayerInputComponent::class.java) != null)
        {
            if (world.getEntity(entityB).getComponent(EnemyComponent::class.java) != null)
            {
                // player dies
                return
            }
            // else if powerup, TODO: Add powerup component
            else if (world.getEntity(entityB).getComponent(EnemyComponent::class.java) != null)
            {
                // Activate Powerup
                return
            }
        }

        // Player
        else if (world.getEntity(entityB).getComponent(PlayerInputComponent::class.java) != null)
        {
            if (world.getEntity(entityA).getComponent(EnemyComponent::class.java) != null)
            {
                // player dies
                return
            }
            // else if powerup, TODO: Add powerup component
            else if (world.getEntity(entityA).getComponent(EnemyComponent::class.java) != null)
            {
                // Activate Powerup
                return
            }
        }



    }
}
