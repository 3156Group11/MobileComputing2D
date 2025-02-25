/**
 * @file ColliderComponent.kt
 * @brief  Holds the Collider Component and its data members and methods
 */

package com.csd3156.group11.components

import com.artemis.Component

/**
 * @brief Component that defines a circular collision boundary.
 *
 * The ColliderComponent provides a simple circular collision shape
 * for an entity using a specified radius.
 */
class ColliderComponent
    (var radius: Float = 0f)
    : Component()
