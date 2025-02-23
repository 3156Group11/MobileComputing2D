package com.csd3156.group11.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.csd3156.group11.components.FXComponent
import com.csd3156.group11.components.TransformComponent
import com.csd3156.group11.enums.PowerUpType

class FXSystem : BaseEntitySystem(Aspect.all(FXComponent::class.java, TransformComponent::class.java)) {

    private lateinit var fxMapper: ComponentMapper<FXComponent>
    private lateinit var transformMapper: ComponentMapper<TransformComponent>

    override fun processSystem() {
        val fxEntities = subscription.entities

        for (i in 0 until fxEntities.size()) {
            val fxId = fxEntities[i]
            val fxComp = fxMapper[fxId]
            val fxTransform = transformMapper[fxId]

            // Skip duration check for shield FX
            if (fxComp.fxType != PowerUpType.SHIELD) {
                fxComp.duration -= world.delta
                if (fxComp.duration <= 0f) {
                    world.delete(fxId)
                    continue
                }
            }

            // Make Shield FX follow the player
            if (fxComp.fxType == PowerUpType.SHIELD && fxComp.followEntityId != -1) {
                val playerTransform = transformMapper.get(fxComp.followEntityId)
                if (playerTransform != null) {
                    val playerCenterX = playerTransform.position.x + (playerTransform.scale.x / 2f)
                    val playerCenterY = playerTransform.position.y + (playerTransform.scale.y / 2f)

                    fxTransform.position.set(
                        playerCenterX - (fxTransform.scale.x / 2f),
                        playerCenterY - (fxTransform.scale.y / 2f)
                    )
                }
            }
        }
    }
}
