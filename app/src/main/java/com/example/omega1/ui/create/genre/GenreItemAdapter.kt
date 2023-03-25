package com.example.omega1.ui.create.genre

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.omega1.databinding.AdapterGenreListItemBinding
import com.example.omega1.rest.GenreItem
import kotlinx.android.synthetic.main.adapter_genre_list_item.view.*

class GenreItemAdapter(
    private var genreList: ArrayList<GenreItem>,
    private var selectGenre:(GenreItem)->Unit
):RecyclerView.Adapter<GenreItemAdapter.GenreItemHolder>() {
    class GenreItemHolder(itemView: AdapterGenreListItemBinding):RecyclerView.ViewHolder(itemView.root) {
        fun bind(currentGenre:GenreItem,selectGenre:(GenreItem)->Unit){
            itemView.genre_name_input.text = currentGenre.name
            itemView.genre_type_input.text = currentGenre.type
            itemView.genre_layout.setOnClickListener(){
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