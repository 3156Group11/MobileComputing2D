package com.csd3156.group11.components

import com.artemis.Component

class EnemyComponent : Component(){
    public val InFormation = false
    public var isImmune = true
    public var ImmuneTime = 2f
    public var isDying = false
    public var DyingTime = 2f
    public var isLive = false
}
