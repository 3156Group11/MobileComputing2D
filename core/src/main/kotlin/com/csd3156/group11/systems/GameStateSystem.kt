import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
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
import com.csd3156.group11.prefabs.LaserPowerUp
import com.csd3156.group11.prefabs.Image_Button
import com.csd3156.group11.prefabs.Image_Label
import com.csd3156.group11.prefabs.Text_Button
import com.csd3156.group11.prefabs.Text_Label
import com.csd3156.group11.resources.Globals
import com.csd3156.group11.soundSystem
import com.csd3156.group11.systems.PlayerInputSystem
import ktx.assets.file
import sun.font.TextLabel

class GameStateSystem(inViewport: Viewport) : BaseEntitySystem(Aspect.all(TransformComponent::class.java)) {
    var currentState: GameState
        get() = Globals.currentState
        set(value) { Globals.currentState = value }
    var viewport: Viewport = inViewport

    private var pendingState: GameState? = null
    private var initialized = false
    private var pauseScreenCreated = false
    private var isResuming = false
    private val pauseEntities = mutableListOf<Int>()
    private val deathScreenEntities = mutableListOf<Int>()
    private val highScores: MutableList<Int> = mutableListOf()
    private val prefs: Preferences = Gdx.app.getPreferences("HighScores")
    private var isDeathScreenCreated = false


