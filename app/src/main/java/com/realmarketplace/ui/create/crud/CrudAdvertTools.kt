package com.realmarketplace.ui.create.crud

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class CrudAdvertTools {
    fun createImagesFileBody(skipFirs:Int,imagesFile:ArrayList<File>?):ArrayList<MultipartBody.Part>{
        val bodyList = ArrayList<MultipartBody.Part>()
        if (!imagesFile.isNullOrEmpty()) {
            var counter=0
            for (file in imagesFile!!) {
                counter++
                if(counter<=skipFirs)continue
                val requestFile: RequestBody =
                    RequestBody.create("image/${file?.absolutePath.toString()
                        .substring(file?.absolutePath.toString().lastIndexOf(".") + 1)
                    }".toMediaTypeOrNull(), file!!
                    )
                val body = MultipartBody.Part.createFormData("uploaded_file", file?.name, requestFile)
                bodyList.add(body)
            }
        }
        return bodyList
    }
    fun createImageFileBody(file:File?):MultipartBody.Part{
        val requestFile: RequestBody =
            RequestBody.create("image/${file?.absolutePath.toString()
                .substring(file?.absolutePath.toString().lastIndexOf(".") + 1)
            }".toMediaTypeOrNull(), file!!
            )
        val body = MultipartBody.Part.createFormData("uploaded_file", file?.name, requestFile)
        return body
    }
}