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
import com.csd3156.group11.components.PowerUpComponent
import com.csd3156.group11.components.SpriteComponent
import com.csd3156.group11.components.TransformComponent

@Wire
class RenderSystem(private val spriteBatch: SpriteBatch, private val camera: OrthographicCamera) : BaseEntitySystem(
    Aspect.all(SpriteComponent::class.java, TransformComponent::class.java)) {

    private lateinit var spriteMapper: ComponentMapper<SpriteComponent>
    private lateinit var transformMapper: ComponentMapper<TransformComponent>
    private lateinit var powerUpMapper: ComponentMapper<PowerUpComponent>

    //for shield
    private val shapeRenderer = ShapeRenderer()

    override fun begin() {
        val entities = subscription.entities
        for (i in 0 until entities.size()) {
            spriteMapper[entities[i]].width = camera.viewportWidth/35
            spriteMapper[entities[i]].height = camera.viewportWidth/35
        }

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

            if (sprite != null) {
                spriteBatch.draw(
                    sprite.region,
                    transform.position.x, transform.position.y, // Position
                    sprite.width * transform.scale.x, // Apply scaling to width
                    sprite.height * transform.scale.y // Apply scaling to height
                )
            }
        }
    }

    override fun end() {
        spriteBatch.end()

        //shape for shield
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)// line circle
        shapeRenderer.color = Color(0f, 1f, 1f, 1f) // Light blue

        val shieldEntities = world.aspectSubscriptionManager
            .get(Aspect.all(TransformComponent::class.java, PowerUpComponent::class.java))
            .entities

        for (i in 0 until shieldEntities.size()) {
            val eId = shieldEntities[i]
            val powerUpComp = powerUpMapper[eId]
            if (powerUpComp != null && powerUpComp.hasShield) {
                val transform = transformMapper[eId]

                // draw shield around player entity
                val shieldRadius = 25f
                shapeRenderer.circle(
                    transform.position.x + 16f,  // offset if your sprite’s origin is top-left
                    transform.position.y + 16f,  // or pick offsets that center the circle properly
                    shieldRadius
                )
            }
        }
        shapeRenderer.end()
    }
}
