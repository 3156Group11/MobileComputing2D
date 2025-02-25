/**
 * @file Text_Label.kt
 * @brief  This file contains the prefab for text labels
 */
package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.UIComponent

/**
 * @class Text_Label
 * @brief Prefab for a UI label displayed with text.
 *
 * The Image_Label prefab is not interactable (touchable disabled) and is attached
 * to the ECS via a UIComponent.
 *
 * @param Text The text of the label.
 * @param Style The style of the label.
 * @param Position The position for the label.
 * @param Scale The scaling factors for the label.
 */
public class Text_Label(Text: String, Style: LabelStyle, Position:Vector2, Scale:Vector2) : Prefab()
{
    var inText : String = Text
    var inStyle : LabelStyle = Style
    var inPos : Vector2 = Position
    var inScale : Vector2 = Scale

    public override fun Create(world: World):Int {
        inStyle.font.data.setScale(inScale.x, inScale.y)
        val label = Label(inText, inStyle)
        ID = world.create()
        //println("Enemy Created! Entity ID: $ID")
        world.edit(ID)
            .add(
                TransformComponent(
                position = inPos,
                scale = inScale
            )
            )
            .add(UIComponent(label))
        return ID
    }
}
