package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.MathUtils
import com.csd3156.group11.components.ColliderComponent
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.VelocityComponent
import javax.xml.crypto.dsig.Transform

class EnemySystem : BaseEntitySystem(Aspect.all(EnemyComponent::class.java, VelocityComponent::class.java, TransformComponent::class.java)) {

    private lateinit var velocityMapper: ComponentMapper<VelocityComponent>
    private lateinit var enemyMapper: ComponentMapper<EnemyComponent>
    private lateinit var transformMapper: ComponentMapper<TransformComponent>

    private val entitiesToDelete = mutableListOf<Int>()

    override fun processSystem() {
        val entities = subscription.entities

        for (i in 0 until entities.size()) {
            val entityA = entities[i]
            val velocityA = velocityMapper[entityA]
            val enemyA = enemyMapper[entityA]
            val transformA = transformMapper[entityA]
            //println(enemyA.ImmuneTime.toString() + " " + velocityA.velocity)
            // Immunity for when enemy first spawn
            if (enemyA.isImmune)
            {
                enemyA.ImmuneTime-= world.getDelta()
                if (enemyA.ImmuneTime < 0)
                {
                    enemyA.isImmune = false
                    enemyA.isLive = true
                }
                continue
            }

            else if (enemyA.isDying)
            {
                enemyA.DyingTime-= world.getDelta()
                if (enemyA.DyingTime < 0)
                {
                    enemyA.isDying = false
                    enemyA.isLive = false
                }
                // play dying animation here
                entitiesToDelete.add(entityA)
                continue
            }

            else // move towards player
            {
                val playerPos : Vector2 = Vector2(0f,0f) // tmp for player pos
                val direction = playerPos.cpy().sub(transformA.position)
                direction.nor()
                velocityA.velocity = direction.scl(30f) // tmp speed
            }

        }

        for (entity in entitiesToDelete) {
            world.delete(entity)
        }
        entitiesToDelete.clear()  // Clear the list after deletion
    }

}
