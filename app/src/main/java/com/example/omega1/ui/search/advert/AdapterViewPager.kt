package com.example.omega1.ui.search.advert

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.omega1.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.adapter_image_slide.view.*

class AdapterViewPager(
    private val imageUrls:ArrayList<String>
):RecyclerView.Adapter<AdapterViewPager.AdvertViewHolder>() {
    class AdvertViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        fun bind(curImageUrl:String,position: Int){
            Picasso
                .get()
                .load("https://www.realmarketplace.shop/advert$curImageUrl")
                .into(itemView.slide_image_view)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertViewHolder {
        return AdvertViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_image_slide,
            parent,
            false
        ))
    }
    override fun getItemCount(): Int {
        return imageUrls.size
    }
    override fun onBindViewHolder(holder: AdvertViewHolder, position: Int) {
        holder.bind(imageUrls[position],position)
    }
}