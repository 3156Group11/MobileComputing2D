import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.Viewport
import com.csd3156.group11.assetManager
import com.csd3156.group11.components.EnemySpawnerComponent
import com.csd3156.group11.enums.GameState
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.enums.EnemyFormation
import com.csd3156.group11.prefabs.EnemyBasic
import com.csd3156.group11.components.SpriteComponent
import com.csd3156.group11.components.TagComponent
import com.csd3156.group11.components.UIComponent
import com.csd3156.group11.enums.RenderLayers
import com.csd3156.group11.enums.Tag
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
import sun.font.TextLabel

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


        if (Globals.currentState == GameState.GAME_STAGE)
        {
            if (Globals.deathScreen && Globals.deathScreenInit)
            {
                // Generate death scren UI once
                val font = assetManager.system().get("fonts/LiberationSans.ttf", BitmapFont::class.java)
                val GameOverLabel = Text_Label(
                    "Game Over!",
                    LabelStyle(font, Color.WHITE),
                    Position = Vector2(17.5f,6f),
                    Scale = Vector2(1f,1f) )
                GameOverLabel.Create(world)
                Globals.deathScreenInit = false
            }
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
            Scale = Vector2(13f, 5f),

        )
        gameName.Create(world)

        val startGameButton = Image_Button(
            filepath = "textures/Start.png",
            Position = Vector2(15f, 5f),
            Scale = Vector2(0.8f, 0.8f),
            Action = {
                changeState(GameState.GAME_STAGE)
            }
        )
        startGameButton.Create(world)

        val highScore = Image_Button(
            filepath = "textures/HighScore.png",
            Position = Vector2(15f, 2f),
            Scale = Vector2(0.8f,0.8f),
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

       /* val spawnEntityNone = world.create()
        world.edit(spawnEntityNone).add(EnemySpawnerComponent(EnemyFormation.ALL_EDGES, 20, Vector2(400f, 240f)))*/

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

        // -----------------------------
        // Lightning button
        // -----------------------------
        val spawnLightningButton = Text_Button(
            "Spawn Lightning",
            TextButton.TextButtonStyle().apply { font = BitmapFont() },
            Position = Vector2(6f, 6f),
            Scale = Vector2(3f, 3f),
            Action = {
                // Spawns the bomb power-up
                val LightningPowerUp = LightningPowerUp()
                LightningPowerUp.Create(world)
            }
        )
        spawnLightningButton.Create(world)

        // -----------------------------
        // Slow button
        // -----------------------------
        val spawnSlowFieldButton = Text_Button(
            "Spawn SlowField",
            TextButton.TextButtonStyle().apply { font = BitmapFont() },
            Position = Vector2(7f, 7f),
            Scale = Vector2(3f, 3f),
            Action = {
                // Spawns the bomb power-up
                val SlowFieldPowerUp = SlowFieldPowerUp()
                SlowFieldPowerUp.Create(world)
            }
        )
        spawnSlowFieldButton.Create(world)

        val pauseButton = Image_Button(
            filepath = "textures/Pause.png",
            Position = Vector2(33f, 0.5f),
            Scale = Vector2(0.8f,0.8f),
            Action = {

            }
        )
        pauseButton.Create(world)

        //to save score into highScore, create variable to store score
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

        Globals.IsStarting = true
        Globals.StartingTimer = 3f

        val font = assetManager.system().get("fonts/LiberationSans.ttf", BitmapFont::class.java)
        val inStyle = LabelStyle(font, Color.WHITE, )
        inStyle.font.data.setScale(3f, 3f)
        val label = Label("3", inStyle)
        val startTimerID = world.create()
        //println("Enemy Created! Entity ID: $ID")
        world.edit(startTimerID)
            .add(
                TransformComponent(
                    position = Vector2(17.5f,6f),
                    scale = Vector2(10f,10f)
                )
            )
            .add(UIComponent(label))
            .add(TagComponent(Tag.START_TIME))

        Globals.deathScreen = false
        Globals.deathScreenInit = false
    }

    private fun createHighScoreEntities() {
        // Example: Create high score UI elements
        loadScores() // Load saved scores before displaying

        val highScoreScreenOverLay = Image_Label(
            filepath = "textures/HighScore_Overlay.png",
            Position = Vector2(0f, 0f),
            Scale = Vector2(35f, 15f),
        )
        highScoreScreenOverLay.Create(world)

        for (i in highScores.indices) {
            val scoreLabel = Text_Label(
                "${i + 1}. ${highScores[i]}", Label.LabelStyle(BitmapFont(), Color.YELLOW),
                Position = Vector2(16f, 10f-i), //spacing of scores
                Scale = Vector2(5f, 5f)
            )
            scoreLabel.Create(world)
        }

        val backButton = Image_Button(
            filepath = "textures/Back.png",
            Position = Vector2(13f, 1f),
            Scale = Vector2(0.6f,0.8f),
            Action = {
                val randomScore = com.badlogic.gdx.math.MathUtils.random(100, 10000)
                onGameEnd(randomScore)
                changeState(GameState.MAIN_MENU)
            }
        )
        backButton.Create(world)

        val clearButton = Image_Button(
            filepath = "textures/Clear.png",
            Position = Vector2(19f, 1f),
            Scale = Vector2(0.6f,0.8f),
            Action = {
                clearHighScores()
                removeExistingUI()
                createHighScoreEntities()
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
        if (highScores.size > 7) {
            highScores.removeAt(7)
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
        for (i in 0 until 7) {
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

    private fun removeExistingUI() {
        val entities = subscription.entities
        for (i in 0 until entities.size()) {
            world.delete(entities[i]) // Delete all UI elements
        }
        world.process()
    }
}
