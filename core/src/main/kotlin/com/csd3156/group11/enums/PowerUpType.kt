/**
 * @file PowerUpType.kt
 * @brief  This file contains enums specific to power ups
 */
package com.csd3156.group11.enums

/**
 * @enum PowerUpType
 * @brief Enumerates the different types of power-ups available in the game.
 *
 * Available power-up types include:
 * - SHIELD: Provides temporary protection.
 * - LIGHTNING: May deal area or chain damage.
 * - BOMB: Explodes and damages nearby enemies.
 * - SLOW_FIELD: Temporarily slows enemy movement.
 * - LASER_3X: Fires three simultaneous laser beams.
 * - NONE: Indicates no power-up is present.
 */
enum class PowerUpType {
    SHIELD,
    LIGHTNING,
    BOMB,
    SLOW_FIELD,
    LASER_3X,
    NONE

}
