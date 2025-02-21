package com.csd3156.group11.components

import com.artemis.Component
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.csd3156.group11.assetManager

class UIComponent() : Component() {
    var actor: Actor? = null // Holds the Scene2D UI element

    constructor(inActor : Actor) : this() {
        actor = inActor
    }
}


