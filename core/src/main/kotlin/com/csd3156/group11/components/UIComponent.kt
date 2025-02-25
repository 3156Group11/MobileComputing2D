/**
 * @file UIComponent.kt
 * @brief  Holds the UI Component and its data members and methods
 */
package com.csd3156.group11.components

import com.artemis.Component
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.csd3156.group11.assetManager

/**
 * @brief Holds a Scene2D UI element (Actor) for an entity.
 *
 * This component encapsulates a Scene2D Actor, allowing the ECS to manage UI elements
 * alongside game entities.
 */
class UIComponent() : Component() {
    var actor: Actor? = null // Holds the Scene2D UI element

    constructor(inActor : Actor) : this() {
        actor = inActor
    }
}


