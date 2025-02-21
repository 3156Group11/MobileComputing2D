package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.EnemyLineComponent
import com.csd3156.group11.components.EnemySpawnerComponent
import com.csd3156.group11.enums.EnemyFormation
import com.csd3156.group11.prefabs.EnemyBasic
import com.csd3156.group11.prefabs.EnemyLine
import com.csd3156.group11.resources.Globals

class EnemySpawnerSystem : BaseEntitySystem(Aspect.all(EnemySpawnerComponent::class.java)) {

    private lateinit var spawnMapper: ComponentMapper<EnemySpawnerComponent>
    private val screenWidth = 35f
    private val screenHeight = 35 * Globals.scrHeight / Globals.scrWidth

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
                    val rows = 5
                    val cols = 10
                    val spacingX = 4f
                    val spacingY = 4f

                    // Calculate total grid dimensions:
                    val gridWidth = (cols - 1) * spacingX
                    val gridHeight = (rows - 1) * spacingY

                    // Compute screen center and then top-left starting position for the grid.
                    val centerX = screenWidth / 2
                    val centerY = screenHeight / 2

                    // startX is set so that the grid is horizontally centered
                    // startY is set so that the grid is vertically centered (with the top row at startY)
                    val startX = centerX - gridWidth / 2
                    val startY = centerY + gridHeight / 2

                    // Spawn 5 rows x 10 columns = 50 enemies
                    for (r in 0 until rows) {
                        for (c in 0 until cols) {
                            val enemy = EnemyBasic()
                            enemy.Create(world)
                            enemy.transform.position.set(startX + c * spacingX, startY - r * spacingY)
                            // Mark enemy as in formation and adjust speed.
                            val enemyMapper = world.getMapper(EnemyComponent::class.java)
                            val comp = enemyMapper.get(enemy.ID)
                            comp.inFormation = true
                            comp.speed = 50f
                        }
                    }
                }
                EnemyFormation.CIRCLE -> {
                    val center = spawner.center
                    val radius = 5f
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
                    val halfCount = 10
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
                }
                EnemyFormation.LEFT_RIGHT -> {
                    val halfCount = 10
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
                }
                EnemyFormation.ALL_EDGES -> {
                    val topbottomedge = 10
                    val leftrightedge = 5
                    val spacingX = screenWidth / (topbottomedge + 1)
                    val spacingY = screenHeight / (leftrightedge + 1)
                    // Top edge:
                    for (j in 0 until topbottomedge) {
                        val enemy = EnemyLine()
                        enemy.Create(world)
                        enemy.transform.position.set(spacingX * (j + 1), screenHeight)
                        enemy.velocity.velocity.set(0f, -30f)
                        val lineComponent = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                        lineComponent.spawnEdge = 2
                    }
                    // Bottom edge:
                    for (j in 0 until topbottomedge) {
                        val enemy = EnemyLine()
                        enemy.Create(world)
                        enemy.transform.position.set(spacingX * (j + 1), 0f)
                        enemy.velocity.velocity.set(0f, 30f)
                        val lineComponent = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                        lineComponent.spawnEdge = 3
                    }
                    // Left edge:
                    for (j in 0 until leftrightedge) {
                        val enemy = EnemyLine()
                        enemy.Create(world)
                        enemy.transform.position.set(0f, spacingY * (j + 1))
                        enemy.velocity.velocity.set(30f, 0f)
                        val lineComponent = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                        lineComponent.spawnEdge = 0
                    }
                    // Right edge:
                    for (j in 0 until leftrightedge) {
                        val enemy = EnemyLine()
                        enemy.Create(world)
                        enemy.transform.position.set(screenWidth, spacingY * (j + 1))
                        enemy.velocity.velocity.set(-30f, 0f)
                        val lineComponent = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                        lineComponent.spawnEdge = 1
                    }
                }
            }

            world.delete(entity)
        }
    }
}
