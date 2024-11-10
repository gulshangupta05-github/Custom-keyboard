package com.app.jayesh.customkeyboard

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var vibrationCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("keyboardPrefs", MODE_PRIVATE)
        vibrationCheckBox = findViewById(R.id.vibrationCheckBox)

        // Set checkbox state from SharedPreferences
        vibrationCheckBox.isChecked = sharedPreferences.getBoolean("isVibrationEnabled", false)

        vibrationCheckBox.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("isVibrationEnabled", isChecked)
            editor.apply()
        }

    }
}