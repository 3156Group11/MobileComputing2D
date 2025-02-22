import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import com.csd3156.group11.components.TagComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.UIComponent
import com.csd3156.group11.enums.Tag
import com.csd3156.group11.resources.Globals
import com.badlogic.gdx.scenes.scene2d.ui.Label


class UISystem(
    private val batch: SpriteBatch,
    private val viewport: Viewport
) : BaseEntitySystem(Aspect.all(UIComponent::class.java, TransformComponent::class.java)) {

    private lateinit var mUI: ComponentMapper<UIComponent>
    private lateinit var mTransform: ComponentMapper<TransformComponent>
    val stage = Stage(viewport, batch)

    override fun inserted(entityId: Int) {
        val uiComponent = mUI[entityId]
        uiComponent.actor?.let {
            stage.addActor(it)
        }
    }

    override fun removed(entityId: Int) {
        val uiComponent = mUI[entityId]
        uiComponent.actor?.remove()
    }

    override fun processSystem() {
        val entities = subscription.entities
        for (i in 0 until entities.size()) {
            val entityId = entities[i]
            val uiComponent = mUI[entityId]
            val transform = mTransform[entityId]

            if (Globals.IsStarting) {
                val tagA = world.getEntity(entityId).getComponent(TagComponent::class.java)?.tag
                if (tagA != null) {
                    if (tagA == Tag.START_TIME) {
                        Globals.StartingTimer -= world.delta
                        val timeActor = world.getEntity(entityId).getComponent((UIComponent::class.java)).actor
                        val intTime = Globals.StartingTimer.toInt() + 1
                        (timeActor as? Label)?.setText(intTime.toString())

                        if (Globals.StartingTimer < 0)
                        {
                            Globals.IsStarting = false
                            world.delete(entityId)
                        }
                    }
                }
            }


            val screenPosition = Globals.WorldToScreen(transform.position)
            uiComponent.actor?.setPosition(screenPosition.x , screenPosition.y)
        }

        stage.act(world.delta)
        stage.draw()
    }

    fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
    }
}
