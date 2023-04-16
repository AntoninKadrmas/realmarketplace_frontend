package com.realmarketplace.ui.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.realmarketplace.databinding.FragmentSearchBinding
import com.realmarketplace.model.AdvertModel
import com.realmarketplace.model.UserTokenAuth
import com.realmarketplace.ui.advert.AdvertActivity
import com.realmarketplace.ui.advert.AdvertViewModel
import com.realmarketplace.ui.auth.AuthViewModel
import com.realmarketplace.ui.auth.LogOutAuth
import com.realmarketplace.ui.favorite.FavoriteObject
import com.realmarketplace.ui.search.advert.AdvertAdapter
import com.realmarketplace.viewModel.LoadingBar

/**
 * A group of *fragment*.
 *
 * Class for fragment_search layout and logic there.
 */
class SearchFragment : Fragment(){
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var advertAdapter: AdvertAdapter
    private lateinit var searchView: SearchView
    companion object {
        fun newInstance() = SearchFragment()
        const val NAME = "Search"
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        AdvertViewModel.changeToolBarState(false)
        SearchViewModel.advertModelSample.observe(viewLifecycleOwner, Observer {
            if(binding.searchView.query.toString().isNullOrEmpty()) {
                advertAdapter.updateAdvertList(it)
                advertAdapter.notifyDataSetChanged()
            }
        })
        SearchViewModel.advertModelSearch.observe(viewLifecycleOwner, Observer {
            if(!binding.searchView.query.toString().isNullOrEmpty()) {
                advertAdapter.updateAdvertList(it)
                advertAdapter.notifyDataSetChanged()
            }
        })
        LoadingBar.mutableHideLoading.observe(viewLifecycleOwner, Observer {
            if(it){
                binding.container.isRefreshing = false
            }
        })
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        var advertList = ArrayList<AdvertModel>()
        advertAdapter = AdvertAdapter(advertList, clickAdvert = {
            advert: AdvertModel ->openAdvert(advert)
        },false)
        binding.advertRecyclerView.adapter = advertAdapter
        binding.advertRecyclerView.adapter = advertAdapter
        val layoutManager = LinearLayoutManager(
            FragmentActivity(),
            LinearLayoutManager.VERTICAL,false)
        binding.advertRecyclerView.layoutManager = layoutManager
        binding.container.setOnRefreshListener {
            if(binding.searchView.query.toString().isNullOrEmpty()){
                context?.let { SearchViewModel.loadAdvertSample(
                    AuthViewModel.userToken.value ?: UserTokenAuth(""),it
                ) }
            }else{
                context?.let {
                    SearchViewModel.loadAdvertSearch(binding.searchView.query.toString(),true,
                        AuthViewModel.userToken.value ?: UserTokenAuth(""),
                        it
                    )
                }
            }
        }
        searchView = binding.searchView
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object:OnQueryTextListener{
            override fun onQueryTextChange(text: String?): Boolean {
                if(text=="") {
                    if(SearchViewModel.advertModelSample.value!=null){
                        advertAdapter.updateAdvertList(SearchViewModel.advertModelSample.value!!)
                        advertAdapter.notifyDataSetChanged()
                    }
                }
                return true
            }

            override fun onQueryTextSubmit(text: String?): Boolean {
                binding.container.isRefreshing = true
                context?.let {
                    SearchViewModel.loadAdvertSearch(text!!,true,AuthViewModel.userToken.value ?: UserTokenAuth(""),
                        it
                    )
                }
                return true
            }
        })
        binding.advertRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    if (SearchViewModel.advertModelSearch.value != null && !binding.searchView.query.toString().isNullOrEmpty())
                        if (SearchViewModel.advertModelSearch.value!!.size < SearchViewModel.countAdvertSearch
                            && !binding.container.isRefreshing){
                            binding.container.isRefreshing = true
                            context?.let {
                                SearchViewModel.loadAdvertSearch(
                                    binding.searchView.query.toString(), false,
                                    AuthViewModel.userToken.value ?: UserTokenAuth(""),
                                    it
                                )
                            }
                        }
                }
            }
        })
        return binding.root
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used for start new activity with given advert content.
     * @param advert advert that going to be displayed in new activity viz. AdvertModel
     */
    private fun openAdvert(advert: AdvertModel){
        val intent = Intent(context, AdvertActivity::class.java)
        intent.putExtra("advertModel",advert)
        if(FavoriteObject.existsAdvertId(advert._id))intent.putExtra("favorite",true)
        else intent.putExtra("favorite",false)
        startActivityForResult(intent,10)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK) {
                val logOut: Boolean? = data?.getBooleanExtra("logOut",false)
                if(logOut!!) LogOutAuth.mutableLogOutMain.value= true
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}