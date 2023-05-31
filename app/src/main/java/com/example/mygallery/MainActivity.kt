package com.example.mygallery

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
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
import com.example.mygallery.databinding.ActivityMainBinding
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
                if (it.resultCode == RESULT_OK)
                    Timber.e("successfully deleted")
                else
                    Timber.e("couldn't be deleted")
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

        for (item in willDeleteItemList) {
            Timber.e("item : $item")
//            id=27, name=IMG_20230530_161844.jpg, path=/storage/emulated/0/Pictures/IMG_20230530_161844.jpg, size=140537
            deleteImage(contentResolver, item)
        }
    }

    private fun deleteImage(contentResolver: ContentResolver?, item: ImageModel): Int? {
        contentResolver?.let { contentResolver ->
            val path = item.path
            val selection = "${MediaStore.Images.Media.DATA} = ?"
            val selectionArgs = arrayOf(path)

            return contentResolver.delete(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                selection,
                selectionArgs
            )
        }
        return null
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

    private fun loadImages() {
//        val list = ImagesGallery().listOfImages(this)
        val modelList = ImagesGallery().test(applicationContext)
        binding.apply {
            rv.adapter = adapter
            adapter.setList(modelList)
            adapter.setOnClickListenerCustom {
                Log.e("CLICKED", it)
            }
        }

//        Log.e("X", "Y")
//        Log.e("LIST", "list : $list")
//
//
//        binding.apply {
//            rv.adapter = adapter
//
//            adapter.setList(list)
//            adapter.setOnClickListenerCustom {
//                Log.e("CLICKED", it)
//            }
//        }
    }

}