package com.realmarketplace.ui.search.advert

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.realmarketplace.databinding.AdapterImageSlideBinding
import com.realmarketplace.model.text.TextModelGlobal
import com.squareup.picasso.Picasso

/**
 * A group of *adapter*.
 *
 * Class used as adapter for image view pager.
 *
 * @param imageUrls list of string urls of advert images
 * @param showImage function to show image in fullscreen by showFullScreenActivity
 */
class AdapterViewPager(
    private val imageUrls:ArrayList<String>,
    private val showImage:(Int)->Unit
):RecyclerView.Adapter<AdapterViewPager.AdvertViewHolder>() {
    class AdvertViewHolder(private val itemBinding:AdapterImageSlideBinding):RecyclerView.ViewHolder(itemBinding.root){
        /**
         * A group of *adapter_function*.
         *
         * Function used to bind properties into specific items in view pager.
         * Used Picasso module for displaying images by https url.
         *
         * @param curImageUrl url of actual image that would be displayed
         * @param position position of actual item
         * @param showImage function to show image in fullscreen by showFullScreenActivity
         */
        fun bind(curImageUrl:String,position: Int,showImage:(Int)->Unit){
            Picasso.get()
                .load("${TextModelGlobal.REAL_MARKET_URL}/advert$curImageUrl"+"?Admin=b326b5062b2f0e69046810717534cb09")
                .into(itemBinding.slideImageView)
            itemBinding.slideImageView.setOnClickListener(){
                showImage(position)
            }
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
        holder.bind(imageUrls[position],position,showImage)
    }
}