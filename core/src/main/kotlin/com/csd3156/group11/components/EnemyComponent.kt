/**
 * @file EnemyComponent.kt
 * @brief  Holds the different Enemy Components and their data members and methods
 */
package com.csd3156.group11.components

import com.artemis.Component

/**
 * @brief Component that defines enemy state and behavior.
 *
 * The EnemyComponent is attached to all enemy entities. It stores various
 * state flags and timers used to control enemy behavior, such as immunity,
 * dying state, speed, and whether the enemy is in a formation.
 */
open class EnemyComponent : Component(){
    public var inFormation: Boolean = false
    public var isImmune = true
    public var ImmuneTime = 2f
    public var isDying = false
    public var DyingTime = 2f
    public var isLive = false
    public var speed: Float = 2f
    var isSlowed: Boolean = false
    var slowTimeRemaining: Float = 0f

    var formationSpawnComplete: Boolean = true
}

/**
 * @brief Marker component for basic enemy types.
 *
 * The EnemyBasicComponent can be attached to enemies to indicate they
 * use the "basic" enemy behavior.
 */
class EnemyBasicComponent : Component() {

}

/**
 * @brief Component that holds formation-specific data for line enemies.
 *
 * The EnemyLineComponent is used by enemies spawned as part of a group formation.
 * It stores the edge from which the enemy was spawned.
 */
class EnemyLineComponent() : Component() {
    public var spawnEdge = 0
}
