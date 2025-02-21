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
import com.csd3156.group11.components.EnemySpawnerComponent
import com.csd3156.group11.enums.GameState
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.enums.EnemyFormation
import com.csd3156.group11.prefabs.EnemyBasic
import com.csd3156.group11.components.SpriteComponent
import com.csd3156.group11.enums.RenderLayers
import com.csd3156.group11.prefabs.Player
import com.csd3156.group11.prefabs.ShieldPowerUp
import com.csd3156.group11.prefabs.BombPowerUp
import com.csd3156.group11.prefabs.LightningPowerUp
import com.csd3156.group11.prefabs.SlowFieldPowerUp
import com.csd3156.group11.prefabs.Image_Button
import com.csd3156.group11.prefabs.Image_Label
import com.csd3156.group11.prefabs.Text_Button
import com.csd3156.group11.prefabs.Text_Label
import com.csd3156.group11.resources.Globals
import com.csd3156.group11.soundSystem
import ktx.assets.file

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
            GameState.MAIN_MENU -> {
                createMainMenuEntities()
                soundSystem.playBGM("audio/bgm/bgm_mainMenu.wav")
            }
            GameState.GAME_STAGE -> {
                createGameEntities()
                soundSystem.playBGM("audio/bgm/bgm_level.wav")
            }
            GameState.HIGH_SCORE -> createHighScoreEntities()
            GameState.PAUSE_SCREEN -> pauseScreen()
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
        val gameName = Image_Label(
            filepath = "textures/Title.png",
            Position = Vector2(12f, 8f),
            Scale = Vector2(13f, 7f),

        )
        gameName.Create(world)

        val startGameButton = Image_Button(
            filepath = "textures/Start.png",
            Position = Vector2(15f, 4f),
            Scale = Vector2(6f, 6f),
            Action = {
                changeState(GameState.GAME_STAGE)
            }
        )
        startGameButton.Create(world)

        val highScore = Image_Button(
            filepath = "textures/HighScore.png",
            Position = Vector2(15f, 1f),
            Scale = Vector2(6f,6f),
            Action = {
                changeState(GameState.HIGH_SCORE)
            }
        )
        highScore.Create(world)

        val entity = world.create()
        val emitter = world.edit(entity).create(EmitterComponent::class.java)
        emitter.position.set(4f, 4f) // Spawn location
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

        // -----------------------------
        //  Shield button
        // -----------------------------
        val but = Text_Button(
            "Create Shield", TextButton.TextButtonStyle().apply { font = BitmapFont() },
            Position = Vector2(4f,4f),
            Scale = Vector2(3f,3f),
            Action = {
                val enemy = ShieldPowerUp()
                enemy.Create(world)
            }
        )
        but.Create(world)

        val spawnEntityNone = world.create()
        world.edit(spawnEntityNone).add(EnemySpawnerComponent(EnemyFormation.ALL_EDGES, 20, Vector2(400f, 240f)))

        // -----------------------------
        // Bomb button
        // -----------------------------
        val spawnBombButton = Text_Button(
            "Spawn Bomb",
            TextButton.TextButtonStyle().apply { font = BitmapFont() },
            Position = Vector2(5f, 5f),
            Scale = Vector2(3f, 3f),
            Action = {
                // Spawns the bomb power-up
                val bombPowerUp = BombPowerUp()
                bombPowerUp.Create(world)
            }
        )
        spawnBombButton.Create(world)

        val pauseButton = Image_Button(
            filepath = "textures/Pause.png",
            Position = Vector2(32f, 0f),
            Scale = Vector2(3f,3f),
            Action = {
                clearHighScores()
                changeState(GameState.PAUSE_SCREEN)
            }
        )
        pauseButton.Create(world)

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

        val highScore = Image_Label(
            filepath = "textures/HighScore.png",
            Position = Vector2(15f, 12f),
            Scale = Vector2(8f, 3f),

            )
        highScore.Create(world)

        for (i in highScores.indices) {
            val scoreLabel = Text_Label(
                "${i + 1}. ${highScores[i]}", Label.LabelStyle(BitmapFont(), Color.WHITE),
                Position = Vector2(350f, 320f - (i * 30)), //spacing of scores
                Scale = Vector2(1f, 1f)
            )
            scoreLabel.Create(world)
        }

        val backButton = Image_Button(
            filepath = "textures/Back.png",
            Position = Vector2(14f, 0f),
            Scale = Vector2(4f,4f),
            Action = {
                changeState(GameState.MAIN_MENU)
            }
        )
        backButton.Create(world)

        /*
        val clearButton = Image_Button(
            filepath = "textures/Clear.png",
            Position = Vector2(20f, 0f),
            Scale = Vector2(4f,4f),
            Action = {
                clearHighScores()
                changeState(GameState.HIGH_SCORE)
            }
        )
        clearButton.Create(world)
        */

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

    private fun pauseScreen() {
        val pauseScreenOverLay = Image_Label(
            filepath = "textures/Overlay.png",
            Position = Vector2(0f, 0f),
            Scale = Vector2(35f, 15f),
            )
        pauseScreenOverLay.Create(world)

        val resumeButton = Image_Button(
            filepath = "textures/Resume.png",
            Position = Vector2(15f, 8f),
            Scale = Vector2(6f, 6f),
            Action = {
                changeState(GameState.GAME_STAGE)
            }
        )
        resumeButton.Create(world)

        val restartButton = Image_Button(
            filepath = "textures/Restart.png",
            Position = Vector2(15f, 4f),
            Scale = Vector2(6f, 6f),
            Action = {
                changeState(GameState.GAME_STAGE)
            }
        )
        restartButton.Create(world)

        val quitButton = Image_Button(
            filepath = "textures/Quit.png",
            Position = Vector2(15f, 0f),
            Scale = Vector2(6f, 6f),
            Action = {
                changeState(GameState.MAIN_MENU)
            }
        )
        quitButton.Create(world)
    }

    private val highScores: MutableList<Int> = mutableListOf()
    private val prefs: Preferences = Gdx.app.getPreferences("HighScores")

    private fun addScore(newScore: Int) {
        highScores.add(newScore)
        highScores.sortDescending()
        if (highScores.size > 5) {
            highScores.removeAt(5)
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
        for (i in 0 until 5) {
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
