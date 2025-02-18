import com.artemis.BaseSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.Viewport
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.prefabs.Player
import com.csd3156.group11.prefabs.ShieldPowerUp

class UISystem(
    private val batch: SpriteBatch,
    private val viewport: Viewport
) : BaseSystem() {

    public val stage = Stage(viewport, batch)

    init {
        // Initialize UI elements (buttons, labels, etc.)
        val label = Label("Score: 0", Label.LabelStyle(BitmapFont(), Color.WHITE))
        label.setPosition(10f, viewport.worldHeight - 30f)
        stage.addActor(label)

        val button = TextButton("Spawn Enemy", TextButton.TextButtonStyle().apply {
            font = BitmapFont()
        })
        button.setPosition(10f, viewport.worldHeight - 70f)
        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                // Communicate with the ECS world
                val enemy = Player()
                enemy.Create(world)
            }
        })
        stage.addActor(button)

        //Button for testing shield below
        val shieldButton = TextButton("Spawn Shield", TextButton.TextButtonStyle().apply {
            font = BitmapFont()
        })
        shieldButton.setPosition(10f, viewport.worldHeight - 110f) // Adjusted position below enemy button
        shieldButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val powerUp = ShieldPowerUp()
                powerUp.Create(world) // Spawns Shield Power-Up in the ECS world
            }
        })
        stage.addActor(shieldButton)
        //Button for testing shield abv


        // Add more UI elements here
    }

    override fun processSystem() {
        // Update UI stage
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
