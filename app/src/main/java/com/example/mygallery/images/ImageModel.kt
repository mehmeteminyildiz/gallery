package com.example.mygallery.images

import android.net.Uri

data class ImageModel(
    var id: Long? = null,
    var name: String? = null,
    var path: String? = null,
    var size: Long? = null,
    var isSelected: Boolean? = false,
    var contentUri: Uri?

)
