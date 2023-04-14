package com.realmarketplace.ui.search.advert

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.realmarketplace.databinding.AdapterImageSlideBinding
import com.realmarketplace.model.text.TextModelGlobal
import com.squareup.picasso.Picasso

class AdapterViewPager(
    private val imageUrls:ArrayList<String>
):RecyclerView.Adapter<AdapterViewPager.AdvertViewHolder>() {
    class AdvertViewHolder(private val itemBinding:AdapterImageSlideBinding):RecyclerView.ViewHolder(itemBinding.root){
        fun bind(curImageUrl:String,position: Int){
            Picasso
                .get()
                .load("${TextModelGlobal.REAL_MARKET_URL}/advert$curImageUrl")
                .into(itemBinding.slideImageView)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertViewHolder {
        val advertBinding:AdapterImageSlideBinding=AdapterImageSlideBinding.inflate(
            LayoutInflater.from(parent.context),
                parent,
                false
        )
        return AdvertViewHolder(
            advertBinding
        )
    }
    override fun getItemCount(): Int {
        return imageUrls.size
    }
    override fun onBindViewHolder(holder: AdvertViewHolder, position: Int) {
        holder.bind(imageUrls[position],position)
    }
}