    // Use Globals to store calibration state persistently
    var isCalibrated: Boolean
        get() = Globals.isCalibrated
        set(value) { Globals.isCalibrated = value }


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
            currentState = newState!!
            exitState()
            enterState(currentState)
        }

        if(Globals.currentState == GameState.GAME_STAGE){
            if(Globals.IsStarting && Globals.StartingTimer > 0){
                return
            }
            if(!Globals.deathScreen && !Globals.isPausing)
            {
                Globals.timeElapsed += world.delta
            }

            // 🔹 Update Score UI
            val uiMapper = world.getMapper(UIComponent::class.java)
            val tagMapper = world.getMapper(TagComponent::class.java)

            val entities = world.aspectSubscriptionManager.get(Aspect.all(UIComponent::class.java, TagComponent::class.java)).entities
            for (i in 0 until entities.size()) {
                val entityId = entities[i]
                val tagComponent = tagMapper[entityId]

                if (tagComponent.tag == Tag.SCORE_UI) { // Find score label
                    val uiComponent = uiMapper[entityId]
                    val scoreLabel = uiComponent.actor as Label
                    val currentScore = Globals.timeElapsed.toInt() + Globals.enemiesKilled

                    // 🔹 Prevent updating score if death screen is active
                    if (!Globals.deathScreen) {
                        scoreLabel.setText("Score: $currentScore")
                    }
                }
            }
        }

        // --------------------------
        // ⏸ Pause Game Logic
        // --------------------------
        if (Globals.currentState == GameState.GAME_STAGE && !Globals.isPausing && !isResuming) {
            if (Gdx.input.justTouched()) {
                if (!Globals.deathScreen) {
                    Globals.isPausing = true

                }
            }
        }
        if (Globals.isPausing && !pauseScreenCreated && !isResuming) {
            createPauseScreenEntities()
            pauseScreenCreated = true
        }
        // If game is paused, stop processing
        if (Globals.isPausing && !isResuming) {
            return
        }

        // --------------------------
        // 💀 Handle Game Over
        // --------------------------
        if (Globals.currentState == GameState.GAME_STAGE) {
            if (Globals.deathScreen && Globals.deathScreenInit && !isDeathScreenCreated) {
                isDeathScreenCreated = true // Set flag to prevent multiple creation
                Globals.deathScreenInit = false // Reset init flag

                soundSystem.stopBGM()
                soundSystem.playSFX("audio/sfx/fx_Player_Death.wav")

                val font = assetManager.system().get("fonts/LiberationSans.ttf", BitmapFont::class.java)

                // 🔹 Calculate Final Score
                val finalScore = Globals.timeElapsed.toInt() + Globals.enemiesKilled

                // 🔹 Stop updating the score
                Globals.timeElapsed = finalScore.toFloat()

                //Store the final score into high score list
                if (!Globals.highScoreSaved) {
                    onGameEnd(finalScore)
                    Globals.highScoreSaved = true  // Prevent duplicate saving
                }

                val uiMapper = world.getMapper(UIComponent::class.java)
                val tagMapper = world.getMapper(TagComponent::class.java)

                val entities = world.aspectSubscriptionManager.get(Aspect.all(UIComponent::class.java, TagComponent::class.java)).entities
                for (i in 0 until entities.size()) {
                    val entityId = entities[i]
                    val tagComponent = tagMapper[entityId]

                    if (tagComponent.tag == Tag.SCORE_UI) { // Ensure it's the correct UI element
                        val uiComponent = uiMapper[entityId]
                        val scoreLabel = uiComponent.actor as Label
                        scoreLabel.setText("Score: $finalScore")  // Update UI with the latest score
                    }
                }


                val gameOverLabel = Image_Label(
                    filepath = "textures/GameOver_Text.png",
                    Position = Vector2(12f, 9.5f),
                    Scale = Vector2(13f, 5f),
                    )
                gameOverLabel.Create(world)
                val gameOverEntity = gameOverLabel.Create(world)
                deathScreenEntities.add(gameOverEntity)

                // Main Menu Button
                val mainMenuButton = Image_Button(
                    filepath = "textures/MainMenu.png",
                    filepath2 = "textures/MainMenu_pressed.png",
                    Position = Vector2(15f, 5f),
                    Scale = Vector2(0.8f, 0.8f),
                    Action = {
                        soundSystem.playSFX("audio/sfx/fx_Button_DefaultSelection.wav")
                        removeDeathScreenEntities()
                        removePauseScreenEntities()
                        world.process()
                        changeState(GameState.MAIN_MENU)
                    }
                )
                val mainMenuEntity = mainMenuButton.Create(world)
                deathScreenEntities.add(mainMenuEntity)
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
                Globals.deathScreen = false
                Globals.deathScreenInit = false
                Globals.isPausing = false
                isResuming = false
                isDeathScreenCreated = false
                Globals.highScoreSaved = false

                // 🔹 Only reset score when a new game starts
                Globals.timeElapsed = 0f
                Globals.enemiesKilled = 0

                removeDeathScreenEntities()
                createGameEntities()
                soundSystem.resetDeathSFXFlag()
                soundSystem.playBGM("audio/bgm/bgm_level.wav")
            }
            GameState.HIGH_SCORE -> createHighScoreEntities()
        }
    }

    private fun exitState() {
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
            Position = Vector2(12f, 9.5f),
            Scale = Vector2(13f, 5f),

            )
        gameName.Create(world)

        val startGameButton = Image_Button(
            filepath = "textures/Start.png",
            filepath2 = "textures/Start_pressed.png",
            Position = Vector2(15f, 7f),
            Scale = Vector2(0.8f, 0.8f),
            Action = {
                soundSystem.playSFX("audio/sfx/fx_Button_DefaultSelection.wav")
                changeState(GameState.GAME_STAGE)
            }
        )
        startGameButton.Create(world)

        val highScore = Image_Button(
            filepath = "textures/HighScore.png",
            filepath2 = "textures/HighScore_pressed.png",
            Position = Vector2(15f, 4f),
            Scale = Vector2(0.8f, 0.8f),
            Action = {
                soundSystem.playSFX("audio/sfx/fx_Button_DefaultSelection.wav")
                changeState(GameState.HIGH_SCORE)
            }
        )
        highScore.Create(world)
        val initialFilepath = if (isCalibrated) "textures/Calibration.png" else "textures/TopDown.png"
        val calibrationButton = Image_Button(
            filepath = initialFilepath,  // Default image
            Position = Vector2(15f, 1f),
            Scale = Vector2(0.8f, 0.8f),
            Action = { button ->
                soundSystem.playSFX("audio/sfx/fx_Button_DefaultSelection.wav")
                isCalibrated = !isCalibrated
                swapImages(button, isCalibrated)

                if (isCalibrated) {
                    Globals.calibrate()
                } else {
                    Globals.topdown()
                }
            }
        )
        calibrationButton.Create(world)

        val entity = world.create()
        val emitter = world.edit(entity).create(EmitterComponent::class.java)
        emitter.position.set(4f, 4f) // Spawn location
        emitter.emissionRate = 5f
        emitter.particleLifeTime = 30f
        emitter.particleSpeed = 20f

        val background = world.create()
        world.edit(background)
            .add(SpriteComponent("textures/Background.png", RenderLayers.Background))
            .add(
                TransformComponent(
                    scale = Vector2(
                        7f,
                        9.5f * viewport.worldHeight / viewport.worldWidth
                    )
                )
            )
    }
    private fun swapImages(button: ImageButton, isCalibrated: Boolean) {
        val newFilepath = if (isCalibrated) "textures/Calibration.png" else "textures/TopDown.png"

        val texture = assetManager.system().get(newFilepath, Texture::class.java)
        val region = TextureRegion(texture, texture.width, texture.height)
        val drawable = TextureRegionDrawable(region)

        button.style.imageUp = drawable
        button.invalidate()
    }

    private fun createGameEntities() {
        // Example: Create player, enemies, and other game entities
        val player = Player()
        player.Create(world)

        // Inside createGameEntities() function:
        val typeStyle = assetManager.system().get("fonts/LiberationSans.ttf", BitmapFont::class.java)
        val scoreLabelStyle = LabelStyle(typeStyle, Color.WHITE)
        scoreLabelStyle.font.data.setScale(0.15f,.015f)
        val scoreLabel = Label("Score: 0", scoreLabelStyle)
        val scoreEntity = world.create()
        world.edit(scoreEntity)
            .add(TransformComponent(position = Vector2(1f, 13f),
                0f, scale = Vector2(0.15f,0.15f))) // Position at top-left
            .add(UIComponent(scoreLabel))
            .add(TagComponent(Tag.SCORE_UI)) // Use a tag to identify the score label

/*
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

        // -----------------------------
        // Laser Bomb Button
        // -----------------------------
        val spawnLaserButton = Text_Button(
            "Spawn Laser",
            TextButton.TextButtonStyle().apply { font = BitmapFont() },
            Position = Vector2(8f, 8f),
            Scale = Vector2(3f, 3f),
            Action = {
                val laserPowerUp = LaserPowerUp()
                laserPowerUp.Create(world)
            }
        )
        spawnLaserButton.Create(world)*/

        val background = world.create()
        world.edit(background)
            .add(SpriteComponent("textures/Background.png", RenderLayers.Background))
            .add(
                TransformComponent(
                    scale = Vector2(
                        7f,
                        9.5f * viewport.worldHeight / viewport.worldWidth
                    )
                )
            )

        Globals.IsStarting = true
        Globals.StartingTimer = 3f

        val font = assetManager.system().get("fonts/LiberationSans.ttf", BitmapFont::class.java)
        val inStyle = LabelStyle(font, Color.WHITE)
        inStyle.font.data.setScale(1.5f, 1.5f)
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
        loadScores() // Load saved scores before displaying

        val highScoreScreenOverLay = Image_Label(
            filepath = "textures/HighScore_Overlay.png",
            Position = Vector2(11f, 11f),
            Scale = Vector2(10.5f, 3f),
        )
        highScoreScreenOverLay.Create(world)

        val highScoreFont = assetManager.system().get("fonts/DroidSans.ttf", BitmapFont::class.java)
        for (i in highScores.indices) {
            val scoreLabel = Text_Label(
                "${i + 1}. ${highScores[i]}", LabelStyle(highScoreFont, Color.YELLOW),
                Position = Vector2(16f, 10f - i), //spacing of scores
                Scale = Vector2(0.75f, 0.75f)
            )
            scoreLabel.Create(world)
        }

        val backButton = Image_Button(
            filepath = "textures/Back.png",
            filepath2 = "textures/Back_pressed.png",
            Position = Vector2(11f, 1f),
            Scale = Vector2(0.6f, 0.8f),
            Action = {
                soundSystem.playSFX("audio/sfx/fx_Button_GoBack.wav")
                changeState(GameState.MAIN_MENU)
            }
        )
        backButton.Create(world)

        val clearButton = Image_Button(
            filepath = "textures/Clear.png",
            filepath2 = "textures/Clear_pressed.png",
            Position = Vector2(17f, 1f),
            Scale = Vector2(0.6f, 0.8f),
            Action = {
                soundSystem.playSFX("audio/sfx/fx_Button_Clear.wav")
                clearHighScores()
                removeExistingScore()
                createHighScoreEntities()
            }
        )
        clearButton.Create(world)

        val background = world.create()
        world.edit(background)
            .add(SpriteComponent("textures/Background.png", RenderLayers.Background))
            .add(
                TransformComponent(
                    scale = Vector2(
                        7f,
                        9.5f * viewport.worldHeight / viewport.worldWidth
                    )
                )
            )
    }

    private fun createPauseScreenEntities() {
        if (pauseScreenCreated) return
        soundSystem.playSFX("audio/sfx/fx_Button_PauseGame.wav")
        pauseEntities.clear()

        val over = world.create()
        world.edit(over).add(TransformComponent(position = Vector2(-1f,-1f), scale = Vector2(10f,5f)))
            .add(SpriteComponent("textures/Overlay.png", RenderLayers.BackgroundBorder))

        pauseEntities.add(over) // Store the entity ID

        val resumeButton = Image_Button(
            filepath = "textures/Resume.png",
            filepath2 = "textures/Resume_pressed.png",
            Position = Vector2(15f, 7.5f),
            Scale = Vector2(0.8f, 0.8f),
            Action = {
                soundSystem.playSFX("audio/sfx/fx_Button_UnpauseGame.wav")
                removePauseScreenEntities() // Properly remove all pause entities
                isResuming = true
                startResumeCountdown()
            }
        )
        pauseEntities.add(resumeButton.Create(world))

        val quitButton = Image_Button(
            filepath = "textures/Quit.png",
            filepath2 = "textures/Quit_pressed.png",
            Position = Vector2(15f, 4.5f),
            Scale = Vector2(0.8f, 0.8f),
            Action = {
                soundSystem.playSFX("audio/sfx/fx_Button_GoBack.wav")
                Globals.isPausing = false
                pauseScreenCreated = false
                changeState(GameState.MAIN_MENU)
            }
        )
        pauseEntities.add(quitButton.Create(world))
        pauseScreenCreated = true
    }

    private fun removePauseScreenEntities() {
        for (entity in pauseEntities) {
            if (entity != -1 && world.getEntity(entity) != null) {
                world.delete(entity)
            }
        }
        world.process() // Force immediate deletion

        pauseEntities.clear() // Clear stored entity IDs
        pauseScreenCreated = false // Allow UI to be created again next time
    }

    private fun startResumeCountdown() {
        var countdownTime = 3
        val font = assetManager.system().get("fonts/LiberationSans.ttf", BitmapFont::class.java)
        val style = LabelStyle(font, Color.WHITE)
        style.font.data.setScale(1.5f, 1.5f)
        val countdownLabel = Label(countdownTime.toString(), style)
        val countdownEntity = world.create()
        world.edit(countdownEntity)
            .add(
                TransformComponent(
                    position = Vector2(17.5f, 6f),
                    scale = Vector2(10f, 10f)
                )
            )
            .add(UIComponent(countdownLabel))
            .add(TagComponent(Tag.START_TIME))

        com.badlogic.gdx.utils.Timer.schedule(object : com.badlogic.gdx.utils.Timer.Task() {
            override fun run() {
                countdownTime--
                if (countdownTime > 0) {
                    countdownLabel.setText(countdownTime.toString())
                } else {
                    world.delete(countdownEntity)
                    world.process()
                    // Clear both flags so the game resumes normally
                    Globals.isPausing = false
                    isResuming = false
                    cancel()
                }
            }
        }, 1f, 1f)
    }

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

    private fun removeExistingScore() {
        val entities = subscription.entities
        for (i in 0 until entities.size()) {
            world.delete(entities[i]) // Delete all UI elements
        }
        world.process()
    }

    private fun removeDeathScreenEntities() {
        for (entity in deathScreenEntities) {
            if (entity != -1) {
                world.delete(entity)
            }
        }
        world.process() // Ensure immediate removal
        deathScreenEntities.clear() // Clear stored IDs

        // Reset game over flags to prevent re-creation
        Globals.deathScreen = false
        Globals.deathScreenInit = false
        isDeathScreenCreated = false
    }
}
