package com.example.omega1.ui.search.advert

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.omega1.R
import com.example.omega1.model.AdvertModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.adapter_advert.view.*
import kotlinx.android.synthetic.main.adapter_advert.view.image_view

class AdvertAdapter(private var advertList:ArrayList<AdvertModel>):RecyclerView.Adapter<AdvertAdapter.AdvertViewHolder>() {
    class AdvertViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        fun bind(curAdvert:AdvertModel,position:Int){
            itemView.image_view.scaleType = ImageView.ScaleType.CENTER_CROP
            Picasso.get().load("https://www.realmarketplace.shop"+curAdvert.mainImage).into(itemView.image_view)
            itemView.advert_title_text.text = curAdvert.title
            itemView.advert_description_text.text = curAdvert.description
            itemView.advert_genre_text.text = "${curAdvert.genreName}/${curAdvert.genreType}"
            itemView.advert_condition_text.text = curAdvert.condition
            itemView.advert_price_text.text = curAdvert.price
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertViewHolder {
       return AdvertViewHolder(LayoutInflater.from(parent.context).inflate(
           R.layout.adapter_advert,
           parent,
           false
       ))
    }

    override fun getItemCount(): Int {
        return advertList.size
    }

    override fun onBindViewHolder(holder: AdvertViewHolder, position: Int) {
        holder.bind(advertList[position],position)
    }
    fun updateAdvertList(new_list:ArrayList<AdvertModel>){
        advertList = new_list
    }
}