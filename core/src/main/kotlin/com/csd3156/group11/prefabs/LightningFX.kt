/**
 * @file LightningFX.kt
 * @brief  This file contains the prefab for Lightning FX
 */
package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.*
import com.csd3156.group11.enums.PowerUpType
import com.csd3156.group11.enums.RenderLayers

/**
 * @class LightningFX
 * @brief Prefab for lightning visual effects.
 *
 * The LightningFX prefab creates a lightning effect at the given strike position. It uses a
 * custom texture ("textures/LightningFX.png") rendered on the FX layer. The effect lasts for
 * 1.5 seconds and is used to visually represent a lightning strike.
 *
 * @param strikePosition The position where the lightning effect should appear.
 */
class LightningFX(private val strikePosition: Vector2) : Prefab() {
    override fun Create(world: World):Int {
        ID = world.create()
        world.edit(ID)
            .add(TransformComponent(
                position = strikePosition.cpy(),
                scale = Vector2(1f, 1f)  // Adjust scale as needed for visual size
            ))
            .add(SpriteComponent("textures/LightningFX.png", RenderLayers.FX))
            .add(FXComponent().apply {
                fxType = PowerUpType.LIGHTNING
                duration = 1.5f  // Duration for how long the FX should last (e.g., 1.5 seconds)
            })

        println("LightningFX created with ID: $ID at position $strikePosition")
        return ID
    }
}
