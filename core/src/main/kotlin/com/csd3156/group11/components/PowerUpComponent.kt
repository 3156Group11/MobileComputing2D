/**
 * @file PowerUpComponent.kt
 * @brief  Holds the Power Up Component and its data members and methods
 */
package com.csd3156.group11.components

import com.artemis.Component
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.enums.PowerUpType

/**
 * @brief Component for managing power-up states and effects.
 *
 * The PowerUpComponent stores the type of power-up active on an entity and contains additional
 * data for specific power-up effects such as shield, bomb, lightning, slow fields, and lasers.
 * It supports multiple simultaneous bomb, lightning, slow field, and laser effects.
 */
class PowerUpComponent : Component() {
    var type: PowerUpType = PowerUpType.NONE

    // --------------------------------------
    // SHIELD data
    // --------------------------------------
    var hasShield: Boolean = false //shield active
    var shieldBreakEffect: Boolean = false //shield effect.
    var invulnerability: Float = 0f //invulnerability timer after shield breaks
    var shieldFXEntityId: Int = -1

    // ----------------------------------------
    // BOMB DATA - store multiple bombs at once
    // ----------------------------------------
    /**
     * @brief Data class representing a bomb power-up effect.
     *
     * @param center The pickup position of the bomb.
     * @param timeLeft Duration the bomb effect remains active.
     * @param radius The area-of-effect radius of the bomb.
     */
    data class BombEntry(
        val center: Vector2,  // The position where the bomb was picked up
        var timeLeft: Float,  // Duration the bomb effect stays active
        val radius: Float    // AoE radius
    )
    val bombs = mutableListOf<BombEntry>()

    // ----------------------------------------
    //  Lightning Data  - store multiple Lightning at once
    // ----------------------------------------
    /**
     * @brief Data class representing a lightning power-up effect.
     *
     * @param targetPosition The target position for the lightning strike.
     */
    data class LightningEntry(
        val targetPosition: Vector2,


    )

    val lightning = mutableListOf<LightningEntry>()

    // ----------------------------------------
    //  Slow Field Data  - store multiple slow fields at once
    // ----------------------------------------
    /**
     * @brief Data class representing a slow field effect.
     *
     * @param position The center of the slow field.
     * @param timeLeft Duration the slow field remains active.
     * @param direction The movement direction applied by the slow field.
     */
    data class SlowFieldEntry(
        val  position: Vector2,
        val timeLeft: Float,
        val direction: Vector2
    )

    val slowFields = mutableListOf<SlowFieldEntry>()

    // ----------------------------------------
    //  LASER DATA - store multiple lasers at once
    // ----------------------------------------
    /**
     * @brief Data class representing a laser power-up effect.
     *
     * @param startPosition The starting position of the laser (typically the player's position).
     * @param direction The direction of the laser beam.
     * @param timeLeft Duration the laser remains active.
     * @param length Length of the laser beam.
     * @param damage Damage dealt by the laser.
     */
    data class LaserEntry(
        val startPosition: Vector2,  // Starting position of the laser (player's position)
        val direction: Vector2,     // Direction of the laser beam
        var timeLeft: Float,         // Duration the laser remains active
        val length: Float,           // Length of the laser beam
        val damage: Float            // Damage dealt to enemies
    )
    val lasers = mutableListOf<LaserEntry>() // List to store active lasers
}
