package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.ColliderComponent
import com.csd3156.group11.components.EnemyBasicComponent
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.EnemyLineComponent
import com.csd3156.group11.components.SpriteComponent
import com.csd3156.group11.components.TagComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.VelocityComponent
import com.csd3156.group11.enums.RenderLayers
import com.csd3156.group11.enums.Tag

open class Enemy : Prefab()
{
    lateinit var transform: TransformComponent
    lateinit var velocity: VelocityComponent
    open override fun Create(world: World)
    {
        ID = world.create()
        //println("Enemy Created! Entity ID: $ID")
        transform = TransformComponent(
            position = Vector2(400f, 240f),
            scale = Vector2(1f, 1f)
        )
        world.edit(ID).add(transform)

        velocity = VelocityComponent(Vector2(0f, 0f))
        world.edit(ID).add(velocity)

        world.edit(ID).add(ColliderComponent(radius = 8f))
        world.edit(ID).add(SpriteComponent("textures/Enemy.png"))
        world.edit(ID).add(EnemyComponent())
        world.edit(ID).add(TagComponent(Tag.ENEMY))
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

class EnemyLine : Enemy() {
    override fun Create(world: World) {
        super.Create(world)

        world.edit(ID).add(EnemyLineComponent())
    }
}
