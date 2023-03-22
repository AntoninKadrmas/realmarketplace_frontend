package com.example.omega1.ui.create.image

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.omega1.R
import kotlinx.android.synthetic.main.adapter_create_image.view.*
import java.io.File


class ImageAdapter(
    private var images:MutableList<File>,
    private var clickDelete: (File) -> Unit,
    private var clickAdd: (File) -> Unit
): RecyclerView.Adapter<ImageAdapter.ImageAdapterHolder>() {
    class ImageAdapterHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        fun bind(curImage: File, clickDelete: (File) -> Unit, clickAdd: (File) -> Unit, position: Int) {
            val myBitmap = BitmapFactory.decodeFile(curImage.absolutePath)
            itemView.image_view.setImageBitmap(myBitmap)
            itemView.image_view.scaleType = ImageView.ScaleType.CENTER_CROP
            if(position ==0){
                itemView.delete_text.visibility = View.GONE
                itemView.cover_image.visibility = View.GONE
                itemView.image_view.setOnClickListener(){
                    clickAdd(curImage)
                }
            }
            else{
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
        holder.bind(images[position],clickDelete,clickAdd,position)
    }
    fun addNewImage(image:File){
        images.add(image)
    }
    fun removeNewImage(image:File){
        images.remove(image)
    }
    fun positionOfImage(image:File):Int{
        return images.indexOf(image)
    }
}