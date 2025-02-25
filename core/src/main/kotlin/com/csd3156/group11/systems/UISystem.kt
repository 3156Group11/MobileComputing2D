/**
 * @file UISystem.kt
 * @brief Manages the rendering and updating of UI elements in the game.
 */
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

/**
 * @class UISystem
 * @brief Handles all UI-related updates and rendering.
 *
 * This system manages UI components, positioning them based on entity transformations,
 * updating UI elements (such as countdown timers), and rendering the UI using Scene2D.
 */
class UISystem(
    private val batch: SpriteBatch,
    private val viewport: Viewport
) : BaseEntitySystem(Aspect.all(UIComponent::class.java, TransformComponent::class.java)) {

    private lateinit var mUI: ComponentMapper<UIComponent>
    private lateinit var mTransform: ComponentMapper<TransformComponent>
    val stage = Stage(viewport, batch)

    /**
     * @brief Called when a new UI entity is added to the system.
     *
     * Adds the UI actor associated with the entity to the `Stage`, enabling it to be rendered.
     *
     * @param entityId The ID of the entity being added.
     */
    override fun inserted(entityId: Int) {
        val uiComponent = mUI[entityId]
        uiComponent.actor?.let {
            stage.addActor(it)
        }
    }

    /**
     * @brief Called when a UI entity is removed from the system.
     *
     * Removes the UI actor from the `Stage` to ensure it is no longer rendered.
     *
     * @param entityId The ID of the entity being removed.
     */
    override fun removed(entityId: Int) {
        val uiComponent = mUI[entityId]
        uiComponent.actor?.remove()
    }

    /**
     * @brief Updates and renders all UI elements in the game.
     *
     * - Updates the position of UI elements based on entity transformations.
     * - Manages the countdown timer for the start of the game.
     * - Processes Scene2D actions and draws the UI.
     */
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

    /**
     * @brief Adjusts the viewport size when the game window is resized.
     *
     * @param width The new width of the window.
     * @param height The new height of the window.
     */
    fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    /**
     * @brief Cleans up UI resources when the system is disposed.
     *
     * Releases memory by disposing of the `Stage`.
     */
    override fun dispose() {
        stage.dispose()
    }
}
