package com.devventure.colormyviews

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import android.text.format.DateFormat
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.app.ActivityCompat
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() {
    private var changeColor = R.color.grey
    private var boxes = arrayOf(
        R.id.box_0,
        R.id.box_1,
        R.id.box_2,
        R.id.box_3,
        R.id.box_4
    )
    private val sharedPreferences: SharedPreferences
        get() {
            return getSharedPreferences("colors", Context.MODE_PRIVATE)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
        loadPreferences()
    }

    private fun loadPreferences() {
        for(box_id in boxes) {
            findViewById<TextView>(box_id).setBackgroundResource(
                sharedPreferences.getInt(
                    "box_$box_id",
                    changeColor
                )
            )
        }
    }

    private fun checkPermission() {
        var permission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if(permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1
            )
        }
    }

    private fun convertViewToBitmap(view: View, bitmapWidth: Int, bitmapHeight: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun shareScreenshot(view: View) {
        try{
            val filename = "Screenshot_${DateFormat.format("ddMMyyyy-hhmmss", Date())}.png"
            val file = File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath + File.separator + filename)
            val fileOutputStream = FileOutputStream(file)
            val bitmap = takeScreenshot(view.parent as View)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            sendScreenshot(file)
        } catch (e: Exception) {
            Toast.makeText(this, resources.getText(R.string.send_image_error), Toast.LENGTH_LONG).show()
        }
    }

    private fun takeScreenshot(view: View): Bitmap {
        val guideline = findViewById<Guideline>(R.id.guideline2)
        val params = guideline.layoutParams as ConstraintLayout.LayoutParams
        val bitmapHeight = (view.height * params.guidePercent).toInt()

        return convertViewToBitmap(view, view.width, bitmapHeight)
    }

    private fun sendScreenshot(image: File) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/image"
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(image.absolutePath))
        startActivity(Intent.createChooser(intent, resources.getString(R.string.share_message)))
    }

    fun onClickTextView(view: View) {
        view.setBackgroundResource(changeColor)
        val editor = sharedPreferences.edit()
        editor.putInt("box_${view.id}", changeColor)
        editor.commit()
    }

    fun onClickButton(view: View) {
        changeColor = when(view.id) {
            R.id.red_button -> R.color.red
            R.id.green_button -> R.color.green
            else -> R.color.yellow
        }
    }

}