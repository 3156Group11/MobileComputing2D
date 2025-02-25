/**
 * @file RenderLayers.kt
 * @brief  This file contains enums specific to render layers
 */
package com.csd3156.group11.enums

/**
 * @enum RenderLayers
 * @brief Defines the drawing order (layers) for rendered entities.
 *
 * This enum is used to determine which entities are drawn over others:
 * - Background: For background images.
 * - BackgroundBorder: For border or frame graphics.
 * - FX: For special effects like particles.
 * - Enemy: For enemy sprites.
 * - Powerup: For power-up items.
 * - Player: For the player sprite.
 */
enum class RenderLayers
{
    Background,
    BackgroundBorder,
    FX,
    Enemy,
    Powerup,
    Player
}
