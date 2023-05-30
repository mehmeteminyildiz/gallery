package com.example.mygallery

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mygallery.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private val adapter = GalleryItemAdapter()
    val PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkStoragePermission()


    }


    fun checkStoragePermission() {
        loadImages()

//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            Log.e(TAG, "1")
//            // İzin verilmemiş, kullanıcıdan izin iste
//            ActivityCompat.requestPermissions(
//                this@MainActivity,
//                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                PERMISSION_REQUEST_CODE
//            )
//        } else {
//            Log.e(TAG, "2")
//            // İzin zaten verilmiş
//            // Devam edebilirsiniz
//
//        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
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