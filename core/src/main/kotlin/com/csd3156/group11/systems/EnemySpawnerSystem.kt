package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.ColliderComponent
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.EnemySpawnerComponent
import com.csd3156.group11.components.PlayerInputComponent
import com.csd3156.group11.components.PowerUpComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.enums.EnemyFormation
import com.csd3156.group11.prefabs.EnemyBasic
import kotlin.math.ceil
import kotlin.math.sqrt

class EnemySpawnerSystem : BaseEntitySystem(Aspect.all(EnemySpawnerComponent::class.java)) {

    private lateinit var spawnMapper: ComponentMapper<EnemySpawnerComponent>

    override fun processSystem() {
        val entities = subscription.entities

        for (i in 0 until entities.size()) {
            val entity = entities[i]
            val spawner = spawnMapper[entity]

            println("Processing spawn request: formation=${spawner.enemyFormation}, count=${spawner.count}, center=${spawner.center}")
            when (spawner.enemyFormation) {
                EnemyFormation.NONE -> {
                    for (j in 0 until spawner.count) {
                        println("Spawning enemy #$j in NONE formation")
                        val enemy = EnemyBasic()
                        enemy.Create(world)
                    }
                }
                EnemyFormation.GRID -> {

                }
                EnemyFormation.CIRCLE -> {

                }
            }

            world.delete(entity)
        }
    }
}
