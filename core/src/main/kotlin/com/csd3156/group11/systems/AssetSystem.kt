package com.csd3156.group11.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import java.util.Locale

class AssetSystem {
    private var assetManager:AssetManager = AssetManager()
    public fun system() : AssetManager
    {
        return assetManager
    }
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
                        println("Loading: " + progress * 100 + "%")
                    }
                }
            }
        }
    }

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
                    val filePath = folderPath + file.name()
                    assetManager.load(filePath, Sound::class.java) // For sound effects
                    while (!assetManager.update())
                    {
                        val progress = assetManager.progress
                        println("Loading: " + progress * 100 + "%")
                    }
                }
            }
        }
    }

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
                    val filePath = folderPath + file.name()
                    assetManager.load(filePath, Music::class.java) // For sound effects
                    while (!assetManager.update())
                    {
                        val progress = assetManager.progress
                        println("Loading: " + progress * 100 + "%")
                    }
                }
            }
        }
    }

    public fun dispose()
    {
        assetManager.dispose()
    }
}
