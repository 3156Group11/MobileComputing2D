/**
 * @file Player.kt
 * @brief  This file contains the prefab for the player
 */
package com.csd3156.group11.prefabs

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.ColliderComponent
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.PlayerInputComponent
import com.csd3156.group11.components.PowerUpComponent
import com.csd3156.group11.components.TagComponent
import com.csd3156.group11.components.SpriteComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.VelocityComponent
import com.csd3156.group11.enums.RenderLayers
import com.csd3156.group11.enums.Tag

/**
 * @class Player
 * @brief Prefab for the player entity.
 *
 * The Player prefab initializes the player with components such as TransformComponent,
 * VelocityComponent, ColliderComponent, SpriteComponent, PlayerInputComponent, TagComponent, and
 * PowerUpComponent. The default spawn position is set at (17.5, 3) in world coordinates.
 */
public class Player : Prefab()
{
    public override fun Create(world: World):Int
    {
        // Default values
        ID = world.create()
        println("Player Created! Entity ID: $ID")
        world.edit(ID)
            .add(TransformComponent(
                position = Vector2(17.5f, 3f),
                scale = Vector2(1f, 1f)
            ))
            .add(VelocityComponent(Vector2(0f, 0f)))
            .add(ColliderComponent(radius = 0.5f)) // Adjust collider size accordingly
            .add(SpriteComponent("textures/Player.png", RenderLayers.Player))
            .add(PlayerInputComponent())
            .add(TagComponent(Tag.PLAYER))
            .add(PowerUpComponent())
        return ID
    }
}
