package com.example.omega1.ui.create

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.omega1.model.AdvertId
import com.example.omega1.model.AdvertModel
import com.example.omega1.rest.AdvertService
import com.example.omega1.rest.RetrofitInstance
import com.example.omega1.rest.ReturnTypeError
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class CreateViewModel : ViewModel() {
    private var mutableImages= MutableLiveData<ArrayList<Uri>>()
    val images: LiveData<ArrayList<Uri>> get() = mutableImages
    fun appendNew(list:ArrayList<Uri>){
        if(mutableImages.value!=null)
            for(uri in mutableImages.value!!){
                list.add(uri)
            }
        mutableImages.value = list
    }
    fun removeOld(delete_uri: Uri){
        val list =ArrayList<Uri>()
        if(mutableImages.value!=null)
            for(uri in mutableImages.value!!){
                if(uri!=delete_uri)list.add(uri)
            }
        mutableImages.value = list
    }
    private var retroServiceAdvert: AdvertService = RetrofitInstance.getRetroFitInstance().create(
        AdvertService::class.java)
    fun createAdvert(advertModel:AdvertModel,userToken:String, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceAdvert.createAdvert(advertModel,userToken)
            }catch (e: IOException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }catch (e: HttpException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                val body: AdvertId? = Gson().fromJson(Gson().toJson(response.body()), AdvertId::class.java)
                withContext(Dispatchers.Main){
                    if(!images.value.isNullOrEmpty()){
                        val uris = images.value!![0]
                            val file = getFileFromUri(uris,context)
                            println(file?.length())
                            val requestFile: RequestBody =
                                RequestBody.create("image/*".toMediaTypeOrNull(), file!!)
                            val body = MultipartBody.Part.createFormData(
                                "uploaded_file",
                                file?.name,
                                requestFile
                            )
                            uploadImage(body,context)
                    }
                }
            }else{
                val errorBody = response.errorBody()
                try {
                    val errorResponse: ReturnTypeError? = Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"${errorResponse?.error}", Toast.LENGTH_LONG).show()
                    }
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"Server dose not respond.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            return@launch
        }
    }
    private fun uploadImage(filePart:MultipartBody.Part, context:Context){
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceAdvert.uploadAdvertImage(filePart)
            }catch (e: IOException){
                withContext(Dispatchers.Main) {
                    println(e)
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }catch (e: HttpException){
                withContext(Dispatchers.Main) {
                    println(e)
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                val body: AdvertId? = Gson().fromJson(Gson().toJson(response.body()), AdvertId::class.java)
                println(body)
            }else{
                val errorBody = response.errorBody()
                try {
                    val errorResponse: ReturnTypeError? = Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"${errorResponse?.error}", Toast.LENGTH_LONG).show()
                    }
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"Server dose not respond.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            return@launch
        }
    }
    fun getFileFromUri(uri: Uri,context:Context): File? {
        if (uri.path == null) { return null }
        var realPath = String()
        val databaseUri: Uri
        val selection: String?
        val selectionArgs: Array<String>?
        if (uri.path!!.contains("/document/image:")) {
            databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            selection = "_id=?"
            selectionArgs = arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1])
        } else {
            databaseUri = uri
            selection = null
            selectionArgs = null
        }
        try {
            val column = "_data"
            val projection = arrayOf(column)
            val cursor = context.contentResolver.query( databaseUri, projection, selection, selectionArgs, null )
            cursor?.let {
                if (it.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(column)
                    realPath = cursor.getString(columnIndex)
                }
                cursor.close()
            }
        } catch (e: Exception) {
        }
        val path = if (realPath.isNotEmpty()) realPath else {
            when {
                uri.path!!.contains("/document/raw:") -> uri.path!!.replace( "/document/raw:", "" )
                uri.path!!.contains("/document/primary:") -> uri.path!!.replace( "/document/primary:", "/storage/emulated/0/" ) else -> return null
            }
        }
        return File(path)
    }
}
object CompressFile {
    fun getCompressedImageFile(file: File, mContext: Context?): File? {
        return try {
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            if (getFileExt(file.name) == "png" || getFileExt(file.name) == "PNG") {
                o.inSampleSize = 6
            } else {
                o.inSampleSize = 6
            }
            var inputStream = FileInputStream(file)
            BitmapFactory.decodeStream(inputStream, null, o)
            inputStream.close()

            // The new size we want to scale to
            val REQUIRED_SIZE = 100

            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                o.outHeight / scale / 2 >= REQUIRED_SIZE
            ) {
                scale *= 2
            }
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            inputStream = FileInputStream(file)
            var selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2)
            val ei = ExifInterface(file.absolutePath)
            val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> selectedBitmap =
                    rotateImage(selectedBitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> selectedBitmap =
                    rotateImage(selectedBitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> selectedBitmap =
                    rotateImage(selectedBitmap, 270f)
                ExifInterface.ORIENTATION_NORMAL -> {}
                else -> {}
            }
            inputStream.close()


            // here i override the original image file
            val folder = File(Environment.getExternalStorageDirectory().toString() + "/FolderName")
            var success = true
            if (!folder.exists()) {
                success = folder.mkdir()
            }
            if (success) {
                val newFile = File(File(folder.absolutePath), file.name)
                if (newFile.exists()) {
                    newFile.delete()
                }
                val outputStream = FileOutputStream(newFile)
                if (getFileExt(file.name) == "png" || getFileExt(file.name) == "PNG") {
                    selectedBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                } else {
                    selectedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                newFile
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            null
        }
    }

    fun getFileExt(fileName: String): String {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length)
    }

    fun rotateImage(source: Bitmap?, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source!!, 0, 0, source.width, source.height,
            matrix, true
        )
    }
}