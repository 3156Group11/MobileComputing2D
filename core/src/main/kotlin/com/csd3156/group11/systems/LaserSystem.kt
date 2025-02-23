package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.prefabs.LaserFX

class LaserSystem : BaseEntitySystem(Aspect.all(PowerUpComponent::class.java, TransformComponent::class.java)) {
    private lateinit var powerUpMapper: ComponentMapper<PowerUpComponent>
    private lateinit var transformMapper: ComponentMapper<TransformComponent>
    private lateinit var enemyMapper: ComponentMapper<EnemyComponent>

    override fun processSystem() {
        val entitiesWithLasers = subscription.entities
        for (i in 0 until entitiesWithLasers.size()) {
            val entityId = entitiesWithLasers[i]
            val pwrComp = powerUpMapper[entityId]
            val playerTransform = transformMapper.get(entityId)

            val lasersToRemove = mutableListOf<PowerUpComponent.LaserEntry>()
            for (laser in pwrComp.lasers) {
                laser.timeLeft -= world.delta
                if (laser.timeLeft <= 0f) {
                    lasersToRemove.add(laser)
                    continue
                }

                // Set laser start position
                laser.startPosition.set(playerTransform.position)

                // Normalize direction and set laser end position
                val laserDirectionNormalized = laser.direction.cpy().nor()
                val laserSpeed = 200f // Adjust speed of the laser extending

                // Create or Update Laser FX
                val laserFX = LaserFX(laser.startPosition.cpy(), laserDirectionNormalized, laserSpeed)
                val laserId = laserFX.Create(world)

                // Keep updating laser over time
                laserFX.update(world, world.delta)

                // Check for enemy collisions
                val enemyEntities = world.aspectSubscriptionManager
                    .get(Aspect.all(EnemyComponent::class.java, TransformComponent::class.java))
                    .entities

                for (j in 0 until enemyEntities.size()) {
                    val enemyId = enemyEntities[j]
                    val enemyTransform = transformMapper.get(enemyId)
                    val enemyPos = enemyTransform.position

                    // Calculate laser extension
                    val laserEndPos = laser.startPosition.cpy().add(laserDirectionNormalized.scl(laserFX.currentLength))

                    // Check if enemy is within laser width
                    val laserWidth = 0.5f

                    val distanceToLine = distanceFromPointToLineSegment(enemyPos, laser.startPosition, laserEndPos)

                    if (distanceToLine < laserWidth && isPointWithinSegment(enemyPos, laser.startPosition, laserEndPos)) {
                        println("🔥 Enemy $enemyId hit by laser!")
                        world.delete(enemyId) // Destroy enemy
                    }
                }
            }
            pwrComp.lasers.removeAll(lasersToRemove)
        }
    }
}

fun distanceFromPointToLineSegment(point: Vector2, start: Vector2, end: Vector2): Float {
    val lineVec = end.cpy().sub(start)
    val pointVec = point.cpy().sub(start)

    val lineLengthSquared = lineVec.len2()
    if (lineLengthSquared == 0f) return pointVec.len() // If start == end, return distance to start

    // Project point onto line, clamping to segment
    val t = pointVec.dot(lineVec) / lineLengthSquared
    val clampedT = t.coerceIn(0f, 1f)

    val closestPoint = start.cpy().add(lineVec.scl(clampedT))
    return point.dst(closestPoint)
}

fun isPointWithinSegment(point: Vector2, start: Vector2, end: Vector2): Boolean {
    val segmentLengthSquared = start.dst2(end) // Squared distance for efficiency
    val pointToStartLengthSquared = start.dst2(point)
    val pointToEndLengthSquared = end.dst2(point)

    return pointToStartLengthSquared <= segmentLengthSquared && pointToEndLengthSquared <= segmentLengthSquared
}
