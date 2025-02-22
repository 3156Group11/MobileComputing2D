package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.UIComponent


public class Text_Label(Text: String, Style: LabelStyle, Position:Vector2, Scale:Vector2) : Prefab()
{
    var inText : String = Text
    var inStyle : LabelStyle = Style
    var inPos : Vector2 = Position
    var inScale : Vector2 = Scale

    public override fun Create(world: World) {
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
    }
}
