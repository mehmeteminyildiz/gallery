package com.example.mygallery.files

import android.net.Uri

data class FileModel(

    var id: Long? = null,
    var name: String? = null,
    var path: String? = null,
    var size: Long? = null,
    var isSelected: Boolean? = false,
    var contentUri: Uri?
)
