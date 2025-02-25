/**
 * @file SpriteComponent.kt
 * @brief  Holds the Sprite Component and its data members and methods
 */
package com.csd3156.group11.components

import com.artemis.Component
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.csd3156.group11.assetManager
import com.csd3156.group11.enums.RenderLayers

/**
* @brief Stores rendering properties for an entity’s sprite.
*
* This component holds a TextureRegion representing the visual appearance of an entity,
* as well as its width, height, and the render layer on which it should be drawn.
*/
class SpriteComponent() : Component() {
    var region: TextureRegion = TextureRegion()  // Empty region as default
    var width: Float = 15f
    var height: Float = 15f
    var layer: RenderLayers = RenderLayers.Enemy

    // Custom constructor to initialize with a texture
    constructor(filepath : String, inLayer : RenderLayers = RenderLayers.Enemy) : this() {
        val texture = assetManager.system().get(filepath, Texture::class.java)
        region = TextureRegion(texture)
        layer = inLayer
    }
}
