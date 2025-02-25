/**
 * @file EnemyFormation.kt
 * @brief  This file contains enums specific to enemy formation
 */
package com.csd3156.group11.enums

/**
 * @enum EnemyFormation
 * @brief Specifies the formation type used for enemy spawning.
 *
 * This enum defines the various patterns in which enemies can be spawned:
 * - NONE: Enemies are spawned randomly.
 * - GRID: Enemies are arranged in a grid pattern.
 * - CIRCLE: Enemies are arranged in a circular formation.
 * - TOP_BOTTOM: Enemies spawn along the top and bottom edges.
 * - LEFT_RIGHT: Enemies spawn along the left and right edges.
 * - ALL_EDGES: Enemies spawn along all four edges.
 */
enum class EnemyFormation {
    NONE,
    GRID,
    CIRCLE,
    TOP_BOTTOM,
    LEFT_RIGHT,
    ALL_EDGES
}
