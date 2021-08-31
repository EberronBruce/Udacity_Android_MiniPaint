package com.redravencomputing.minipaint

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.WindowInsets
import android.view.WindowInsetsController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myCanvasView = MyCanvasView(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d("onCreate", "In R build")
            window.setDecorFitsSystemWindows(false)
        } else {
            Log.d("onCreate", "Not In R build")
            @Suppress("DEPRECATION")
            myCanvasView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
            actionBar?.hide()
        }
        myCanvasView.contentDescription = getString(R.string.canvasContentDescription)
        setContentView(myCanvasView)

        //This is used to hide the status bar and other navigation bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                it.hide(WindowInsets.Type.systemBars())
            }
        }
    }
}