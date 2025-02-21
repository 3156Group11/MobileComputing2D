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

@Wire
class RenderSystem(private val spriteBatch: SpriteBatch, private val camera: OrthographicCamera) : BaseEntitySystem(
    Aspect.all(SpriteComponent::class.java, TransformComponent::class.java)) {

    private lateinit var spriteMapper: ComponentMapper<SpriteComponent>
    private lateinit var transformMapper: ComponentMapper<TransformComponent>

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
                    transform.position.x * Globals.UnitSize, transform.position.y * Globals.UnitSize, // Position
                    sprite.width * transform.scale.x, // Apply scaling to width
                    sprite.height * transform.scale.y // Apply scaling to height
                )
            }
        }
    }

    override fun end() {
        // Finish drawing sprites
        spriteBatch.end()


    }
}
