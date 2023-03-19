package com.example.omega1.ui.create.image

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.omega1.R
import kotlinx.android.synthetic.main.adapter_create_image.view.*

class ImageAdapter(
    private var images:MutableList<Uri>,
    private var clickDelete: (Uri) -> Unit,
    private var clickAdd: (Uri) -> Unit
): RecyclerView.Adapter<ImageAdapter.ImageAdapterHolder>() {
    class ImageAdapterHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        fun bind(curImage: Uri, clickDelete: (Uri) -> Unit, clickAdd: (Uri) -> Unit, position: Int) {
            itemView.image_view.setImageURI(curImage)
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
    fun addNewImage(image:Uri){
        images.add(image)
    }
    fun removeNewImage(image:Uri){
        images.remove(image)
    }
    fun positionOfImage(image:Uri):Int{
        return images.indexOf(image)
    }
}