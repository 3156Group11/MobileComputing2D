/**
 * @file AssetSystem.kt
 * @brief Manages the loading and storage of game assets including textures, fonts, sound effects, and background music.
 *
 * The AssetSystem provides methods for dynamically loading assets from specified folders and managing their lifecycle.
 */
package com.csd3156.group11.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import java.util.Locale
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader

/**
 * @class AssetSystem
 * @brief Handles asset management for the game, including textures, fonts, and audio.
 *
 * This system is responsible for loading and retrieving game assets, ensuring they are efficiently managed.
 */
class AssetSystem() {
    private var assetManager:AssetManager = AssetManager()

    init {
        assetManager.setLoader(BitmapFont::class.java, FreetypeFontLoader(assetManager.fileHandleResolver))
        assetManager.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(assetManager.fileHandleResolver))
    }

    /**
     * @brief Returns the AssetManager instance.
     * @return The AssetManager used by this system.
     */
    public fun system() : AssetManager
    {
        return assetManager
    }

    /**
     * @brief Loads all texture assets from a specified folder.
     * @param folderPath The path of the folder containing texture files.
     */
    public fun loadTexturesFromFolder(folderPath: String)
    {
        val folder = Gdx.files.internal(folderPath)
        val files = folder.list()

        for (file in files) {
            if (file.isDirectory) {
                // If it's a subfolder, recursively call this function
                loadTexturesFromFolder(folderPath + file.name() + "/")
            }
            else {
                if (file.extension().lowercase() in listOf("png", "jpg", "jpeg", "bmp")) {
                    val filePath = folderPath + "/" + file.name()
                    assetManager.load(filePath, Texture::class.java)
                    while (!assetManager.update())
                    {
                        val progress = assetManager.progress
                    }
                }
            }
        }
    }

    /**
     * @brief Loads all font assets from a specified folder.
     * @param folderPath The path of the folder containing font files.
     */
    public fun loadFontsFromFolder(folderPath: String)
    {
        println("02")

        val folder = Gdx.files.internal(folderPath)
        val files = folder.list()

        for (file in files) {
            if (file.isDirectory) {
                // If it's a subfolder, recursively call this function
                loadFontsFromFolder(folderPath + file.name() + "/")
            }
            else {
                if (file.extension().lowercase() in listOf("ttf")) {
                    val filePath = folderPath + "/" + file.name()

                    val fontParams = FreetypeFontLoader.FreeTypeFontLoaderParameter().apply {
                        fontFileName = filePath
                        fontParameters.size = 64 // Set font size
                        fontParameters.minFilter = Texture.TextureFilter.Linear
                        fontParameters.magFilter = Texture.TextureFilter.Linear
                    }

                    assetManager.load(filePath, BitmapFont::class.java, fontParams)
                    while (!assetManager.update())
                    {
                        val progress = assetManager.progress
                    }
                }
            }
        }
    }

    /**
     * @brief Loads all sound effect assets from a specified folder.
     * @param folderPath The path of the folder containing sound effect files.
     */
    public fun loadSFXFromFolder(folderPath: String) {
        // Get the folder as a FileHandle
        val folder = Gdx.files.internal(folderPath)

        // List all files and subfolders in the directory
        val files = folder.list()

        // Loop through each file/subfolder
        for (file in files) {
            if (file.isDirectory) {
                loadSFXFromFolder(folderPath + file.name() + "/")
            } else {
                if (file.extension().lowercase(Locale.ROOT) in listOf("mp3", "ogg", "wav")) {
                    val filePath = folderPath + "/" + file.name()

                    if (Gdx.files.internal(filePath).exists()) {
                        assetManager.load(filePath, Sound::class.java)
                        assetManager.finishLoading() // Ensure it's fully loaded
                        println("Loaded SFX: $filePath")
                    } else {
                        println("SFX file not found: $filePath")
                    }
                    while (!assetManager.update())
                    {
                        val progress = assetManager.progress
                    }
                }
            }
        }
    }

    /**
     * @brief Loads all background music assets from a specified folder.
     * @param folderPath The path of the folder containing background music files.
     */
    public fun loadBGMFromFolder(folderPath: String) {
        // Get the folder as a FileHandle
        val folder = Gdx.files.internal(folderPath)

        // List all files and subfolders in the directory
        val files = folder.list()

        // Loop through each file/subfolder
        for (file in files) {
            if (file.isDirectory) {
                loadBGMFromFolder(folderPath + file.name() + "/")
            } else {
                if (file.extension().lowercase(Locale.ROOT) in listOf("mp3", "ogg", "wav")) {
                    val filePath = folderPath + "/" + file.name()
                    if (Gdx.files.internal(filePath).exists()) {
                        assetManager.load(filePath, Music::class.java)
                        assetManager.finishLoading() // Ensure it's fully loaded
                        println("Loaded BGM: $filePath")
                    } else {
                        println("BGM file not found: $filePath")
                    }
                    while (!assetManager.update())
                    {
                        val progress = assetManager.progress
                    }
                }
            }
        }
    }

    /**
     * @brief Disposes of all loaded assets.
     */
    public fun dispose()
    {
        assetManager.dispose()
    }
}
