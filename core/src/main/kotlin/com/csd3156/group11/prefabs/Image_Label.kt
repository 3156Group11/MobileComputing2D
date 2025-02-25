/**
 * @file Image_Label.kt
 * @brief  This file contains the prefab for labels
 */
package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.csd3156.group11.assetManager
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.UIComponent
import com.csd3156.group11.resources.Globals

/**
 * @class Image_Label
 * @brief Prefab for a UI label displayed as an image.
 *
 * The Image_Label prefab loads a texture from a file, creates an Image actor,
 * and configures its size based on Globals.UnitSize. The actor is then attached
 * to the ECS using a UIComponent.
 *
 * @param filepath The file path to the label texture.
 * @param Position The position where the label will be placed.
 * @param Scale The scale to be applied to the label.
 */
public class Image_Label(filepath: String, Position:Vector2, Scale:Vector2) : Prefab()
{
    var inFilepath : String = filepath
    var inPos : Vector2 = Position
    var inScale : Vector2 = Scale

    public override fun Create(world: World): Int
    {
        val texture = assetManager.system().get(inFilepath, Texture::class.java)
        val region = TextureRegion(texture, texture.width, texture.height)
        val button = Image(region)
        button.setSize(Globals.UnitSize * inScale.x, Globals.UnitSize * inScale.y)
        button.touchable = Touchable.disabled
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
