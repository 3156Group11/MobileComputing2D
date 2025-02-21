package com.csd3156.group11.resources
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.enums.GameState

object Globals {
    public var currentState : GameState = GameState.MAIN_MENU
    public var scrWidth : Float = 0f // Initialised at runtime
    public var scrHeight : Float = 0f // Initialised at runtime
    public var UnitSize : Float = 0f // Initialised at runtime
    public fun ScreenToWorld(inVec : Vector2): Vector2 {
        return Vector2(inVec.x/scrWidth * 35,inVec.y/scrWidth * 35)
    }

    public fun WorldToScreen(inVec : Vector2) : Vector2
    {
        return Vector2(inVec.x/35 * scrWidth,inVec.y/35 * scrWidth)
    }
}
