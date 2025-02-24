package com.csd3156.group11.android

import android.content.res.Resources
import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.csd3156.group11.Main
import com.csd3156.group11.enums.GameState
import com.csd3156.group11.resources.Globals

/** Launches the Android application. */
class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize(Main(
            widthPix = Resources.getSystem().displayMetrics.widthPixels,
            heightPix = Resources.getSystem().displayMetrics.heightPixels),
            AndroidApplicationConfiguration().apply {
            // Configure your application here.
            useImmersiveMode = true // Recommended, but not required.
            useCompass = true
            useAccelerometer = true
            useGyroscope = true
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (Globals.currentState == GameState.GAME_STAGE)
            Globals.isPausing = true
    }

    override fun onStop()
    {
        super.onStop()
        if (Globals.currentState == GameState.GAME_STAGE)
            Globals.isPausing = true
    }

}
