package com.example.savethecat_colormatching

import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.Validators.and
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    companion object {
        var staticSelf: MainActivity? = null
        var rootView: View? = null
        var isThemeLight:Boolean = true
    }




    fun setCurrentTheme() {
        // Set theme for android pie
        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> isThemeLight = false
        }
        Log.i("Color?", "${isThemeLight}")
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rootView = window.decorView.rootView
        staticSelf = this
        setCurrentTheme()


    }
}
