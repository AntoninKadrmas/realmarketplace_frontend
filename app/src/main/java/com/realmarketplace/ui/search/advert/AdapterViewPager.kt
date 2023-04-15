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
 */
class AdapterViewPager(
    private val imageUrls:ArrayList<String>
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
         */
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