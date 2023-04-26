package com.realmarketplace.ui.advert.fullscreen

import android.R
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.realmarketplace.databinding.AdapterImageSlideNoSpaceBinding
import com.realmarketplace.model.text.TextModelGlobal
import com.squareup.picasso.Picasso


/**
 * A group of *adapter*.
 *
 * Class used as adapter for image view pager.
 *
 * @param imageUrls list of string urls of advert images
 * @param blockMoving function for disable or enable functionality of viewPager2
 */
class AdapterViewPagerFullScreen(
    private val imageUrls:ArrayList<String>,
    private val blockMoving:(Float)->Unit
):RecyclerView.Adapter<AdapterViewPagerFullScreen.AdvertViewHolder>() {
    class AdvertViewHolder(private val itemBinding:AdapterImageSlideNoSpaceBinding):RecyclerView.ViewHolder(itemBinding.root){
        /**
         * A group of *adapter_function*.
         *
         * Function used to bind properties into specific items in view pager.
         * Used Picasso module for displaying images by https url.
         *
         * @param curImageUrl url of actual image that would be displayed
         * @param blockMoving function for disable or enable functionality of viewPager2
         */
        fun bind(curImageUrl:String,blockMoving:(Float)->Unit){
            Picasso.get()
                .load("${TextModelGlobal.REAL_MARKET_URL}/advert$curImageUrl"+"?Admin=b326b5062b2f0e69046810717534cb09")
                .into(itemBinding.slideImageView)
            itemBinding.slideImageView.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    if(event?.action==MotionEvent.ACTION_UP&&itemBinding.slideImageView.currentZoom<=1)blockMoving(1f)
                    else blockMoving(itemBinding.slideImageView.currentZoom)
                    return true
                }
            })
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertViewHolder {
        val advertBinding:AdapterImageSlideNoSpaceBinding=AdapterImageSlideNoSpaceBinding.inflate(
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
        holder.bind(imageUrls[position],blockMoving)
    }
}