package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.UIComponent


public class Text_Button(Text: String, Style: TextButton.TextButtonStyle, Position:Vector2, Scale:Vector2, Action:()->Unit ) : Prefab()
{
    var inText : String = Text
    var inStyle : TextButton.TextButtonStyle = Style
    var inPos : Vector2 = Position
    var inScale : Vector2 = Scale
    var inAction : ()->Unit = Action

    public override fun Create(world: World)
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
    }
}
