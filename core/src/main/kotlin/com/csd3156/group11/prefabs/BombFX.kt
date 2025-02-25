/**
 * @file Bomb.kt
 * @brief  This file contains the prefab for bombs
 */
package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.createCircleTexture
import com.csd3156.group11.enums.PowerUpType
import com.csd3156.group11.enums.RenderLayers


/**
 * @class BombFX
 * @brief Prefab for bomb explosion visual effects.
 *
 * The BombFX class creates an explosion effect using a circular texture generated at runtime.
 * The effect is centered around the specified detonation position and scaled according to the
 * provided radius. The effect lasts for a fixed duration.
 *
 * @param detonationPos The screen-space position where the bomb detonates.
 * @param radius The collision radius, used to calculate the visual scale.
 */
class BombFX(private val detonationPos: Vector2, private val radius: Float) : Prefab() {
    override fun Create(world: World): Int {
        ID = world.create()

        // Directly use the provided screen-space detonation position
        val adjustedPosition = detonationPos.cpy().sub(radius, radius) // Properly center the FX
        val texture = createCircleTexture(256, Color.RED)
        // Set the visual scale based on the actual radius (scale is diameter)
        val visualScale = Vector2(radius , radius )
        world.edit(ID)
            .add(TransformComponent(
                position = adjustedPosition,  // Proper centering
                scale = visualScale  // Visual size matches the collision radius
            ))
            .add(SpriteComponent("textures/Circle.png", RenderLayers.FX))
            .add(FXComponent().apply {
                fxType = PowerUpType.BOMB
                duration = 3f  // Effect duration
            })
        world.getMapper(SpriteComponent::class.java).get(ID).region = texture

        // Debug logs for verification
        println("💥 BombFX created at $adjustedPosition with visual scale $visualScale and radius $radius")
        println("💥 BombFX Debug Info:")
        println("Original Detonation Position (Screen): $detonationPos")
        println("Adjusted Centered Position: $adjustedPosition")
        return ID
    }
}
