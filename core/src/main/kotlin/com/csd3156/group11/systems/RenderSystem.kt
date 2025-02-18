package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.csd3156.group11.components.SpriteComponent
import com.csd3156.group11.components.TransformComponent

@Wire
class RenderSystem(private val spriteBatch: SpriteBatch, private val camera: OrthographicCamera) : IteratingSystem(
    Aspect.all(SpriteComponent::class.java, TransformComponent::class.java)) {

    private lateinit var spriteMapper: ComponentMapper<SpriteComponent>
    private lateinit var transformMapper: ComponentMapper<TransformComponent>

    override fun begin() {
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
