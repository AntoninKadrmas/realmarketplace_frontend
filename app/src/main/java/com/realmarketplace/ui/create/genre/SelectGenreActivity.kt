package com.realmarketplace.ui.create.genre

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.realmarketplace.R
import com.realmarketplace.databinding.ActivitySelectGenreBinding
import com.realmarketplace.rest.GenreItem

/**
 * A group of *activity*.
 *
 * Class used as genre selector when someone want to create or update advert.
 */
class SelectGenreActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectGenreBinding
    private lateinit var genreItemAdapter: GenreItemAdapter
    private lateinit var searchView: SearchView
    private var genreListItems= ArrayList<GenreItem>()
    private var fictionFilter = true
    private var nonFictionFilter = true
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_genre)
        val extrasValues = intent.extras
        binding = ActivitySelectGenreBinding.inflate(layoutInflater)
        searchView = binding.searchView
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object:OnQueryTextListener{
            override fun onQueryTextChange(text: String?): Boolean {
                filterList(text!!)
                return true
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
               return false
            }
        })
        setContentView(binding.root)
        if (extrasValues != null) {
            for(item in extrasValues.getStringArrayList("genreArray")!!){
                val tempItem = item.split('|')
                genreListItems.add(GenreItem(tempItem[0],tempItem[1]))
            }
        }
        genreItemAdapter = GenreItemAdapter(genreListItems,selectGenre = {
                selectGenre: GenreItem ->returnGenre(selectGenre)
        })
        binding.recyclerView.adapter = genreItemAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.filterFictionButton.setOnClickListener(){
            fictionFilter=!fictionFilter
            setSubtitle(fictionFilter,nonFictionFilter)
            filterList(searchView.query.toString())
        }
        binding.filterNonfictionButton.setOnClickListener(){
            nonFictionFilter=!nonFictionFilter
            setSubtitle(fictionFilter,nonFictionFilter)
            filterList(searchView.query.toString())
        }
        binding.myToolbar.setNavigationOnClickListener(){
            finish()
        }
    }
    /**
     * A group of *activity_function*.
     *
     * Function used to change subtitle of toolbar.
     * @param fiction boolean variable decide if fiction would be in the subtitle
     * @param nonFiction boolean variable decide if nonfiction would be in the subtitle
     */
    private fun setSubtitle(fiction:Boolean, nonFiction:Boolean){
        if(fiction && nonFiction)binding.myToolbar.subtitle="Fiction or NonFiction"
        else if(fiction && !nonFiction)binding.myToolbar.subtitle="Fiction"
        else if(!fiction && nonFiction)binding.myToolbar.subtitle="NonFiction"
        else binding.myToolbar.subtitle=""
    }
    /**
     * A group of *activity_function*.
     *
     * Function used to filter genres in recycle view by given text.
     * @param text string text that is used as filter in regex
     */
    private fun filterList(text:String){
        if(text=="")genreItemAdapter.updateGenreList(genreListItems)
        val filterGenreListItem = ArrayList<GenreItem>()
        for(item in genreListItems){
            if(item.name.lowercase().matches(".*${text.lowercase()}.*".toRegex())&&
                    ((item.type==getString(R.string.genre_type_fiction)&&fictionFilter)||
                    (item.type==getString(R.string.genre_type_non_fiction)&&nonFictionFilter)))
                filterGenreListItem.add(item)
        }
        if(filterGenreListItem!=genreListItems){
            genreItemAdapter.updateGenreList(filterGenreListItem)
        }
    }
    /**
     * A group of *activity_function*.
     *
     * Function used to return selected genre as result of intent.
     * @param genre genre selected by user viz. GenreItem
     */
    private fun returnGenre(genre: GenreItem){
        val data = Intent()
        data.putExtra("name", genre.name)
        data.putExtra("type",genre.type)
        setResult(RESULT_OK, data)
        finish()
    }
}