package com.csd3156.group11.components

import com.artemis.Component

open class EnemyComponent : Component(){
    public var inFormation: Boolean = false
    public var isImmune = true
    public var ImmuneTime = 2f
    public var isDying = false
    public var DyingTime = 2f
    public var isLive = false
    public var speed: Float = 20f

    var formationSpawnComplete: Boolean = true
}

class EnemyBasicComponent : Component() {

}

class EnemyLineComponent() : Component() {
    public var spawnEdge = 0
}
