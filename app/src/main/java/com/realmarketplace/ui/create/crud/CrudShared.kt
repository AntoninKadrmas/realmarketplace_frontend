package com.realmarketplace.ui.create.crud

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.realmarketplace.R
import com.realmarketplace.databinding.FragmentCreateBinding
import com.realmarketplace.ui.create.image.ImageAdapter
import com.realmarketplace.viewModel.PermissionViewModel
import com.realmarketplace.viewModel.ToastObject
import id.zelory.compressor.Compressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import android.os.Build
import android.os.ext.SdkExtensions.getExtensionVersion

private const val ANDROID_R_REQUIRED_EXTENSION_VERSION = 2
/**
 * A group of *tool*.
 *
 * Class contains functions used in both CreateFragment and UpdateDeleteFragment.
 */
class CrudShared(
    private val imageAdapter: ImageAdapter,
    private val crudAdvertViewModel:CrudAdvertViewModel,
    private var binding: FragmentCreateBinding
){
    private val extensionList = listOf("png","jpg","svg","jpeg")
    var deleteUrls = ArrayList<String>()
    val maxImage = 5
    var actualImage = 0
    /**
     * A group of *tool_function*.
     *
     * Object used to drag and drop functionality in image recycler view.
     */
    private val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                ItemTouchHelper.START or
                ItemTouchHelper.END, 0) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition
            if(fromPosition==0){
                return false
            }
            val toPosition = target.adapterPosition
            if(toPosition==0) {
                return false
            }
            crudAdvertViewModel.imagesFile.value?.let { Collections.swap(it, fromPosition-1, toPosition-1) }
            imageAdapter.swap(fromPosition,toPosition)
            imageAdapter.notifyItemMoved(fromPosition, toPosition)
            return true
        }
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }
    }
    var itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(binding.recyclerView)
    /**
     * A group of *tool_function*.
     *
     * Function used to delete image file on which user has clicked.
     *
     * @param file on which user clicked to delte it
     */
    fun clickDelete(file:File){
        actualImage--
        binding.imageCounter.text = "${actualImage}/${maxImage}"
        deleteUrls.add(file.path)
        val indexOfImage = imageAdapter.positionOfImage(file)
        imageAdapter.removeNewImage(file)
        imageAdapter.notifyItemRemoved(indexOfImage)
        crudAdvertViewModel.removeOldFileByPos(indexOfImage-1)
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to delete image file on which user has clicked.
     *
     * @param permissionModel used to check if permission from external storage was granted viz. PermissionViewModel
     * @return intent that is going to run image gallery selection if permission was granted
     */
    fun clickAdd(permissionModel: PermissionViewModel):Intent?{
        permissionModel.setPermissionStorageAsk(true)
        if(permissionModel.permissionStorage.value==true){
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, 5)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                return intent
        }
        return null
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to handle output after image selection ended. Compress new selected files and add new image file into recycle view.
     * Used zelory compressor module
     *
     * @param uris list of uris of newly selected image files
     * @param context context of activity or fragment
     */
    fun handleOutput(uris:List<Uri>,context:Context){
        if (uris.isNotEmpty()) {
            var actualMaximum:Int = maxImage-actualImage
            if(uris.size<=actualMaximum)actualMaximum = uris.size
            else context?.let { ToastObject.makeText(it,"You can use just 5 photos", Toast.LENGTH_SHORT) }
            actualMaximum--
            val fileArray = ArrayList<File>()
            CoroutineScope(Dispatchers.Main).launch {
                for(value in 0..actualMaximum){
                    var file = context?.let { UriToFileConvertor.getRealPathFromURI(it,uris[value])?.let { File(it) } }!!
                    file = context?.let { Compressor.compress(it, file!!) }!!
                    val extension = file?.absolutePath.toString()
                        .substring(file?.absolutePath.toString().lastIndexOf(".") + 1)
                    if(extensionList.contains(extension.toLowerCase())){
                        actualImage++
                        binding.imageCounter.text = "${actualImage}/${maxImage}"
                        imageAdapter.addNewImage(file!!)
                        imageAdapter.notifyItemInserted(actualImage)
                        fileArray.add(file!!)
                    }
                    else ToastObject.makeText(context,"($extension)Allowed file extensions are ${extensionList.joinToString(", ")}.",
                        Toast.LENGTH_LONG)
                }
                if(fileArray.size>0){
                    crudAdvertViewModel.appendNewFile(fileArray)
                }
            }
        }
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to update drop down with loaded condition enum.
     *
     * @param condition condition enum that is going to be inserted in one of the drop downs
     * @param context context of activity or fragment
     */
    fun updateDropDownCondition(condition:ArrayList<String>,context: Context){
        val conditionArrayAdapter =
            context?.let { ArrayAdapter(it, R.layout.adapter_drop_down_price_option,condition) }
        if(binding.priceInput.text.toString()=="") binding.priceInput.setText("")
        binding.conditionInput.setAdapter(conditionArrayAdapter)
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to update drop down with loaded price option enum.
     *
     * @param priceOptions price option enum that is going to be inserted in one of the drop downs
     * @param context context of activity or fragment
     */
    fun updateDropDownPrice(priceOptions:ArrayList<String>,context: Context){
        val priceArrayAdapter =
            context?.let { ArrayAdapter(it, R.layout.adapter_drop_down_price_option,priceOptions) }
        binding.priceOptionInput.setText(priceOptions[0])
        binding.priceOptionInput.setAdapter(priceArrayAdapter)

    }
    /**
     * A group of *tool_function*.
     *
     * Function used to load image files into recycle view if there didn't be already.
     */
    fun loadImages(){
        if(crudAdvertViewModel.imagesFile.value!=null&&binding.imageCounter.text=="0/${maxImage}"){
            actualImage=crudAdvertViewModel.imagesFile.value!!.size
            for(uri in crudAdvertViewModel.imagesFile.value!!){
                imageAdapter.addNewImage(uri)
            }
            binding.imageCounter.text = "${actualImage}/${maxImage}"
        }
    }
}
