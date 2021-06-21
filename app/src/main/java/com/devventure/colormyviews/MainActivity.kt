package com.devventure.colormyviews

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    private var changeColor = R.color.grey
    private lateinit var colors: ArrayList<Int>

    private val sharedPreferences: SharedPreferences
        get() {
            return getSharedPreferences("colors", Context.MODE_PRIVATE)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadPreferences()
    }

    private fun loadPreferences() {
        colors = ArrayList()
        for (i in 0..4) {
            var view: View = findViewById(resources.getIdentifier("box_${i}", "id", packageName))
            var color = sharedPreferences.getInt("box_${i}", changeColor)
            view.setBackgroundResource(color)
            colors.add(i, color)
        }
    }

    private fun savePreferences() {
        val editor = sharedPreferences.edit()
        for (i in 0..4) {
            editor.putInt("box_${i}", if (colors.isEmpty()) R.color.grey else colors[i])
        }
        editor.commit()
    }

    fun onClickTextView(view: View) {
        view.setBackgroundResource(changeColor)
        var index = resources.getResourceEntryName(view.id).split("_").last().toInt()
        colors[index] = changeColor
    }

    fun onClickButton(view: View) {
        changeColor = when(view.id) {
            R.id.red_button -> R.color.red
            R.id.green_button -> R.color.green
            else -> R.color.yellow
        }
    }

    override fun onStop() {
        super.onStop()
        savePreferences()
    }
}