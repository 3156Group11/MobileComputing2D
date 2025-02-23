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

            // Decrement duration timer
            fxComp.duration -= world.delta
            if (fxComp.duration <= 0f) {
                world.delete(fxId)
                continue
            }


            if (fxComp.fxType == PowerUpType.SHIELD && fxComp.followEntityId != -1) {
                val playerTransform = transformMapper.get(fxComp.followEntityId)
                if (playerTransform != null) {
                    // Center FX around player
                    fxTransform.position.set(
                        playerTransform.position.x - fxTransform.scale.x / 2f,
                        playerTransform.position.y - fxTransform.scale.y / 2f
                    )
                }
            }
            // For bomb FX, no need to follow; it stays at the detonation location.
        }
    }
}
