package com.example.mygallery.myApp

import android.Manifest
import android.app.RecoverableSecurityException
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.mygallery.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private val adapter = GalleryItemAdapter()
    val PERMISSION_REQUEST_CODE = 100

    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkStoragePermission()) {
            loadImages()
        }
        handleClicks()
        intentSenderLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == RESULT_OK) loadImages()
                else Toast.makeText(this@MainActivity, "Couldn't deleted", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun handleClicks() {
        binding.apply {
            cardDeleteSelectedImages.setOnClickListener {
                deleteProcess()
            }
        }
    }

    private fun deleteProcess() {
        val willDeleteItemList = adapter.getSelectedItems()
        val photoUris = ArrayList<Uri?>()
        willDeleteItemList.map { it -> photoUris.add(it.contentUri) }

        lifecycleScope.launch {
            deleteItem(photoUris = photoUris)
        }
    }

    private suspend fun deleteItem(photoUris: ArrayList<Uri?>) {

        withContext(Dispatchers.IO) {
            for (item in photoUris) {
                try {
                    contentResolver.delete(item!!, null, null)
                } catch (e: SecurityException) {
                    val intentSender = when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                            MediaStore.createDeleteRequest(
                                contentResolver, listOf(item)
                            ).intentSender
                        }

                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                            val recoverableSecurityException = e as? RecoverableSecurityException
                            recoverableSecurityException?.userAction?.actionIntent?.intentSender
                        }

                        else -> null
                    }
                    intentSender?.let { sender ->
                        intentSenderLauncher.launch(
                            IntentSenderRequest.Builder(sender).build()
                        )
                    }
                }
            }

        }
    }


    private fun loadImages() {
        val modelList = ImagesGallery().test(applicationContext)
        binding.apply {
            rv.adapter = adapter
            adapter.setList(modelList)
            adapter.setOnClickListenerCustom {
                Log.e("CLICKED", it.contentUri.toString())
            }
        }
    }

    private fun checkStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Timber.e("Tiramisu or higher")
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Timber.e("1")
                // İzin verilmemiş, kullanıcıdan izin iste
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    PERMISSION_REQUEST_CODE
                )
                return false
            } else {
                Timber.e("2")
                return true
                // İzin zaten verilmiş
                // Devam edebilirsiniz
            }
        } else {
            Timber.e("lower than Tiramisu")
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Timber.e("1")
                // İzin verilmemiş, kullanıcıdan izin iste
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
                return false
            } else {
                Timber.e("2")
                // İzin zaten verilmiş
                // Devam edebilirsiniz
                return true
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this@MainActivity,
                    "Read external storage permission granted",
                    Toast.LENGTH_SHORT
                ).show()
                loadImages()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Read external storage permission deniede",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


}