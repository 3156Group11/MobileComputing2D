import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.Viewport
import com.csd3156.group11.components.SpriteComponent
import com.csd3156.group11.enums.GameState
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.enums.RenderLayers
import com.csd3156.group11.prefabs.Player
import com.csd3156.group11.prefabs.ShieldPowerUp
import com.csd3156.group11.prefabs.Text_Button
import com.csd3156.group11.prefabs.Text_Label
import com.csd3156.group11.resources.Globals

class GameStateSystem(inViewport: Viewport) : BaseEntitySystem(Aspect.all(TransformComponent::class.java)) {
    var currentState: GameState
        get() = Globals.currentState
        set(value) { Globals.currentState = value }
    var viewport: Viewport = inViewport

    private var pendingState: GameState? = null
    private var initialized = false
    fun changeState(newState: GameState) {
        if (newState != currentState) {
            println("Changing state to: $newState")
            pendingState = newState
        }
    }

    override fun processSystem() {
        if (!initialized) {
            enterState(currentState)
            initialized = true
        }

        if (pendingState != null) {
            val newState = pendingState
            pendingState = null

            world.process()

            exitState(currentState)
            currentState = newState!!
            enterState(currentState)
        }
    }


    private fun enterState(state: GameState) {
        when (state) {
            GameState.MAIN_MENU -> createMainMenuEntities()
            GameState.GAME_STAGE -> createGameEntities()
            GameState.HIGH_SCORE -> createHighScoreEntities()
        }
    }

    private fun exitState(state: GameState) {
        val entities = subscription.entities
        for (i in 0 until entities.size())  {
            world.delete(entities[i]) // Delete all active entities
        }
        world.process() // Ensure entities are removed immediately
    }

    private fun createMainMenuEntities() {
        // Example: Create UI entities for Main Menu
        val gameName = Text_Label(
            "2D Mobile Game", Label.LabelStyle(BitmapFont(), Color.YELLOW),
            Position = Vector2(340f,250f),
            Scale = Vector2(1f,1f),
        )
        gameName.Create(world)

        val startGameButton = Text_Button(
            "Start Game",
            TextButton.TextButtonStyle().apply { font = BitmapFont() },
            Position = Vector2(350f, 200f),
            Scale = Vector2(4f, 4f),
            Action = {
                println("Start Game button clicked!")
                changeState(GameState.GAME_STAGE)
            }
        )
        startGameButton.Create(world)

        val highScore = Text_Button(
            "High Score",
            TextButton.TextButtonStyle().apply { font = BitmapFont() },
            Position = Vector2(350f, 150f),
            Scale = Vector2(3f, 3f),
            Action = {
                changeState(GameState.HIGH_SCORE)
            }
        )
        highScore.Create(world)

        val entity = world.create()
        val emitter = world.edit(entity).create(EmitterComponent::class.java)
        emitter.position.set(400f, 300f) // Spawn location
        emitter.emissionRate = 5f
        emitter.particleLifeTime = 30f
        emitter.particleSpeed = 20f


        val background = world.create()
        world.edit(background)
            .add(SpriteComponent("textures/Background.png", RenderLayers.Background))
            .add(TransformComponent(scale = Vector2(35f,35f * viewport.worldHeight/viewport.worldWidth )))

        val backgroundBox = world.create()
        world.edit(backgroundBox)
            .add(SpriteComponent("textures/BackgroundBorder.png",RenderLayers.BackgroundBorder))
            .add(TransformComponent(scale = Vector2(35f * 0.9f,35f * viewport.worldHeight/viewport.worldWidth * 0.8f),
                position = Vector2(viewport.worldWidth * 0.05f, viewport.worldHeight * 0.1f)))
    }

