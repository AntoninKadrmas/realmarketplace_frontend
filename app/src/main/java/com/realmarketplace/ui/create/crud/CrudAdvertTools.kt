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

/**
 * A group of *tool*.
 *
 * Class contains functions used in creating request for create or update advert.
 */
class CrudAdvertTools {
    /**
     * A group of *tool_function*.
     *
     * Function used to create request body from given image files.
     *
     * @param imagesFiles list of image files that is going to be inserted intoRequestBody
     * @return list of MultipartBody.Part file images
     */
    fun createImagesFileBody(imagesFiles:ArrayList<File>?):ArrayList<MultipartBody.Part>{
        val bodyList = ArrayList<MultipartBody.Part>()
        if (!imagesFiles.isNullOrEmpty()) {
            var counter=0
            for (file in imagesFiles!!) {
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

    /**
     * A group of *tool_function*.
     *
     * Function used to create request body from given image file.
     *
     * @param imageFile list of image file that is going to be inserted intoRequestBody
     * @return MultipartBody.Part file image
     */
    fun createImageFileBody(imageFile: File?): MultipartBody.Part {
        val requestFile: RequestBody =
            RequestBody.create(
                "image/${
                    imageFile?.absolutePath.toString()
                        .substring(imageFile?.absolutePath.toString().lastIndexOf(".") + 1)
                }".toMediaTypeOrNull(), imageFile!!
            )
        return MultipartBody.Part.createFormData("uploaded_file", imageFile?.name, requestFile)
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to create key pair of old url and their position in list.
     *
     * @param imageFiles all new and old image files list
     * @return list of string url and position pair
     */
    fun getOldFileArray(imageFiles: ArrayList<File>?):ArrayList<String>{
        var oldFiles = ArrayList<String>()
        if (imageFiles != null)
            for (file in imageFiles) {
                if (!file.path.contains("/compressor/")||file.path.count{it=='/'}<=1) oldFiles.add("${file.path};${imageFiles.indexOf(file)}")
            }
        return oldFiles
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to return newly added image files.
     *
     * @param imagesFile all new and old image files list
     * @return list of newly added image files
     */
    fun getNewFileArray(imagesFile: ArrayList<File>?):ArrayList<File>{
        var newFiles = ArrayList<File>()
        if (imagesFile != null)
            for (file in imagesFile) {
                if (file.path.contains("/compressor/")) newFiles.add(file)
            }
        return newFiles
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to handle error response.
     *
     * @param response error response from https request
     * @param context context of activity or fragment
     */
    suspend fun errorResponse(response: Response<Any>, context: Context) {
        val errorBody = response.errorBody()
        val errorResponse: ReturnTypeError? =
            Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
        withContext(Dispatchers.Main) {
            if (response.code() == 401) LogOutAuth.setLogOut(true)
            Toast.makeText(context, "${errorResponse?.error}", Toast.LENGTH_LONG).show()
        }
    }

}