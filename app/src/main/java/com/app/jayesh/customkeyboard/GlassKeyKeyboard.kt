package com.app.jayesh.customkeyboard


import android.app.Service;
import android.content.Context
import android.content.Intent;
import android.content.SharedPreferences
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup
import android.view.View;
import android.view.inputmethod.InputConnection;


class GlassKeyKeyboard : InputMethodService(), KeyboardView.OnKeyboardActionListener {
    private lateinit var kv: KeyboardView
    private lateinit var keyboard: Keyboard

    private var isCaps = false
    private var isSpecial = false;
    private lateinit var vibrator: Vibrator
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate() {
        super.onCreate()
        // Initialize vibrator and SharedPreferences
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        sharedPreferences = getSharedPreferences("keyboardPrefs", MODE_PRIVATE)
    }
    override fun onCreateInputView(): View {
        kv = layoutInflater.inflate(R.layout.keyboard, null) as KeyboardView
        keyboard = Keyboard(this, R.xml.qwerty)
        kv.keyboard = keyboard
        kv.setOnKeyboardActionListener(this)
        val qk = keyboard.keys.find {
            it.codes.contains(44)
        }
        qk?.label = "?123"
        val qm = keyboard.keys.find {
            it.codes.contains(63)
        }
        qm?.label = "?"
        return kv
    }

    override fun onPress(primaryCode: Int) {
    }

    override fun onRelease(i: Int) {}

    override fun onKey(i: Int, ints: IntArray) {
        val ic: InputConnection = currentInputConnection
        playClick(i)

        // Check if vibration is enabled
        if (sharedPreferences.getBoolean("isVibrationEnabled", false)) {
            vibrate()
        }
        when (i) {
            Keyboard.KEYCODE_DELETE -> ic.deleteSurroundingText(1, 0)
            Keyboard.KEYCODE_SHIFT -> {
                isCaps = !isCaps
                keyboard.isShifted = isCaps
                kv.invalidateAllKeys()
            }

            44 -> {
                if (!isSpecial) {
                    kv.keyboard = Keyboard(this, R.xml.qwerty)
                    isSpecial = !isSpecial
                } else {
                    kv.keyboard = Keyboard(this, R.xml.keyboard_special_chars)
                    isSpecial = !isSpecial
                }

            }

            Keyboard.KEYCODE_DONE -> ic.sendKeyEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_ENTER
                )
            )

            else -> {
                var code = i.toChar()
                if (Character.isLetter(code) && isCaps) {
                    code = Character.toUpperCase(code)
                }
                ic.commitText(code.toString(), 1)
            }
        }
    }
    private fun vibrate() {
        // For API 26 and above, use VibrationEffect
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(50) // Vibrate for 50 milliseconds for older devices
        }
    }
    private fun playClick(i: Int) {
        val am: AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        when (i) {
            32 -> am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR)
            Keyboard.KEYCODE_DONE, 10 -> am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN)
            Keyboard.KEYCODE_DELETE -> am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE)
            else -> am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD)
        }
    }

    override fun onText(charSequence: CharSequence) {}

    override fun swipeLeft() {}

    override fun swipeRight() {}

    override fun swipeDown() {}

    override fun swipeUp() {}

}