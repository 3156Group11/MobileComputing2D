package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.ColliderComponent
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.SpriteComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.VelocityComponent

public class Enemy : Prefab()
{
    public override fun Create(world: World)
    {
        ID = world.create()
        world.edit(ID)
            .add(TransformComponent(
                position = Vector2(400f, 240f),
                scale = Vector2(0.5f, 0.5f)
            ))
            .add(VelocityComponent(Vector2(0f, 0f)))
            .add(ColliderComponent(radius = 8f))
            .add(SpriteComponent("textures/Enemy.png"))
            .add(EnemyComponent())
    }
}
