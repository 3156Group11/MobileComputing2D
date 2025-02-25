/**
 * @file TagComponent.kt
 * @brief  Holds the Tag Component and its data members and methods
 */
package com.csd3156.group11.components
import com.artemis.Component
import com.csd3156.group11.enums.Tag

/**
 * @brief Assigns a tag to an entity for identification or grouping.
 *
 * This component is used to label an entity with a specific tag, which can be used
 * by systems for filtering, grouping, or handling entities in a specialized manner.
 */
class TagComponent
    (val tag: Tag = Tag.NONE, name: String = "")
    : Component()
