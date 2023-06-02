package com.example.mygallery.files

import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import timber.log.Timber
import java.io.File

/**
created by Mehmet E. Yıldız
 **/
class FilesFinder {

    fun loadFiles(context: Context) {
        // buradaki yöntemi kullanarak her klasör ve dosya için isFile ve isDirectory kontrolü yaparak bütün dosyalara erişebiliriz diye
        // düşünüyorum. Alternatif bir yöntem vardır, onları da araştıralım.
        val path = Environment.getExternalStorageDirectory().toString() + "/Download"
        val directory: File = File(path)
        val files = directory.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isFile) {
                    val fileName = file.name
                    val fileSize = file.length()
                    Timber.d("FileName: $fileName, Size: $fileSize bytes")
                }
            }
        }
    }

    fun getFiles(context: Context): ArrayList<FileModel> {
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE
        )

        val selection =
            "${MediaStore.Files.FileColumns.MEDIA_TYPE}=${MediaStore.Files.FileColumns.MEDIA_TYPE_NONE}"
        val sortOrder = "${MediaStore.Files.FileColumns.SIZE} DESC"


        val files = ArrayList<FileModel>()

        val cursor = context.contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            selection,
            null,
            sortOrder
        )

        cursor?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

            while (cursor.moveToNext()) {
                val fileId = cursor.getLong(idColumn)
                val fileName = cursor.getString(nameColumn)
                val fileSize = cursor.getLong(sizeColumn)

                val file = FileModel(
                    id = fileId,
                    name = fileName,
                    path = null,
                    size = fileSize,
                    isSelected = false,
                    contentUri = null
                )
                files.add(file)
                // Dosya bilgilerini kullanarak istediğiniz işlemleri yapabilirsiniz
                // Örneğin, loglama, listeleme, vb.
                Log.d("Dosya Listesi", "ID: $fileId, Adı: $fileName, Boyutu: $fileSize bytes")
            }
        }


        return files

    }
}