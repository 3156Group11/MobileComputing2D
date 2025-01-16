package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.ColliderComponent
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.PlayerInputComponent
import com.csd3156.group11.components.SpriteComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.VelocityComponent

public class Player : Prefab()
{
    public override fun Create(world: World)
    {
        // Default values
        ID = world.create()
        world.edit(ID)
            .add(TransformComponent(Vector2(400f, 240f)))
            .add(VelocityComponent(Vector2(0f,0f)))
            .add(SpriteComponent("textures/ic_launcher.png"))
            .add(PlayerInputComponent())
    }
}