    private fun createGameEntities() {
        // Example: Create player, enemies, and other game entities
        val player = Player()
        player.Create(world)

        val but = Text_Button(
            "Create Shield", TextButton.TextButtonStyle().apply { font = BitmapFont() },
            Position = Vector2(200f,300f),
            Scale = Vector2(1f,1f),
            Action = {
                val enemy = ShieldPowerUp()
                enemy.Create(world)
            }
        )

        but.Create(world)
        val backButton = Text_Button(
            "Back", TextButton.TextButtonStyle().apply { font = BitmapFont() },
            Position = Vector2(750f, 10f),
            Scale = Vector2(1f, 1f),
            Action = {
                changeState(GameState.MAIN_MENU) // Return to main menu
            }
        )
        backButton.Create(world)

        //to save score into highscore, create variable to store score
        //on game end, call onGameEnd(variable)
        val background = world.create()
        world.edit(background)
            .add(SpriteComponent("textures/Background.png",RenderLayers.Background))
            .add(TransformComponent(scale = Vector2(35f,35f * viewport.worldHeight/viewport.worldWidth )))

        val backgroundBox = world.create()
        world.edit(backgroundBox)
            .add(SpriteComponent("textures/BackgroundBorder.png",RenderLayers.BackgroundBorder))
            .add(TransformComponent(scale = Vector2(35f * 0.9f,35f * viewport.worldHeight/viewport.worldWidth * 0.8f),
                position = Vector2(viewport.worldWidth * 0.05f, viewport.worldHeight * 0.1f)))
    }

    private fun createHighScoreEntities() {
        // Example: Create high score UI elements
        loadScores() // Load saved scores before displaying

        val titleLabel = Text_Label(
            "High Scores", Label.LabelStyle(BitmapFont(), Color.YELLOW),
            Position = Vector2(330f, 350f),
            Scale = Vector2(1.2f, 1.2f)
        )
        titleLabel.Create(world)

        for (i in highScores.indices) {
            val scoreLabel = Text_Label(
                "${i + 1}. ${highScores[i]}", Label.LabelStyle(BitmapFont(), Color.WHITE),
                Position = Vector2(350f, 320f - (i * 30)), //spacing of scores
                Scale = Vector2(1f, 1f)
            )
            scoreLabel.Create(world)
        }

        val backButton = Text_Button(
            "Back", TextButton.TextButtonStyle().apply { font = BitmapFont() },
            Position = Vector2(325f, 50f),
            Scale = Vector2(1f, 1f),
            Action = {
                changeState(GameState.MAIN_MENU) // Return to main menu
            }
        )
        backButton.Create(world)

        val clearButton = Text_Button(
            "Clear",
            TextButton.TextButtonStyle().apply { font = BitmapFont() },
            Position = Vector2(375f, 50f), // Position below the back button
            Scale = Vector2(1f, 1f),
            Action = {
                clearHighScores()
                changeState(GameState.HIGH_SCORE) // Refresh high score screen
            }
        )
        clearButton.Create(world)

        val background = world.create()
        world.edit(background)
            .add(SpriteComponent("textures/Background.png",RenderLayers.Background))
            .add(TransformComponent(scale = Vector2(35f,35f * viewport.worldHeight/viewport.worldWidth )))

        val backgroundBox = world.create()
        world.edit(backgroundBox)
            .add(SpriteComponent("textures/BackgroundBorder.png",RenderLayers.BackgroundBorder))
            .add(TransformComponent(scale = Vector2(35f * 0.9f,35f * viewport.worldHeight/viewport.worldWidth * 0.8f),
                position = Vector2(viewport.worldWidth * 0.05f, viewport.worldHeight * 0.1f)))
    }

    private val highScores: MutableList<Int> = mutableListOf()
    private val prefs: Preferences = Gdx.app.getPreferences("HighScores")

    private fun addScore(newScore: Int) {
        highScores.add(newScore)
        highScores.sortDescending()
        if (highScores.size > 9) {
            highScores.removeAt(9)
        }
    }

    private fun saveScores() {
        for (i in highScores.indices) {
            prefs.putInteger("score_$i", highScores[i])
        }
        prefs.flush() // Save changes
    }

    private fun loadScores() {
        highScores.clear()
        for (i in 0 until 9) {
            val score = prefs.getInteger("score_$i", 0)
            if (score > 0) {
                highScores.add(score)
            }
        }
    }
    private fun onGameEnd(finalScore: Int) {
        addScore(finalScore)
        saveScores() // Save new high scores
    }

    private fun clearHighScores() {
        highScores.clear() // Clear the list in memory
        prefs.clear() // Clear stored scores in SharedPreferences
        prefs.flush() // Save changes to persist clearing
    }
}
