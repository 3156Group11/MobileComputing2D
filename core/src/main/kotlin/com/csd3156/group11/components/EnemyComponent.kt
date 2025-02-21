package com.csd3156.group11.components

import com.artemis.Component

open class EnemyComponent : Component(){
    public var inFormation: Boolean = false
    public var isImmune = true
    public var ImmuneTime = 2f
    public var isDying = false
    public var DyingTime = 2f
    public var isLive = false
}

class EnemyBasicComponent : Component() {

}

class EnemyLineComponent(val spawnEdge: Int = 0) : Component() {

}
