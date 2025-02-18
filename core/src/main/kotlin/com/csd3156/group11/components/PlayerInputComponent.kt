package com.csd3156.group11.components

import com.artemis.Component
import com.badlogic.gdx.math.Vector2

class PlayerInputComponent(
    var tiltDirection: Vector2 = Vector2(),
    var hasShield: Boolean = false
    ): Component()
