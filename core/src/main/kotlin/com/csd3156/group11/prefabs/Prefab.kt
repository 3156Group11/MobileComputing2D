package com.csd3156.group11.prefabs

import com.artemis.World

abstract class Prefab {
    public abstract fun Create(world: World)
    public var ID = 0
}
