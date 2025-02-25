/**
 * @file GameState.kt
 * @brief  This file contains enums specific to game states
 */
package com.csd3156.group11.enums

/**
 * @enum GameState
 * @brief Represents the various states of the game.
 *
 * The game can be in one of the following states:
 * - MAIN_MENU: The main menu is active.
 * - GAME_STAGE: The game is currently being played.
 * - HIGH_SCORE: The high score screen is displayed.
 */
enum class GameState {
    MAIN_MENU, GAME_STAGE, HIGH_SCORE
}
