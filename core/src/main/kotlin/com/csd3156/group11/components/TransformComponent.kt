package com.csd3156.group11.components

import com.artemis.Component
import com.badlogic.gdx.math.Vector2

class TransformComponent(
    var position: Vector2 = Vector2(),
    var rotation: Float = 0f,
    var scale : Vector2 = Vector2()) :
    Component()
