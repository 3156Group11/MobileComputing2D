package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.ColliderComponent
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.EnemyLineComponent
import com.csd3156.group11.components.EnemySpawnerComponent
import com.csd3156.group11.components.PlayerInputComponent
import com.csd3156.group11.components.PowerUpComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.enums.EnemyFormation
import com.csd3156.group11.prefabs.EnemyBasic
import com.csd3156.group11.prefabs.EnemyLine
import kotlin.math.ceil
import kotlin.math.sqrt

class EnemySpawnerSystem : BaseEntitySystem(Aspect.all(EnemySpawnerComponent::class.java)) {

    private lateinit var spawnMapper: ComponentMapper<EnemySpawnerComponent>
    private val screenWidth = 800f
    private val screenHeight = 400f

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
                        val enemyMapper = world.getMapper(EnemyComponent::class.java)
                        val comp = enemyMapper.get(enemy.ID)
                        comp.speed = 50f
                    }
                }
                EnemyFormation.GRID -> {
                    val rows = ceil(sqrt(spawner.count.toDouble())).toInt()
                    val cols = ceil(spawner.count.toDouble() / rows).toInt()
                    // Define starting position and spacing
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
                            val comp = enemyMapper.get(enemy.ID)
                            comp.inFormation = true
                            comp.speed = 50f
                            spawned++
                        }
                    }
                }
                EnemyFormation.CIRCLE -> {
                    val center = spawner.center
                    val radius = 100f
                    for (j in 0 until spawner.count) {
                        val angle = j * (360f / spawner.count)
                        val rad = angle * com.badlogic.gdx.math.MathUtils.degreesToRadians
                        val x = center.x + radius * com.badlogic.gdx.math.MathUtils.cos(rad)
                        val y = center.y + radius * com.badlogic.gdx.math.MathUtils.sin(rad)
                        val enemy = EnemyBasic()
                        enemy.Create(world)
                        enemy.transform.position.set(x, y)
                        val enemyMapper = world.getMapper(EnemyComponent::class.java)
                        val comp = enemyMapper.get(enemy.ID)
                        comp.inFormation = true
                        comp.speed = 50f
                    }
                }
                EnemyFormation.TOP_BOTTOM -> {
                    val halfCount = spawner.count / 2
                    val spacingX = screenWidth / (halfCount + 1)
                    // Top edge:
                    for (j in 0 until halfCount) {
                        val enemy = EnemyLine()
                        enemy.Create(world)
                        enemy.transform.position.set(spacingX * (j + 1), screenHeight)
                        // Set velocity so it moves downward.
                        enemy.velocity.velocity.set(0f, -30f)
                        val lineComponent = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                        lineComponent.spawnEdge = 2
                    }
                    // Bottom edge:
                    for (j in 0 until halfCount) {
                        val enemy = EnemyLine()
                        enemy.Create(world)
                        enemy.transform.position.set(spacingX * (j + 1), 0f)
                        // Set velocity so it moves upward.
                        enemy.velocity.velocity.set(0f, 30f)
                        val lineComponent = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                        lineComponent.spawnEdge = 3
                    }
                    if (spawner.count % 2 == 1) {
                        val enemy = EnemyLine()
                        enemy.Create(world)
                        enemy.transform.position.set(screenWidth / 2, screenHeight / 2)
                        // Set a default velocity if needed.
                        enemy.velocity.velocity.set(0f, 0f)
                    }
                }
                EnemyFormation.LEFT_RIGHT -> {
                    val halfCount = spawner.count / 2
                    val spacingY = screenHeight / (halfCount + 1)
                    // Left edge:
                    for (j in 0 until halfCount) {
                        val enemy = EnemyLine()
                        enemy.Create(world)
                        enemy.transform.position.set(0f, spacingY * (j + 1))
                        // Set velocity to move right.
                        enemy.velocity.velocity.set(30f, 0f)
                        val lineComponent = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                        lineComponent.spawnEdge = 0
                    }
                    // Right edge:
                    for (j in 0 until halfCount) {
                        val enemy = EnemyLine()
                        enemy.Create(world)
                        enemy.transform.position.set(screenWidth, spacingY * (j + 1))
                        // Set velocity to move left.
                        enemy.velocity.velocity.set(-30f, 0f)
                        val lineComponent = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                        lineComponent.spawnEdge = 1
                    }
                    if (spawner.count % 2 == 1) {
                        val enemy = EnemyLine()
                        enemy.Create(world)
                        enemy.transform.position.set(screenWidth / 2, screenHeight / 2)
                        enemy.velocity.velocity.set(0f, 0f)
                    }
                }
                EnemyFormation.ALL_EDGES -> {
                    val perEdge = spawner.count / 4
                    val remainder = spawner.count % 4
                    val spacingX = screenWidth / (perEdge + 1)
                    val spacingY = screenHeight / (perEdge + 1)
                    // Top edge:
                    for (j in 0 until perEdge) {
                        val enemy = EnemyLine()
                        enemy.Create(world)
                        enemy.transform.position.set(spacingX * (j + 1), screenHeight)
                        enemy.velocity.velocity.set(0f, -30f)
                        val lineComponent = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                        lineComponent.spawnEdge = 2
                    }
                    // Bottom edge:
                    for (j in 0 until perEdge) {
                        val enemy = EnemyLine()
                        enemy.Create(world)
                        enemy.transform.position.set(spacingX * (j + 1), 0f)
                        enemy.velocity.velocity.set(0f, 30f)
                        val lineComponent = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                        lineComponent.spawnEdge = 3
                    }
                    // Left edge:
                    for (j in 0 until perEdge) {
                        val enemy = EnemyLine()
                        enemy.Create(world)
                        enemy.transform.position.set(0f, spacingY * (j + 1))
                        enemy.velocity.velocity.set(30f, 0f)
                        val lineComponent = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                        lineComponent.spawnEdge = 0
                    }
                    // Right edge:
                    for (j in 0 until perEdge) {
                        val enemy = EnemyLine()
                        enemy.Create(world)
                        enemy.transform.position.set(screenWidth, spacingY * (j + 1))
                        enemy.velocity.velocity.set(-30f, 0f)
                        val lineComponent = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                        lineComponent.spawnEdge = 1
                    }
                    // Spawn any remainder in the center.
                    for (j in 0 until remainder) {
                        val enemy = EnemyLine()
                        enemy.Create(world)
                        enemy.transform.position.set(screenWidth / 2, screenHeight / 2)
                        enemy.velocity.velocity.set(0f, 0f)
                    }
                }
            }

            world.delete(entity)
        }
    }
}
