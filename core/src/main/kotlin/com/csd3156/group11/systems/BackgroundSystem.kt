package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.csd3156.group11.components.BackgroundComponent
import com.csd3156.group11.components.PowerUpComponent
import com.csd3156.group11.components.SpriteComponent
import com.csd3156.group11.components.TransformComponent

@Wire
class BackgroundSystem(private val spriteBatch: SpriteBatch, private val camera: OrthographicCamera) : IteratingSystem(
    Aspect.all(BackgroundComponent::class.java, TransformComponent::class.java)) {

    private lateinit var spriteMapper: ComponentMapper<BackgroundComponent>
    private lateinit var transformMapper: ComponentMapper<TransformComponent>


    override fun begin() {
        val entities = subscription.entities
        for (i in 0 until entities.size()) {
            spriteMapper[entities[i]].width = camera.viewportWidth/35
            spriteMapper[entities[i]].height = camera.viewportWidth/35
        }

        spriteBatch.projectionMatrix = camera.combined
        spriteBatch.begin()
    }

    override fun process(entityId: Int) {
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

    override fun end() {
        spriteBatch.end()
    }
}
