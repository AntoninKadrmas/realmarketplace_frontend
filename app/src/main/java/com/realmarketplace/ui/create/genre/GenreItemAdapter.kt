package com.realmarketplace.ui.create.genre

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.realmarketplace.databinding.AdapterGenreListItemBinding
import com.realmarketplace.rest.GenreItem

/**
 * A group of *adapter*.
 *
 * Class used as adapter for genres recycle view.
 * @param genreList list of genre items viz. GenreItem
 * @param selectGenre function used to identify which genre was selected
 */
class GenreItemAdapter(
    private var genreList: ArrayList<GenreItem>,
    private var selectGenre:(GenreItem)->Unit
):RecyclerView.Adapter<GenreItemAdapter.GenreItemHolder>() {
    class GenreItemHolder(private val itemBinding: AdapterGenreListItemBinding):RecyclerView.ViewHolder(itemBinding.root) {
        /**
         * A group of *adapter_function*.
         *
         * Function used to bind properties into specific items in recycle view.
         *
         * @param currentGenre current genre used to set item values
         * @param selectGenre function used to identify which genre was selected
         */
        fun bind(currentGenre: GenreItem, selectGenre:(GenreItem)->Unit){
            itemBinding.genreNameInput.text = currentGenre.name
            itemBinding.genreTypeInput.text = currentGenre.type
            itemBinding.genreLayout.setOnClickListener(){
                selectGenre(currentGenre)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreItemHolder {
        val binding = AdapterGenreListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GenreItemHolder(binding)
    }
    override fun getItemCount(): Int {
        return genreList.size
    }
    override fun onBindViewHolder(holder: GenreItemHolder, position: Int) {
        holder.bind(genreList[position],selectGenre)
    }
    /**
     * A group of *adapter_function*.
     *
     * Function used to update genre list.
     *
     * @param list new list of genres that would be used as default viz. GenreItem
     */
    fun updateGenreList(list:ArrayList<GenreItem>){
        genreList = list
        this.notifyDataSetChanged()
    }
}