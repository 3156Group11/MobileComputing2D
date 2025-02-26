/**
 * @file MainSystem.kt
 * @brief The main game entry point.
 */
package com.csd3156.group11

import EmitterSystem
import EnemyManagerSystem
import GameStateSystem
import ParticleSystem
import UISystem
import com.artemis.WorldConfigurationBuilder
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.csd3156.group11.prefabs.EnemyBasic
import com.csd3156.group11.prefabs.EnemyLine
import com.csd3156.group11.prefabs.Player
import com.csd3156.group11.resources.Globals
import com.csd3156.group11.systems.AssetSystem
import com.csd3156.group11.systems.BombSystem
import com.csd3156.group11.systems.CollisionSystem
import com.csd3156.group11.systems.EnemyLineSystem
import com.csd3156.group11.systems.EnemySpawnerSystem
import com.csd3156.group11.systems.EnemySystem
import com.csd3156.group11.systems.FXSystem
import com.csd3156.group11.systems.LaserSystem
import com.csd3156.group11.systems.LightningSystem
import com.csd3156.group11.systems.PhysicsSystem
import com.csd3156.group11.systems.PlayerInputSystem
import com.csd3156.group11.systems.PowerUpMovementSystem
import com.csd3156.group11.systems.PowerUpSpawnerSystem
import com.csd3156.group11.systems.RenderSystem
import com.csd3156.group11.systems.ShieldSystem
import com.csd3156.group11.systems.SlowFieldSystem
import com.csd3156.group11.systems.SoundSystem

var assetManager:AssetSystem = AssetSystem() // Global asset manager for handling game resources.
var soundSystem: SoundSystem = SoundSystem() // Global sound system for managing background music (BGM) and sound effects (SFX)

/**
 * @brief Creates a circular texture with a specified diameter and color.
 * @param diameter The diameter of the circle in pixels.
 * @param color The color of the circle (default is white).
 * @return A TextureRegion containing the generated circular texture.
 */
fun createCircleTexture(diameter: Int, color: Color = Color.WHITE): TextureRegion {
    val pixmap = Pixmap(diameter, diameter, Pixmap.Format.RGBA8888)
    pixmap.setColor(color)
    pixmap.fillCircle(diameter / 2, diameter / 2, diameter / 2)

    val texture = Texture(pixmap)
    pixmap.dispose() // Free memory

    return TextureRegion(texture)
}

/**
 * @class Main
 * @brief Main application class responsible for initializing and managing the game loop.
 *
 * This class initializes game resources, sets up the Entity Component System (ECS),
 * handles rendering, and manages input processing.
 */
/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms. */
class Main(widthPix: Int, heightPix: Int) : ApplicationAdapter()
{
    private lateinit var world: com.artemis.World
    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: Viewport
    private lateinit var spriteBatch: SpriteBatch
    private lateinit var font: BitmapFont
    private var scrWidth : Float = widthPix.toFloat()
    private var scrHeight : Float = heightPix.toFloat()

    /**
     * @brief Initializes the game, including assets, ECS world, and UI.
     */
    override fun create() {
        Globals.scrWidth = scrWidth
        Globals.scrHeight = scrHeight
        Globals.UnitSize = scrWidth/35
        camera = OrthographicCamera()
        viewport = FitViewport(scrWidth, scrHeight, camera)
        viewport.apply()
        camera.position.set(viewport.worldWidth / 2, viewport.worldHeight / 2, 0f)
        assetManager.loadTexturesFromFolder("textures")
        assetManager.loadSFXFromFolder("audio/sfx")
        assetManager.loadBGMFromFolder("audio/bgm")
        assetManager.loadFontsFromFolder("fonts")


        // Initialize rendering tools
        spriteBatch = SpriteBatch()
        font = BitmapFont()

        val uiSystem = UISystem(spriteBatch, viewport)

        //True for WASD
        //False for Tilt
        val isDebugMode = false
        val particleTexture = createCircleTexture(360)

        // Configure ECS world
        val worldConfiguration = WorldConfigurationBuilder()
            .with(GameStateSystem(viewport))
            .with(PlayerInputSystem(isDebugMode))
            .with(EnemySystem())
            .with(EnemyLineSystem())
            .with(EnemySpawnerSystem())
            .with(EnemyManagerSystem(threshold = 100, interval = 2f))
            .with(PowerUpMovementSystem())
            .with(PhysicsSystem())
            .with(BombSystem())
            .with(ShieldSystem())
            .with(LightningSystem())
            .with(SlowFieldSystem())
            .with(CollisionSystem())
            .with(LaserSystem())
            .with(FXSystem())
            .with(PowerUpSpawnerSystem())
            .with(RenderSystem(spriteBatch, camera))
            .with(EmitterSystem(viewport.worldWidth, viewport.worldHeight))
            .with(ParticleSystem(spriteBatch, particleTexture))
            .with(uiSystem)
            .with(soundSystem)
            .build()


        world = com.artemis.World(worldConfiguration)

        Gdx.input.inputProcessor = uiSystem.stage

    }

    /**
     * @brief Updates and renders the game world every frame.
     */
    override fun render() {

        // Clear the screen
        Gdx.gl.glClearColor(0.1f, 0.2f, 0.3f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update ECS world
        world.setDelta(Gdx.graphics.deltaTime)
        world.process()
    }

    /**
     * @brief Handles window resizing and updates viewport and camera.
     * @param width The new width of the window.
     * @param height The new height of the window.
     */
    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        camera.position.set(viewport.worldWidth / 2, viewport.worldHeight / 2, 0f)
        (world.getSystem(UISystem::class.java) as UISystem).resize(width, height)
    }

    /**
     * @brief Cleans up resources upon game exit.
     */
    override fun dispose() {
        spriteBatch.dispose()
        font.dispose()
        world.dispose()
        assetManager.dispose()
    }

    /**
     * @brief Sets up input processing for touch events.
     */
    // Input handling (optional, for user input processing)
    private fun setupInputProcessor() {
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                // Handle touch input
                return true
            }
        }
    }
}
