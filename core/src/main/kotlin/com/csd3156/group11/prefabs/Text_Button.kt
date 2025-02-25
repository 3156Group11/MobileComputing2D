/**
 * @file Text_Button.kt
 * @brief  This file contains the prefab for text buttons
 */
package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.UIComponent


/**
 * @class Text_Button
 * @brief Prefab for creating a UI text button.
 *
 * The Image_Button prefab configures its size,
 * and attaches a click listener that executes a given action. The button is added to the ECS via a UIComponent.
 *
 * @param Text The text for the button.
 * @param Style The style of the button.
 * @param Position The screen position of the button.
 * @param Scale The scale factor to adjust the button's size.
 * @param Action A lambda function to execute when the button is clicked.
 */
public class Text_Button(Text: String, Style: TextButton.TextButtonStyle, Position:Vector2, Scale:Vector2, Action:()->Unit ) : Prefab()
{
    var inText : String = Text
    var inStyle : TextButton.TextButtonStyle = Style
    var inPos : Vector2 = Position
    var inScale : Vector2 = Scale
    var inAction : ()->Unit = Action

    public override fun Create(world: World):Int
    {
        inStyle.font.data.setScale(inScale.x, inScale.y)
        val button = TextButton(inText, inStyle)
        button.addListener(object : ClickListener()
        {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                // Communicate with the ECS world
                inAction()
            }
        }
        )

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
