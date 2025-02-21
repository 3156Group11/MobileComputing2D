package com.csd3156.group11.components

import com.artemis.Component
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.csd3156.group11.assetManager

class SpriteComponent() : Component() {
    var region: TextureRegion = TextureRegion()  // Empty region as default
    var width: Float = 25f
    var height: Float = 25f

    // Custom constructor to initialize with a texture
    constructor(filepath : String) : this() {
        val texture = assetManager.system().get(filepath, Texture::class.java)
        region = TextureRegion(texture)
    }
}
