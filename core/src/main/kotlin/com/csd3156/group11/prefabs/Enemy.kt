package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.ColliderComponent
import com.csd3156.group11.components.EnemyBasicComponent
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.EnemyLineComponent
import com.csd3156.group11.components.SpriteComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.VelocityComponent

open class Enemy : Prefab()
{
    protected lateinit var transform: TransformComponent
    protected lateinit var velocity: VelocityComponent
    open override fun Create(world: World)
    {
        ID = world.create()
        transform = TransformComponent(
            position = Vector2(400f, 240f),
            scale = Vector2(0.5f, 0.5f)
        )
        world.edit(ID).add(transform)

        velocity = VelocityComponent(Vector2(0f, 0f))
        world.edit(ID).add(velocity)
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

class EnemyLine : Enemy() {
    override fun Create(world: World) {
        super.Create(world)

        val position = Vector2()
        val velocityVector = Vector2()

        val edge = MathUtils.random(0, 3)
        when (edge) {
            0 -> { // left edge: spawn at x = 0, random y; move right.
                position.set(0f, MathUtils.random(0f, 400f))
                velocityVector.set(30f, 0f)
            }
            1 -> { // right edge: spawn at x = 800, random y; move left.
                position.set(800f, MathUtils.random(0f, 400f))
                velocityVector.set(-30f, 0f)
            }
            2 -> { // top edge: spawn at y = 400, random x; move down.
                position.set(MathUtils.random(0f, 800f), 400f)
                velocityVector.set(0f, -30f)
            }
            3 -> { // bottom edge: spawn at y = 0, random x; move up.
                position.set(MathUtils.random(0f, 800f), 0f)
                velocityVector.set(0f, 30f)
            }
        }
        // Update the transform position and velocity using the stored references
        transform.position.set(position)
        velocity.velocity.set(velocityVector)

        world.edit(ID).add(EnemyLineComponent(edge))
    }
}
