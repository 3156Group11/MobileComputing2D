package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.csd3156.group11.assetManager
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.UIComponent
import com.csd3156.group11.resources.Globals


public class Image_Button(filepath: String, Position: Vector2, Scale: Vector2, Action: (ImageButton) -> Unit) : Prefab() {
    var inFilepath: String = filepath
    var inPos: Vector2 = Position
    var inScale: Vector2 = Scale
    var inAction: (ImageButton) -> Unit = Action

    public override fun Create(world: World): Int {
        val texture = assetManager.system().get(inFilepath, Texture::class.java)
        val region = TextureRegion(texture, texture.width, texture.height)
        val drawable = TextureRegionDrawable(region)
        val button = ImageButton(drawable)
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
