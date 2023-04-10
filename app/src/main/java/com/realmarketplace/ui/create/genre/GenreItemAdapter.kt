package com.realmarketplace.ui.create.genre

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.realmarketplace.databinding.AdapterGenreListItemBinding
import com.realmarketplace.rest.GenreItem

class GenreItemAdapter(
    private var genreList: ArrayList<GenreItem>,
    private var selectGenre:(GenreItem)->Unit
):RecyclerView.Adapter<GenreItemAdapter.GenreItemHolder>() {
    class GenreItemHolder(private val itemBinding: AdapterGenreListItemBinding):RecyclerView.ViewHolder(itemBinding.root) {
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
    fun updateGenreList(list:ArrayList<GenreItem>){
        genreList = list
        this.notifyDataSetChanged()
    }
}