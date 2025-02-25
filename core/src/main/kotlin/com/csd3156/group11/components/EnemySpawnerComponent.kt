/**
 * @file EnemySpawnerComponent.kt
 * @brief  Holds the Enemy Spawner Component and its data members and methods
 */
package com.csd3156.group11.components

import com.artemis.Component
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.assetManager
import com.csd3156.group11.enums.EnemyFormation

/**
 * @brief Component that holds parameters for enemy group spawning.
 *
 * The EnemySpawnerComponent is attached to a temporary entity that serves as a spawn request.
 * It defines the formation type, the number of enemies to spawn, a center point (for circle formations),
 * and manages the timing and queuing of individual spawn tasks.
 */
class EnemySpawnerComponent() : Component() {
    var enemyFormation: EnemyFormation = EnemyFormation.NONE
    var count: Int = 0
    var center: Vector2 = Vector2()
    var spawnInterval: Float = 3f  // seconds between spawns
    var spawnTimer: Float = 0f
    var spawnTasks: MutableList<SpawnTask> = mutableListOf()

    /**
     * @brief Secondary constructor that initializes the spawner component.
     * @param formation The desired enemy formation.
     * @param enemyCount The number of enemies to spawn.
     * @param enemyCenter The center position for the formation.
     */
    constructor(formation : EnemyFormation, enemyCount: Int, enemyCenter: Vector2) : this() {
        enemyFormation = formation
        count = enemyCount
        center = enemyCenter
    }
}

/**
 * @brief Data class representing a single enemy spawn task.
 *
 * A SpawnTask contains all the information needed to create one enemy,
 * including which prefab to use, its spawn position, velocity, movement speed,
 * and (optionally) the spawn edge for group formations.
 */
data class SpawnTask(
    val prefabType: String,  // "basic" or "line"
    val position: Vector2,
    val velocity: Vector2,
    val speed: Float,
    var spawnEdge: Int? = null  // Only needed for line enemies
)
