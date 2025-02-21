package com.csd3156.group11

import EmitterSystem
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
import com.csd3156.group11.components.ColliderComponent
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.PlayerInputComponent
import com.csd3156.group11.components.SpriteComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.VelocityComponent
import com.csd3156.group11.prefabs.Enemy
import com.csd3156.group11.prefabs.Player
import com.csd3156.group11.systems.AssetSystem
import com.csd3156.group11.systems.CollisionSystem
import com.csd3156.group11.systems.EnemySystem
import com.csd3156.group11.systems.PhysicsSystem
import com.csd3156.group11.systems.PlayerInputSystem
import com.csd3156.group11.systems.RenderSystem
import ktx.assets.Asset
import org.w3c.dom.Text

var assetManager:AssetSystem = AssetSystem()



fun createCircleTexture(diameter: Int, color: Color = Color.WHITE): TextureRegion {
    val pixmap = Pixmap(diameter, diameter, Pixmap.Format.RGBA8888)
    pixmap.setColor(color)
    pixmap.fillCircle(diameter / 2, diameter / 2, diameter / 2)

    val texture = Texture(pixmap)
    pixmap.dispose() // Free memory

    return TextureRegion(texture)
}


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

    override fun create() {

        camera = OrthographicCamera()
        viewport = FitViewport(scrWidth, scrHeight, camera)
        viewport.apply()
        camera.position.set(viewport.worldWidth / 2, viewport.worldHeight / 2, 0f)
        assetManager.loadTexturesFromFolder("textures")
        assetManager.loadSFXFromFolder("audio/sfx")
        assetManager.loadBGMFromFolder("audio/bgm")


        // Initialize rendering tools
        spriteBatch = SpriteBatch()
        font = BitmapFont()

        val uiSystem = UISystem(spriteBatch, viewport)

        //True for WASD
        //False for Tilt
        val isDebugMode = true
        val particleTexture = createCircleTexture(360)
        // Configure ECS world
        val worldConfiguration = WorldConfigurationBuilder()
            .with(GameStateSystem(viewport))
            .with(PlayerInputSystem(isDebugMode))
            .with(EnemySystem())
            .with(PhysicsSystem())
            .with(CollisionSystem())
            .with(EmitterSystem(viewport.worldWidth, viewport.worldHeight))
            .with(ParticleSystem(spriteBatch, particleTexture))
            .with(uiSystem)
            .with(RenderSystem(spriteBatch, camera))
            .build()


        world = com.artemis.World(worldConfiguration)

        Gdx.input.inputProcessor = uiSystem.stage

    }

    override fun render() {
        // Clear the screen
        Gdx.gl.glClearColor(0.1f, 0.2f, 0.3f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
            // Update ECS world
        world.setDelta(Gdx.graphics.deltaTime)
        world.process()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        camera.position.set(viewport.worldWidth / 2, viewport.worldHeight / 2, 0f)
        (world.getSystem(UISystem::class.java) as UISystem).resize(width, height)
    }

    override fun dispose() {
        spriteBatch.dispose()
        font.dispose()
        world.dispose()
        assetManager.dispose()
    }

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
