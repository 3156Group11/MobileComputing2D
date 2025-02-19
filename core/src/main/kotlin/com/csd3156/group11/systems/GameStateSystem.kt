import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.csd3156.group11.GameState
import com.csd3156.group11.components.EnemyComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.components.VelocityComponent
import com.csd3156.group11.prefabs.Player

class GameStateSystem() : BaseEntitySystem(Aspect.all(TransformComponent::class.java)) {
    var currentState: GameState = GameState.MAIN_MENU
        private set

    private var pendingState: GameState? = null
    private var initialized = false
    fun changeState(newState: GameState) {
        if (newState != currentState) {
            pendingState = newState
        }
    }

    override fun processSystem() {

        if (initialized == false)
        {
            enterState(currentState)
            initialized = true
        }
        pendingState?.let {
            exitState(currentState)
            currentState = it
            enterState(currentState)
            pendingState = null
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
        val player = Player()
        player.Create(world)
        // Example: Create UI entities for Main Menu
    }

    private fun createGameEntities() {
        // Example: Create player, enemies, and other game entities

    }

    private fun createHighScoreEntities() {
        // Example: Create high score UI elements
    }
}
