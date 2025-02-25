/**
 * @file Image_Button.kt
 * @brief  This file contains the prefab for buttons
 */
package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.csd3156.group11.assetManager
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.UIComponent

/**
 * @class Image_Button
 * @brief Prefab for a UI image button.
 *
 * The Image_Button prefab creates a Scene2D ImageButton using a texture (or a pair of textures for
 * different button states). It sets the button's size based on the provided scale and attaches it to
 * the ECS through a UIComponent.
 *
 * @param filepath The file path to the button texture.
 * @param Position The screen position of the button.
 * @param Scale The scale factor for the button size.
 * @param Action A lambda to execute when the button is clicked.
 */
public class Image_Button(filepath: String, Position: Vector2, Scale: Vector2, Action: (ImageButton) -> Unit) : Prefab() {
    var inFilepath: String = filepath
    var inPos: Vector2 = Position
    var inScale: Vector2 = Scale
    var inAction: (ImageButton) -> Unit = Action
    var inFilepath2: String? = null

    constructor(
        filepath: String,
        filepath2: String,
        Position: Vector2,
        Scale: Vector2,
        Action: (ImageButton) -> Unit
    ) : this(filepath, Position, Scale, Action) {  // Calls the primary constructor
        inFilepath2 = filepath2
    }

    public override fun Create(world: World): Int {
        val texture = assetManager.system().get(inFilepath, Texture::class.java)
        val region = TextureRegion(texture, texture.width, texture.height)
        val drawable = TextureRegionDrawable(region)
        val button : ImageButton
        if (inFilepath2 != null)
        {
            val texture2 = assetManager.system().get(inFilepath2, Texture::class.java)
            val region2 = TextureRegion(texture2, texture2.width, texture2.height)
            val drawable2 = TextureRegionDrawable(region2)
            button = ImageButton(drawable, drawable2)
        }
        else
        {
            button = ImageButton(drawable)
        }

        button.setSize(texture.width * inScale.x, texture.height * inScale.x)

        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {

                inAction(button)
            }
        })

        ID = world.create()
        world.edit(ID)
            .add(
                TransformComponent(
                    position = inPos,
                    scale = inScale
                )
            )
            .add(UIComponent(button))
        return ID
    }
}
