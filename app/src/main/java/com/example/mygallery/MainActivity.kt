package com.example.mygallery

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mygallery.databinding.ActivityMainBinding
import com.example.mygallery.files.BiggestFilesActivity
import com.example.mygallery.images.BiggestImagesActivity
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleClicks()

    }

    private fun handleClicks() {
        binding.apply {
            cardImages.setOnClickListener {
                gotoImages()
            }
            cardFiles.setOnClickListener {
                gotoFiles()
            }
            cardVideos.setOnClickListener {
                gotoVideos()
            }
        }
    }

    private fun gotoFiles() {
        val intent = Intent(this@MainActivity, BiggestFilesActivity::class.java)
        startActivity(intent)
    }

    private fun gotoVideos() {
        val intent = Intent(this@MainActivity, BiggestImagesActivity::class.java)
        startActivity(intent)
    }

    private fun gotoImages() {
        val intent = Intent(this@MainActivity, BiggestImagesActivity::class.java)
        startActivity(intent)
    }

}