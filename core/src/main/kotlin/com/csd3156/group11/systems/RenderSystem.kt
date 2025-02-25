/**
 * @file RenderSystem.kt
 * @brief Handles rendering of all entities with sprite and transform components.
 */
package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.csd3156.group11.components.SpriteComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.resources.Globals

/**
 * @class RenderSystem
 * @brief Responsible for rendering entities in the game world.
 *
 * This system processes all entities that have `SpriteComponent` and `TransformComponent`.
 * It sorts entities by their rendering layer and then renders them using a `SpriteBatch`.
 * The rendering is scaled based on `Globals.UnitSize` to ensure consistency.
 */
@Wire
class RenderSystem(private val spriteBatch: SpriteBatch, private val camera: OrthographicCamera) : BaseEntitySystem(
    Aspect.all(SpriteComponent::class.java, TransformComponent::class.java)) {

    private lateinit var spriteMapper: ComponentMapper<SpriteComponent>
    private lateinit var transformMapper: ComponentMapper<TransformComponent>

    /**
     * @brief Prepares the rendering process.
     *
     * Adjusts sprite sizes to scale with the camera and sets up the `SpriteBatch`
     * with the appropriate projection matrix before drawing begins.
     */
    override fun begin() {
        // Optionally adjust sprite sizes if you want them to scale with camera
        val entities = subscription.entities
        for (i in 0 until entities.size()) {
            val spriteComp = spriteMapper[entities[i]]
            // example: scale sprite's displayed size
            spriteComp.width = Globals.UnitSize
            spriteComp.height = Globals.UnitSize
        }

        // Setup spriteBatch for drawing
        spriteBatch.projectionMatrix = camera.combined
        spriteBatch.begin()
    }

    /**
     * @brief Renders all active entities with sprites.
     *
     * This function:
     * - Sorts entities by layer to ensure correct draw order.
     * - Draws each sprite at its transformed position, applying scaling and rotation.
     */
    override fun processSystem() {
        val sortedEntities = mutableListOf<Int>()
        val entities = entityIds

        for (i in 0 until entities.size()) {
            sortedEntities.add(entities[i])
        }

        sortedEntities.sortBy { spriteMapper[it].layer } // Sort by layer (low renders first)
        for (entityId in sortedEntities) {
            val sprite = spriteMapper[entityId]
            val transform = transformMapper[entityId]

            //println("Rendering Entity ID: $entityId, Layer: ${sprite.layer}, Position: ${transform.position}")

            if (sprite != null) {
                spriteBatch.draw(
                    sprite.region,
                    transform.position.x * Globals.UnitSize, // X Position
                    transform.position.y * Globals.UnitSize, // Y Position

                    sprite.width * 0.5f, // Origin X (center of sprite)
                    sprite.height * 0.5f, // Origin Y (center of sprite)

                    sprite.width * transform.scale.x, // Width (scaled)
                    sprite.height * transform.scale.y, // Height (scaled)

                    transform.scale.x, // Scale X
                    transform.scale.y, // Scale Y

                    transform.rotation // Rotation in degrees
                )

            }
        }
    }

    /**
     * @brief Finalizes the rendering process.
     *
     * Ends the `SpriteBatch` rendering process after all entities have been drawn.
     */
    override fun end() {
        // Finish drawing sprites
        spriteBatch.end()


    }
}
