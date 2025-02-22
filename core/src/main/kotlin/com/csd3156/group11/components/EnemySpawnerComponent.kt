package com.csd3156.group11.components

import com.artemis.Component
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.assetManager
import com.csd3156.group11.enums.EnemyFormation

class EnemySpawnerComponent() : Component() {
    var enemyFormation: EnemyFormation = EnemyFormation.NONE
    var count: Int = 0
    var center: Vector2 = Vector2()
    var spawnInterval: Float = 0.1f  // seconds between spawns
    var spawnTimer: Float = 0f
    var spawnTasks: MutableList<SpawnTask> = mutableListOf()

    constructor(formation : EnemyFormation, enemyCount: Int, enemyCenter: Vector2) : this() {
        enemyFormation = formation
        count = enemyCount
        center = enemyCenter
    }
}

data class SpawnTask(
    val prefabType: String,  // "basic" or "line"
    val position: Vector2,
    val velocity: Vector2,
    val speed: Float,
    var spawnEdge: Int? = null  // Only needed for line enemies
)
