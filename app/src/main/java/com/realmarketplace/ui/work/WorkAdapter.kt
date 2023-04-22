package com.realmarketplace.ui.work

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.RecyclerView
import com.realmarketplace.R
import com.realmarketplace.databinding.AdapterAdvertBinding
import com.realmarketplace.databinding.AdapterWorkInProgressBinding
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
class WorkAdapter(
    private var advertList:ArrayList<Int>
):RecyclerView.Adapter<WorkAdapter.AdvertViewHolder>() {
    class AdvertViewHolder(private val itemBinding:AdapterWorkInProgressBinding):RecyclerView.ViewHolder(itemBinding.root){
        fun bind(){

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertViewHolder {
        val advertAdapter:AdapterWorkInProgressBinding=AdapterWorkInProgressBinding.inflate(
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
        holder.bind()
    }
}