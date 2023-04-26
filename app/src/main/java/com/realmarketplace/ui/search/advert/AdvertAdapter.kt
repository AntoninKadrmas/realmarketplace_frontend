package com.realmarketplace.ui.search.advert

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.RecyclerView
import com.realmarketplace.R
import com.realmarketplace.databinding.AdapterAdvertBinding
import com.realmarketplace.model.AdvertModel
import com.realmarketplace.model.text.TextModelGlobal
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

/**
 * A group of *adapter*.
 *
 * Class used as adapter for advert recycle view.
 *
 * @param advertList list of advert that would be show there viz. AdvertModel
 * @param clickAdvert function that will show advert information in new activity
 * @param doReverse boolean variable that load adverts in reversed order it true else normally
 */
class AdvertAdapter (
    private var advertList:ArrayList<AdvertModel>,
    private var clickAdvert:(AdvertModel)->Unit,
    private var doReverse:Boolean=true
    ):RecyclerView.Adapter<AdvertAdapter.AdvertViewHolder>() {
    init {
        reverseCollection()
    }
    companion object {
        /**
         * A group of *adapter_function*.
         *
         * Function used to format date string into human readable date.
         *
         * @param dateString complicated given date string in pattern yyyy-MM-dd'T'HH:mm:ss
         * @return formatted date in pattern dd.MM.yyyy
         */
        fun formatDate(dateString:String): String {
            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val date: Date =
                dateFormat.parse(dateString)
            val formatter: DateFormat =
                SimpleDateFormat("dd.MM.yyyy")
            return formatter.format(date)
        }
        /**
         * A group of *adapter_function*.
         *
         * Function used to convert price into check crowns.
         *
         * @param price string price value if it contains digit after price would be added currency string
         * @return formatted price string
         */
        fun convertPrice(price:String):String{
            if(price.isDigitsOnly()){
                return "$price kč"
            }
            return price
        }
    }
    class AdvertViewHolder(private val itemBinding:AdapterAdvertBinding):RecyclerView.ViewHolder(itemBinding.root){
        /**
         * A group of *adapter_function*.
         *
         * Function used to bind properties into specific items in recycle view.
         * Used Picasso module for displaying images by https url.
         *
         * @param curAdvert advert that which information's would be displayed in item of recycle view.
         * @param clickAdvert function that will show advert information in new activity
         * @param position position of set item in recycle view
         */
        fun bind(curAdvert: AdvertModel, clickAdvert:(AdvertModel)->Unit, position:Int){
            itemBinding.imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            Picasso.get().load("${TextModelGlobal.REAL_MARKET_URL}/advert"+curAdvert.mainImageUrl+"?Admin=b326b5062b2f0e69046810717534cb09")
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
    /**
     * A group of *adapter_function*.
     *
     * Function used to set advert list to a given one viz. AdvertModel.
     * @param newList list that would rewrite actual advert list
     */
    fun updateAdvertList(newList:ArrayList<AdvertModel>){
        advertList = newList
        reverseCollection()
    }
    /**
     * A group of *adapter_function*.
     *
     * Function used to reverse position of elements in advertList.
     */
    private fun reverseCollection(){
        if(doReverse){
            var tempAdvertList = ArrayList<AdvertModel>()
            for(advert in advertList.reversed()){
                tempAdvertList.add(advert)
            }
            advertList=tempAdvertList
        }
    }
}