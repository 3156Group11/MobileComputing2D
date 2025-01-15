package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.systems.IteratingSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.csd3156.group11.components.SpriteComponent
import com.csd3156.group11.components.TransformComponent

@Wire
class SpriteRenderSystem(
    private val spriteBatch: SpriteBatch,
    private val camera: OrthographicCamera
) : IteratingSystem(Aspect.all(SpriteComponent::class.java, TransformComponent::class.java)) {

    override fun begin() {
        spriteBatch.projectionMatrix = camera.combined
        spriteBatch.begin()
    }

    override fun process(entityId: Int) {
        val transform = world.getMapper(TransformComponent::class.java).get(entityId)
        val sprite = world.getMapper(SpriteComponent::class.java).get(entityId)

        spriteBatch.draw(
            sprite.region,
            transform.position.x,
            transform.position.y,
            sprite.width / 2,
            sprite.height / 2,
            sprite.width,
            sprite.height,
            1f,
            1f,
            transform.rotation
        )
    }

    override fun end() {
        spriteBatch.end()
    }
}
