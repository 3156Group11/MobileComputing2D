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
                    val rows = ceil(sqrt(spawner.count.toDouble())).toInt()
                    val cols = ceil(spawner.count.toDouble() / rows).toInt()
                    // Define starting position and spacing (you can adjust these values)
                    val startX = 100f
                    val startY = 300f
                    val spacingX = 50f
                    val spacingY = 50f
                    var spawned = 0
                    for (r in 0 until rows) {
                        for (c in 0 until cols) {
                            if (spawned >= spawner.count) break
                            val enemy = EnemyBasic()
                            enemy.Create(world)
                            enemy.transform.position.set(startX + c * spacingX, startY - r * spacingY)
                            // Mark enemy as in formation, if needed:
                            val enemyMapper = world.getMapper(EnemyComponent::class.java)
                            enemyMapper.get(enemy.ID).inFormation = true
                            spawned++
                        }
                    }
                }
                EnemyFormation.CIRCLE -> {
                    val center = spawner.center ?: Vector2(400f, 240f)
                    val radius = 100f // adjust radius as needed
                    for (j in 0 until spawner.count) {
                        val angle = j * (360f / spawner.count)
                        val rad = angle * com.badlogic.gdx.math.MathUtils.degreesToRadians
                        val x = center.x + radius * com.badlogic.gdx.math.MathUtils.cos(rad)
                        val y = center.y + radius * com.badlogic.gdx.math.MathUtils.sin(rad)
                        val enemy = EnemyBasic()
                        enemy.Create(world)
                        enemy.transform.position.set(x, y)
                        val enemyMapper = world.getMapper(EnemyComponent::class.java)
                        enemyMapper.get(enemy.ID).inFormation = true
                    }
                }
            }

            world.delete(entity)
        }
    }
}
