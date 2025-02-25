/**
 * @file Tag.kt
 * @brief  This file contains enums specific to tags
 */
package com.csd3156.group11.enums

/**
 * @enum Tag
 * @brief Enumerates tags for categorizing entities.
 *
 * Tags are used to quickly identify and filter entities in the game.
 * The available tags include:
 * - PLAYER: Entities representing the player.
 * - ENEMY: Entities representing enemies.
 * - POWERUP: Entities representing power-ups.
 * - LASER_BEAM: Entities representing laser beams.
 * - START_TIME: Used to mark the start time of the game.
 * - SCORE_UI: Used for score-related UI elements.
 * - NONE: Default tag when no other tag is applicable.
 */
enum class Tag {
    PLAYER,
    ENEMY,
    POWERUP,
    LASER_BEAM,
    START_TIME,
    SCORE_UI,
    NONE
}
