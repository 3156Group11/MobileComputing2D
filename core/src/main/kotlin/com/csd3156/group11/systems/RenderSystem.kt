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
import com.csd3156.group11.components.PowerUpComponent
import com.csd3156.group11.components.SpriteComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.enums.PowerUpType

@Wire
class RenderSystem(private val spriteBatch: SpriteBatch, private val camera: OrthographicCamera) : BaseEntitySystem(
    Aspect.all(SpriteComponent::class.java, TransformComponent::class.java)) {

    private lateinit var spriteMapper: ComponentMapper<SpriteComponent>
    private lateinit var transformMapper: ComponentMapper<TransformComponent>
    private lateinit var powerUpMapper: ComponentMapper<PowerUpComponent>

    // For drawing shapes (shields, bomb radius, etc.)
    private val shapeRenderer = ShapeRenderer()

    override fun begin() {
        // Optionally adjust sprite sizes if you want them to scale with camera
        val entities = subscription.entities
        for (i in 0 until entities.size()) {
            val spriteComp = spriteMapper[entities[i]]
            // example: scale sprite's displayed size
            spriteComp.width = camera.viewportWidth / 35f
            spriteComp.height = camera.viewportWidth / 35f
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
        // Finish drawing sprites
        spriteBatch.end()

        // Now draw shape outlines for shield/bomb
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

        // 1) Draw shield circles: any entity that has hasShield = true
        shapeRenderer.color = Color.CYAN
        val shieldEntities = world.aspectSubscriptionManager
            .get(Aspect.all(TransformComponent::class.java, PowerUpComponent::class.java))
            .entities

        for (i in 0 until shieldEntities.size()) {
            val eId = shieldEntities[i]
            val pwrComp = powerUpMapper[eId]
            if (pwrComp != null && pwrComp.hasShield) {
                val transform = transformMapper[eId]

                // Arbitrary shield radius - can be a constant or a pwrComp field
                val shieldRadius = 25f
                // You might offset it if your sprite’s origin is top-left
                shapeRenderer.circle(
                    transform.position.x + 16f,
                    transform.position.y + 16f,
                    shieldRadius
                )
            }
        }

        // 2) Draw bomb radius: any entity that has bombActive = true
        //    For your game, likely the "player" entity is the one with bomb info,
        //    but technically any entity with a PowerUpComponent could hold it.
        shapeRenderer.color = Color.RED
        val bombEntities = world.aspectSubscriptionManager
            .get(Aspect.all(PowerUpComponent::class.java))
            .entities

        for (i in 0 until bombEntities.size()) {
            val eId = bombEntities[i]
            val pwrComp = powerUpMapper[eId]

            // Check if it's actually the bomb effect
            if (pwrComp != null && pwrComp.type == PowerUpType.BOMB && pwrComp.bombActive) {
                // The bomb is anchored at pwrComp.bombPos, not necessarily the entity's Transform
                val centerPos = pwrComp.bombPos
                val radius = pwrComp.bombRadius
                shapeRenderer.circle(centerPos.x, centerPos.y, radius)
            }
        }

        shapeRenderer.end()
    }
}
