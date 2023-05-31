package com.example.mygallery.files

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mygallery.databinding.ActivityBiggestFilesBinding
import com.example.mygallery.images.GalleryItemAdapter

class BiggestFilesActivity : AppCompatActivity() {
    val TAG = "BiggestFilesActivity"

    private lateinit var binding: ActivityBiggestFilesBinding
    private val adapter = GalleryItemAdapter()
    val PERMISSION_REQUEST_CODE = 100

    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityBiggestFilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkStoragePermission()) {
            loadFiles()
        }
        handleClicks()
        observeSelectedItemCount()
        intentSenderLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == RESULT_OK) loadFiles()
                else Toast.makeText(this@BiggestFilesActivity, "Couldn't deleted", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun handleClicks() {
        binding.apply {
            cardDeleteSelectedFiles.setOnClickListener {
                deleteProcess()
            }
        }
    }
}