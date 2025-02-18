package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.ColliderComponent
import com.csd3156.group11.components.EnemyBasicComponent
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.SpriteComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.VelocityComponent

open class Enemy : Prefab()
{
    protected lateinit var transform: TransformComponent
    open override fun Create(world: World)
    {
        ID = world.create()
        transform = TransformComponent(
            position = Vector2(400f, 240f),
            scale = Vector2(0.5f, 0.5f)
        )
        world.edit(ID).add(transform)

        world.edit(ID).add(VelocityComponent(Vector2(0f, 0f)))
        world.edit(ID).add(ColliderComponent(radius = 8f))
        world.edit(ID).add(SpriteComponent("textures/Enemy.png"))
        world.edit(ID).add(EnemyComponent())
    }
}

class EnemyBasic : Enemy() {
    override fun Create(world: World) {
        // Call the base enemy creation code
        super.Create(world)

        transform.position.set(MathUtils.random(0f, 800f), MathUtils.random(0f, 400f))

        world.edit(ID).add(EnemyBasicComponent())
    }
}
