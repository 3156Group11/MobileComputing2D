/**
 * @file Enemy.kt
 * @brief  This file contains the prefab for enemies
 */
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
import com.csd3156.group11.resources.Globals


/**
 * @class Enemy
 * @brief Base prefab for enemy entities.
 *
 * The Enemy class sets up common components for enemy entities such as TransformComponent,
 * VelocityComponent, ColliderComponent, SpriteComponent, and EnemyComponent. It also tags the
 * entity as an enemy.
 */
open class Enemy : Prefab()
{
    lateinit var transform: TransformComponent
    lateinit var velocity: VelocityComponent
    open override fun Create(world: World): Int
    {
        ID = world.create()
        //println("Enemy Created! Entity ID: $ID")
        transform = TransformComponent(
            position = Vector2(17.5f, 3f),
            scale = Vector2(0f, 0f)
        )
        world.edit(ID).add(transform)

        velocity = VelocityComponent(Vector2(0f, 0f))
        world.edit(ID).add(velocity)

        world.edit(ID).add(ColliderComponent(radius = 0.375f))
        world.edit(ID).add(SpriteComponent("textures/Enemy.png", RenderLayers.Enemy))
        world.edit(ID).add(EnemyComponent())
        world.edit(ID).add(TagComponent(Tag.ENEMY))
        return ID
    }
}

/**
 * @class EnemyBasic
 * @brief Prefab for basic enemy entities.
 *
 * EnemyBasic extends the base Enemy prefab and spawns at a random position within the game world.
 */
class EnemyBasic : Enemy() {
    override fun Create(world: World): Int {
        // Call the base enemy creation code
        super.Create(world)

        transform.position.set(MathUtils.random(0f, 35f), MathUtils.random(0f, 35 * Globals.scrHeight / Globals.scrWidth))

        world.edit(ID).add(EnemyBasicComponent())
        return ID
    }
}

/**
 * @class EnemyLine
 * @brief Prefab for enemy entities used in line formations.
 *
 * EnemyLine extends the base Enemy prefab and adds an EnemyLineComponent to store information
 * specific to line formations (e.g., spawn edge). The spawner system will later configure the position,
 * velocity, and spawn edge.
 */
class EnemyLine : Enemy() {
    override fun Create(world: World):Int {
        super.Create(world)

        world.edit(ID).add(EnemyLineComponent())
        return ID
    }
}
