package com.example.mygallery

import android.content.Context
import android.provider.MediaStore
import android.util.Log

class ImagesGallery {

    fun test(context: Context): ArrayList<ImageModel> {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.SIZE
        )
        val orderBy = MediaStore.Images.Media.SIZE + " DESC"
        val selection = ""
        val selectionArgs = arrayOf<String>()
        Log.e("TAGGGGG", "ÇALIŞTI")

        val modelList = ArrayList<ImageModel>()

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            orderBy
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val path = cursor.getString(dataColumn)
                val size = cursor.getLong(sizeColumn)
                val model = ImageModel(id, name, path, size)
                modelList.add(model)

            }
        }

        return modelList

    }


}