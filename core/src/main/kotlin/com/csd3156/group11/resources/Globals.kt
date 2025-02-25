/**
 * @file Globals.kt
 * @brief Provides global variables and helper functions for the game.
 *
 * This file contains the Globals object, which holds game-wide state such as the current game state,
 * screen dimensions, timing flags, calibration values, and helper methods for coordinate conversion
 * and input calibration.
 */
package com.csd3156.group11.resources
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.enums.GameState

/**
 * @brief Global variables and utility functions.
 *
 * The Globals object is used to store and manage variables that are accessible from anywhere
 * in the game, including current game state, screen dimensions, timing values, enemy statistics,
 * and accelerometer calibration data. It also provides helper methods to convert coordinates
 * between screen and world space, and to calibrate accelerometer input.
 */
object Globals {
    public var currentState : GameState = GameState.MAIN_MENU
    public var scrWidth : Float = 0f // Initialised at runtime
    public var scrHeight : Float = 0f // Initialised at runtime
    public var UnitSize : Float = 0f // Initialised at runtime
    public var IsStarting = false
    public var StartingTimer = 0f
    public var deathScreen: Boolean = false
    public var deathScreenInit: Boolean = false
    public var isPausing: Boolean = false
    public var timeElapsed: Float = 0f
    public var enemiesKilled: Int = 0
    public var highScoreSaved: Boolean = false
    public var calibratedAccelX = 0f
    public var calibratedAccelY = 0f
    public var isCalibrated = false

    /**
     * @brief Converts a vector from screen coordinates to world coordinates.
     * @param inVec The input vector in screen coordinates.
     * @return A new Vector2 representing the coordinates in world space.
     *
     * The conversion scales the screen vector based on a fixed world width of 35 units.
     */
    public fun ScreenToWorld(inVec : Vector2): Vector2 {
        return Vector2(inVec.x/scrWidth * 35,inVec.y/scrWidth * 35)
    }

    /**
     * @brief Converts a vector from world coordinates to screen coordinates.
     * @param inVec The input vector in world coordinates.
     * @return A new Vector2 representing the coordinates in screen space.
     *
     * The conversion uses a fixed world width of 35 units.
     */
    public fun WorldToScreen(inVec : Vector2) : Vector2
    {
        return Vector2(inVec.x/35 * scrWidth,inVec.y/35 * scrWidth)
    }

    /**
     * @brief Calibrates the accelerometer by storing the current input values.
     *
     * This method reads the current accelerometer values from the device and stores them in
     * the calibratedAccelX and calibratedAccelY variables.
     */
    public fun calibrate() {
        calibratedAccelX = Gdx.input.accelerometerX
        calibratedAccelY = Gdx.input.accelerometerY
    }

    /**
     * @brief Resets the accelerometer calibration to zero.
     *
     * This method sets the calibrated accelerometer values for both X and Y axes to zero.
     */
    public fun topdown(){
        calibratedAccelX = 0f
        calibratedAccelY = 0f
    }
}
