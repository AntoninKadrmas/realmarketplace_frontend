package com.realmarketplace.ui.create.crud

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.realmarketplace.rest.ReturnTypeError
import com.realmarketplace.ui.auth.LogOutAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class CrudAdvertTools {
    fun createImagesFileBody(imagesFile:ArrayList<File>?):ArrayList<MultipartBody.Part>{
        val bodyList = ArrayList<MultipartBody.Part>()
        if (!imagesFile.isNullOrEmpty()) {
            var counter=0
            for (file in imagesFile!!) {
                counter++
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
    fun getOldFileArray(imagesFile: ArrayList<File>?):ArrayList<String>{
        var oldFiles = ArrayList<String>()
        if (imagesFile != null)
            for (file in imagesFile) {
                if (!file.path.contains("/compressor/")) oldFiles.add("${file.path};${imagesFile.indexOf(file)}")
            }
        return oldFiles
    }
    fun getNewFileArray(imagesFile: ArrayList<File>?):ArrayList<File>{
        var newFiles = ArrayList<File>()
        if (imagesFile != null)
            for (file in imagesFile) {
                if (file.path.contains("/compressor/")) newFiles.add(file)
            }
        return newFiles
    }
    suspend fun errorResponse(response: Response<Any>, code: Int, context: Context) {
        val errorBody = response.errorBody()
        val errorResponse: ReturnTypeError? =
            Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
        withContext(Dispatchers.Main) {
            if (code == 401) LogOutAuth.setLogOut(true)
            Toast.makeText(context, "${errorResponse?.error}", Toast.LENGTH_LONG).show()
        }
    }

}