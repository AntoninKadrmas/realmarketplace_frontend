package com.realmarketplace.ui.create.image

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.realmarketplace.databinding.AdapterCreateImageBinding
import com.realmarketplace.model.text.TextModelGlobal
import com.squareup.picasso.Picasso
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * A group of *adapter*.
 *
 * Class used as adapter for image files recycle view.
 *
 * @param images list of image file that is going to be displayed
 * @param urls list of string urls that already exists when the advert is going to be updated or deleted
 * @param uri image of camera with text add photo that is first item in recycle view
 * @param clickDelete function that delete specific advert
 * @param clickAdd function that open images selection when you click on first item in recycle view
 * @param life life cycle owner so each advert can listen on change
 */
class ImageAdapter(
    private var images:ArrayList<File>,
    private var urls:ArrayList<String>,
    private var uri:Uri,
    private var clickDelete: (File) -> Unit,
    private var clickAdd: (Boolean) -> Unit,
    private val life: LifecycleOwner
): RecyclerView.Adapter<ImageAdapter.ImageAdapterHolder>() {
    var checkFirst:MutableLiveData<File> = MutableLiveData()
    class ImageAdapterHolder(private val itemBinding: AdapterCreateImageBinding):RecyclerView.ViewHolder(itemBinding.root) {
        /**
         * A group of *adapter_function*.
         *
         * Function used to bind properties into specific items in recycle view.
         * Used Picasso module for displaying images by https url.
         *
         * @param curImage current file that is going to be displayed if uri and all urls are already displayed
         * @param urls list of string urls that already exists when the advert is going to be updated or deleted
         * @param uri image of camera with text add photo that is first item in recycle view
         * @param clickDelete function that delete specific advert
         * @param clickAdd function that open images selection when you click on first item in recycle view
         * @param checkFirst live data used as subscription for checking if advert is first to display cover advert water mark
         * @param life life cycle owner so each advert can listen on change
         */
        fun bind(curImage: File,urls:ArrayList<String>,uri:Uri, clickDelete: (File) -> Unit, clickAdd: (Boolean) -> Unit, position: Int,checkFirst:MutableLiveData<File>,life:LifecycleOwner) {
            itemBinding.imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            checkFirst.observe(life, Observer{
                if(curImage==it) itemBinding.coverImage.visibility = View.VISIBLE
                else itemBinding.coverImage.visibility = View.GONE
            })
            if(uri!=null&&position==0){
                itemBinding.imageView.setImageURI(uri)
                itemBinding.deleteText.visibility = View.GONE
                itemBinding.coverImage.visibility = View.GONE
                itemBinding.imageView.setOnClickListener(){
                    clickAdd(true)
                }
                itemBinding.imageView.setOnLongClickListener(){
                    clickAdd(true)
                    true
                }
            }
            else{
                if(urls.size+1>position){
                    println("${urls.size+1}>$position ${urls[position-1]}")
                    Picasso.get()
                        .load("${TextModelGlobal.REAL_MARKET_URL}/advert"+urls[position-1])
                        .into(itemBinding.imageView)
                }
                else{
                    val myBitmap = BitmapFactory.decodeFile(curImage.absolutePath)
                    itemBinding.imageView.setImageBitmap(myBitmap)
                }
                if(position ==1)itemBinding.coverImage.visibility = View.VISIBLE
                else itemBinding.coverImage.visibility = View.GONE
                itemBinding.deleteText.setOnClickListener() {
                    clickDelete(curImage)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageAdapterHolder {
        val imageBinding = AdapterCreateImageBinding.inflate( LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageAdapterHolder(
            imageBinding
        )
    }
    override fun getItemCount(): Int {
        return images.size
    }
    override fun onBindViewHolder(holder: ImageAdapterHolder, position: Int) {
        holder.bind(images[position],urls,uri,clickDelete,clickAdd,position,checkFirst,life)
    }
    /**
     * A group of *adapter_function*.
     *
     * Function used to add new image.
     *
     * @param image image file that is going to be added into image file list
     */
    fun addNewImage(image:File){
        images.add(image)
    }
    /**
     * A group of *adapter_function*.
     *
     * Function used to delete new image.
     *
     * @param image image file that is going to be deleted from image file list
     */
    fun removeNewImage(image:File){
        images.remove(image)
        if(urls.size>0) urls.remove(image.path)
        if(images.size>1)checkFirst.value=images[1]
    }
    /**
     * A group of *adapter_function*.
     *
     * Function used to change position of two adverts.
     *
     * @param from index of advert previous position
     * @param to index of advert new position
     */
    fun swap(from:Int,to:Int){
        Collections.swap(images,from,to)
        checkFirst.value=images[1]
    }
    /**
     * A group of *adapter_function*.
     *
     * Function used find out position of specific image file
     *
     * @param image specific image file which position am I interested in
     * @return index position of specific image
     */
    fun positionOfImage(image:File):Int{
        return images.indexOf(image)
    }
    /**
     * A group of *adapter_function*.
     *
     * Function used to change urls list of https urls
     *
     * @param newUrls list of urls strings
     */
    fun addUrls(newUrls:ArrayList<String>){
        urls=newUrls
    }
}