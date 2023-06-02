package com.example.mygallery.files

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.mygallery.R
import com.example.mygallery.databinding.ActivityBiggestFilesBinding
import com.example.mygallery.images.ImagesGallery
import kotlinx.coroutines.launch
import timber.log.Timber

class BiggestFilesActivity : AppCompatActivity() {
    val TAG = "BiggestFilesActivity"

    private lateinit var binding: ActivityBiggestFilesBinding
    private val adapter = FilesItemAdapter()
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
                else Toast.makeText(
                    this@BiggestFilesActivity,
                    "Couldn't deleted",
                    Toast.LENGTH_SHORT
                )
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

    private fun observeSelectedItemCount() {
        binding.apply {
            adapter.selectedFileCount.observe(this@BiggestFilesActivity, Observer { size ->
                if (size > 0) {
                    cardDeleteSelectedFiles.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this@BiggestFilesActivity,
                            R.color.r_black
                        )
                    )
                    (cardDeleteSelectedFiles as CardView).isEnabled = true
                } else {
                    cardDeleteSelectedFiles.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this@BiggestFilesActivity,
                            R.color.r_content
                        )
                    )
                    (cardDeleteSelectedFiles as CardView).isEnabled = false
                }
            })
        }
    }


    private fun deleteProcess() {
        val willDeleteItemList = adapter.getSelectedItems()
        val fileUris = ArrayList<Uri?>()
        willDeleteItemList.map { fileUris.add(it.contentUri) }

        lifecycleScope.launch {
            deleteFiles(filePaths = fileUris)
        }
    }

    private suspend fun deleteFiles(filePaths: ArrayList<Uri?>) {

        Timber.e("deleteFiles : $filePaths")

//        withContext(Dispatchers.IO) {
//            for (item in filePaths) {
//                try {
//                    contentResolver.delete(item!!, null, null)
//                } catch (e: SecurityException) {
//                    val intentSender = when {
//                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
//                            MediaStore.createDeleteRequest(
//                                contentResolver, listOf(item)
//                            ).intentSender
//                        }
//
//                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
//                            val recoverableSecurityException = e as? RecoverableSecurityException
//                            recoverableSecurityException?.userAction?.actionIntent?.intentSender
//                        }
//
//                        else -> null
//                    }
//                    intentSender?.let { sender ->
//                        intentSenderLauncher.launch(
//                            IntentSenderRequest.Builder(sender).build()
//                        )
//                    }
//                }
//            }
//
//        }
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
                    this@BiggestFilesActivity,
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
                    this@BiggestFilesActivity,
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
                    this@BiggestFilesActivity,
                    "Read external storage permission granted",
                    Toast.LENGTH_SHORT
                ).show()
                loadFiles()
            } else {
                Toast.makeText(
                    this@BiggestFilesActivity,
                    "Read external storage permission deniede",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadFiles() {
//        val modelList = FilesFinder().loadFiles(applicationContext)
        val modelList = FilesFinder().getFiles(applicationContext)
        binding.apply {
            rvFiles.adapter = adapter
            adapter.setList(modelList)
            adapter.setOnClickListenerCustom {
                Log.e("CLICKED", it.contentUri.toString())
            }
        }
    }
}