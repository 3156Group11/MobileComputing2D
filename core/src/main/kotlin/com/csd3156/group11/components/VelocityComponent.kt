package com.csd3156.group11.components

import com.artemis.Component
import com.badlogic.gdx.math.Vector2

class VelocityComponent(
    var velocity: Vector2 = Vector2(),
    var acceleration: Vector2 = Vector2()
)
    : Component()
