package com.example.omega1.ui.search.advert

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.omega1.R
import com.example.omega1.model.AdvertModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.adapter_advert.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

class AdvertAdapter(
    private var advertList:ArrayList<AdvertModel>,
    private var clickAdvert:(AdvertModel)->Unit
    ):RecyclerView.Adapter<AdvertAdapter.AdvertViewHolder>() {
    companion object {
        fun formatDate(dateString:String): String {
            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val date: Date =
                dateFormat.parse(dateString)
            val formatter: DateFormat =
                SimpleDateFormat("dd.MM.yyyy")
            return formatter.format(date)
        }
    }
    class AdvertViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        fun bind(curAdvert:AdvertModel,clickAdvert:(AdvertModel)->Unit,position:Int){
            itemView.image_view.scaleType = ImageView.ScaleType.CENTER_CROP
            Picasso.get().load("https://www.realmarketplace.shop/advert"+curAdvert.mainImage).into(itemView.image_view)
            itemView.advert_title_text.text = curAdvert.title
            itemView.advert_description_text.text = curAdvert.description
            itemView.advert_author_text.text = curAdvert.author
            itemView.advert_genre_text.text = "${curAdvert.genreName}/${curAdvert.genreType}"
            itemView.advert_condition_text.text = curAdvert.condition
            itemView.advert_price_text.text = curAdvert.price
            itemView.advert_create_in.text = formatDate(curAdvert.createdIn)
            itemView.whole_advert_card.setOnClickListener(){
                clickAdvert(curAdvert)
            }
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
        holder.bind(advertList[position],clickAdvert,position)
    }
    fun updateAdvertList(new_list:ArrayList<AdvertModel>){
        advertList = new_list
    }
}