package com.realmarketplace.ui.search.advert

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.RecyclerView
import com.realmarketplace.R
import com.realmarketplace.databinding.AdapterAdvertBinding
import com.realmarketplace.model.AdvertModel
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

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
        fun convertPrice(price:String):String{
            if(price.isDigitsOnly()){
                return "$price kč"
            }
            return price
        }
    }
    class AdvertViewHolder(private val itemBinding:AdapterAdvertBinding):RecyclerView.ViewHolder(itemBinding.root){
        fun bind(curAdvert: AdvertModel, clickAdvert:(AdvertModel)->Unit, position:Int){
            println(curAdvert)
            itemBinding.imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            Picasso.get().load("https://www.realmarketplace.shop/advert"+curAdvert.mainImageUrl)
                .placeholder(R.drawable.ic_baseline_image_not)
                .into(itemBinding.imageView)
            itemBinding.advertTitleText.text = curAdvert.title
            itemBinding.advertDescriptionText.text = curAdvert.description
            itemBinding.advertAuthorText.text = curAdvert.author
            itemBinding.advertGenreText.text = "${curAdvert.genreName}/${curAdvert.genreType}"
            itemBinding.advertConditionText.text = curAdvert.condition
            itemBinding.advertPriceText.text = convertPrice(curAdvert.price)
            itemBinding.advertCreateIn.text = formatDate(curAdvert.createdIn.toString())
            itemBinding.wholeAdvertCard.setOnClickListener(){
                clickAdvert(curAdvert)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertViewHolder {
        val advertAdapter:AdapterAdvertBinding=AdapterAdvertBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
       return AdvertViewHolder(
           advertAdapter
       )
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