package com.example.omega1.ui.create.image

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.omega1.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.adapter_create_image.view.*
import java.io.File


class ImageAdapter(
    private var images:ArrayList<File>,
    private var urls:ArrayList<String>,
    private var uri:Uri,
    private var clickDelete: (File) -> Unit,
    private var clickAdd: (File) -> Unit
): RecyclerView.Adapter<ImageAdapter.ImageAdapterHolder>() {
    class ImageAdapterHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        fun bind(curImage: File,urls:ArrayList<String>,uri:Uri, clickDelete: (File) -> Unit, clickAdd: (File) -> Unit, position: Int) {
            itemView.image_view.scaleType = ImageView.ScaleType.CENTER_CROP
            if(uri!=null&&position==0){
                itemView.image_view.setImageURI(uri)
                itemView.delete_text.visibility = View.GONE
                itemView.cover_image.visibility = View.GONE
                itemView.image_view.setOnClickListener(){
                    clickAdd(curImage)
                }
            }
            else{
                println(urls.size)
                if(urls.size+1>position){
                    println("${urls.size+1}>$position ${urls[position-1]}")
                    Picasso.get()
                        .load("https://www.realmarketplace.shop/advert"+urls[position-1])
                        .into(itemView.image_view)
                }
                else{
                    val myBitmap = BitmapFactory.decodeFile(curImage.absolutePath)
                    itemView.image_view.setImageBitmap(myBitmap)
                }
                if(position ==1)itemView.cover_image.visibility = View.VISIBLE
                else itemView.cover_image.visibility = View.GONE
                itemView.delete_text.setOnClickListener() {
                    clickDelete(curImage)
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageAdapterHolder {
        return ImageAdapterHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_create_image,
                parent,
                false
            )
        )
    }
    override fun getItemCount(): Int {
        return images.size
    }
    override fun onBindViewHolder(holder: ImageAdapterHolder, position: Int) {
        holder.bind(images[position],urls,uri,clickDelete,clickAdd,position)
    }
    fun addNewImage(image:File){
        images.add(image)
    }
    fun removeNewImage(image:File){
        images.remove(image)
        if(urls.size>0){
            urls.remove(image.path)
        }
    }
    fun positionOfImage(image:File):Int{
        return images.indexOf(image)
    }
    fun addUrls(new_urls:ArrayList<String>){
        urls=new_urls
    }
}