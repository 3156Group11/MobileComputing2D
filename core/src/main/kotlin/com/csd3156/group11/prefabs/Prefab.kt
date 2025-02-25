/**
 * @file Prefab.kt
 * @brief  This file contains the base prefab
 */
package com.csd3156.group11.prefabs

import com.artemis.World

/**
 * @abstract
 * @class Prefab
 * @brief Base class for entity prefabs.
 *
 * The Prefab class provides a common interface for creating entities in the ECS world.
 * Subclasses must implement the Create() method to initialize components and set up the entity.
 */
abstract class Prefab {
    public abstract fun Create(world: World):Int
    public var ID = 0
}
