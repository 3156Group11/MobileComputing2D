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
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.SpawnTask
import com.csd3156.group11.enums.GameState

class EnemySpawnerSystem : BaseEntitySystem(Aspect.all(EnemySpawnerComponent::class.java)) {

    private lateinit var spawnMapper: ComponentMapper<EnemySpawnerComponent>
    private val screenWidth = 35f
    private val screenHeight = 35 * Globals.scrHeight / Globals.scrWidth

    override fun processSystem() {
        if (Globals.IsStarting) return
        if (Globals.currentState != GameState.GAME_STAGE) return
        if (Globals.deathScreen) return
        if (Globals.isPausing) return

        val entities = subscription.entities

        for (i in 0 until entities.size()) {
            val entity = entities[i]
            val spawner = spawnMapper[entity]

            if (spawner.spawnTasks.isEmpty()) {
                when (spawner.enemyFormation) {
                    EnemyFormation.NONE -> {
                        // For NONE, spawn EnemyBasic at random positions.
                        /*for (j in 0 until spawner.count) {*/
                            val pos = Vector2(MathUtils.random(0.5f, screenWidth - 1.5f), MathUtils.random(0.5f, screenHeight - 1.5f))
                            spawner.spawnTasks.add(SpawnTask("basic", pos, Vector2(0f, 0f), 0.5f))
                        /*}*/
                    }
                    EnemyFormation.GRID -> {
                        val rows = 3
                        val cols = 6
                        val spacingX = 3.8f
                        val spacingY = 4f
                        val gridWidth = (cols - 1) * spacingX
                        val gridHeight = (rows - 1) * spacingY
                        val centerX = 17.2f
                        val centerY = 7.2f
                        val startX = centerX - gridWidth / 2
                        val startY = centerY + gridHeight / 2
                        for (r in 0 until rows) {
                            for (c in 0 until cols) {
                                val pos = Vector2(startX + c * spacingX, startY - r * spacingY)
                                spawner.spawnTasks.add(SpawnTask("basic", pos, Vector2(0f, 0f), 0.5f))
                            }
                        }
                    }
                    EnemyFormation.CIRCLE -> {
                        // For CIRCLE, use the spawner center (which should be set to the player's position).
                        val center = spawner.center
                        val radius = MathUtils.random(5f, 7f)
                        val count = MathUtils.random(8, 15)
                        for (j in 0 until count) {
                            val angle = j * (360f / count)
                            val rad = angle * MathUtils.degreesToRadians
                            val pos = Vector2(center.x + radius * MathUtils.cos(rad), center.y + radius * MathUtils.sin(rad))
                            spawner.spawnTasks.add(SpawnTask("basic", pos, Vector2(0f, 0f), 0.5f))
                        }
                    }
                    EnemyFormation.TOP_BOTTOM -> {
                        val halfCount = 8 // fixed for top and bottom edges
                        val spacingX = screenWidth / (halfCount + 1)
                        val spawnedEnemyIds = mutableListOf<Int>()

                        // Top edge:
                        for (j in 0 until halfCount) {
                            val enemy = EnemyLine()
                            enemy.Create(world)
                            enemy.transform.position.set(spacingX * (j + 1), 14.2f)
                            enemy.velocity.velocity.set(0f, -0.5f)
                            val lineComponent = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                            lineComponent.spawnEdge = 2
                            // Mark as part of a group
                            world.getMapper(EnemyComponent::class.java).get(enemy.ID).formationSpawnComplete = false
                            world.getMapper(EnemyComponent::class.java).get(enemy.ID).inFormation = true
                            spawnedEnemyIds.add(enemy.ID)
                        }
                        // Bottom edge:
                        for (j in 0 until halfCount) {
                            val enemy = EnemyLine()
                            enemy.Create(world)
                            enemy.transform.position.set(spacingX * (j + 1), 0.5f)
                            enemy.velocity.velocity.set(0f, 0.5f)
                            val lineComponent = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                            lineComponent.spawnEdge = 3
                            world.getMapper(EnemyComponent::class.java).get(enemy.ID).formationSpawnComplete = false
                            world.getMapper(EnemyComponent::class.java).get(enemy.ID).inFormation = true

                            spawnedEnemyIds.add(enemy.ID)
                        }
                        // After spawning the group, mark them all as ready to start immune countdown.
                        for (id in spawnedEnemyIds) {
                            world.getMapper(EnemyComponent::class.java).get(id).formationSpawnComplete = true
                            world.getMapper(EnemyComponent::class.java).get(id).inFormation = true

                        }
                    }
                    EnemyFormation.LEFT_RIGHT -> {
                        val halfCount = 5  // fixed for left/right edges
                        val spacingY = screenHeight / (halfCount + 1)
                        val spawnedEnemyIds = mutableListOf<Int>()

                        // Left edge:
                        for (j in 0 until halfCount) {
                            val enemy = EnemyLine()
                            enemy.Create(world)
                            enemy.transform.position.set(0.5f, spacingY * (j + 1))
                            enemy.velocity.velocity.set(0.5f, 0f)
                            val lineComponent = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                            lineComponent.spawnEdge = 0
                            world.getMapper(EnemyComponent::class.java).get(enemy.ID).formationSpawnComplete = false
                            world.getMapper(EnemyComponent::class.java).get(enemy.ID).inFormation = true

                            spawnedEnemyIds.add(enemy.ID)
                        }
                        // Right edge:
                        for (j in 0 until halfCount) {
                            val enemy = EnemyLine()
                            enemy.Create(world)
                            enemy.transform.position.set(33.5f, spacingY * (j + 1))
                            enemy.velocity.velocity.set(-0.5f, 0f)
                            val lineComponent = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                            lineComponent.spawnEdge = 1
                            world.getMapper(EnemyComponent::class.java).get(enemy.ID).formationSpawnComplete = false
                            world.getMapper(EnemyComponent::class.java).get(enemy.ID).inFormation = true

                            spawnedEnemyIds.add(enemy.ID)
                        }
                        // After spawning the group, mark them all as ready to start immune countdown.
                        for (id in spawnedEnemyIds) {
                            world.getMapper(EnemyComponent::class.java).get(id).formationSpawnComplete = true
                            world.getMapper(EnemyComponent::class.java).get(id).inFormation = true

                        }
                    }
                    EnemyFormation.ALL_EDGES -> {
                        // We'll assume a fixed number per edge; adjust as needed.
                        val widthCount = 8
                        val heightCount = 4
                        val spawnedEnemyIds = mutableListOf<Int>()

                        // Calculate spacing based on world dimensions:
                        val spacingX = screenWidth / (widthCount + 1)
                        val spacingY = screenHeight / (heightCount + 1)

                        // Top edge:
                        for (j in 0 until widthCount) {
                            val enemy = EnemyLine()
                            enemy.Create(world)
                            enemy.transform.position.set(spacingX * (j + 1), 14.2f)
                            enemy.velocity.velocity.set(0f, -0.5f)
                            val lineComponent = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                            lineComponent.spawnEdge = 2
                            world.getMapper(EnemyComponent::class.java).get(enemy.ID).formationSpawnComplete = false
                            world.getMapper(EnemyComponent::class.java).get(enemy.ID).inFormation = true

                            spawnedEnemyIds.add(enemy.ID)
                        }
                        // Bottom edge:
                        for (j in 0 until widthCount) {
                            val enemy = EnemyLine()
                            enemy.Create(world)
                            enemy.transform.position.set(spacingX * (j + 1), 0.5f)
                            enemy.velocity.velocity.set(0f, 0.5f)
                            val lineComponent = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                            lineComponent.spawnEdge = 3
                            world.getMapper(EnemyComponent::class.java).get(enemy.ID).formationSpawnComplete = false
                            world.getMapper(EnemyComponent::class.java).get(enemy.ID).inFormation = true

                            spawnedEnemyIds.add(enemy.ID)
                        }
                        // Left edge:
                        for (j in 0 until heightCount) {
                            val enemy = EnemyLine()
                            enemy.Create(world)
                            enemy.transform.position.set(0.5f, spacingY * (j + 1))
                            enemy.velocity.velocity.set(0.5f, 0f)
                            val lineComponent = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                            lineComponent.spawnEdge = 0
                            world.getMapper(EnemyComponent::class.java).get(enemy.ID).formationSpawnComplete = false
                            world.getMapper(EnemyComponent::class.java).get(enemy.ID).inFormation = true

                            spawnedEnemyIds.add(enemy.ID)
                        }
                        // Right edge:
                        for (j in 0 until heightCount) {
                            val enemy = EnemyLine()
                            enemy.Create(world)
                            enemy.transform.position.set(33.5f, spacingY * (j + 1))
                            enemy.velocity.velocity.set(-0.5f, 0f)
                            val lineComponent = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                            lineComponent.spawnEdge = 1
                            world.getMapper(EnemyComponent::class.java).get(enemy.ID).formationSpawnComplete = false
                            world.getMapper(EnemyComponent::class.java).get(enemy.ID).inFormation = true

                            spawnedEnemyIds.add(enemy.ID)
                        }
                        // Now mark all spawned enemies as having completed group spawn.
                        for (id in spawnedEnemyIds) {
                            world.getMapper(EnemyComponent::class.java).get(id).formationSpawnComplete = true
                            world.getMapper(EnemyComponent::class.java).get(id).inFormation = true

                        }
                    }
                }
            }
            // Now, increment the spawn timer.
            spawner.spawnTimer += world.delta
            // When the timer exceeds the interval and we have tasks, spawn one enemy.
            if (spawner.spawnTimer >= spawner.spawnInterval && spawner.spawnTasks.isNotEmpty()) {
                val task = spawner.spawnTasks.removeAt(0)
                if (task.prefabType == "basic") {
                    val enemy = EnemyBasic()
                    enemy.Create(world)
                    enemy.transform.position.set(task.position)
                    enemy.velocity.velocity.set(task.velocity)
                    val enemyMapper = world.getMapper(EnemyComponent::class.java)
                    val comp = enemyMapper.get(enemy.ID)
                    comp.speed = task.speed
                    comp.inFormation = false
                } else if (task.prefabType == "line") {
                    val enemy = EnemyLine()
                    enemy.Create(world)
                    enemy.transform.position.set(task.position)
                    enemy.velocity.velocity.set(task.velocity)
                    if (task.spawnEdge != null) {
                        val lineComp = world.getMapper(EnemyLineComponent::class.java).get(enemy.ID)
                        val edge = task.spawnEdge
                        if (edge != null) {
                            lineComp.spawnEdge = edge
                        }
                    }
                    val enemyMapper = world.getMapper(EnemyComponent::class.java)
                    val comp = enemyMapper.get(enemy.ID)
                    comp.speed = task.speed
                    comp.inFormation = true
                }
                spawner.spawnTimer = 0f
            }
            // If all tasks have been spawned, delete the spawner.
            if (spawner.spawnTasks.isEmpty()) {
                world.delete(entity)
            }
        }
    }
}
