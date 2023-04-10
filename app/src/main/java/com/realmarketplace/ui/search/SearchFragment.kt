package com.realmarketplace.ui.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.realmarketplace.R
import com.realmarketplace.databinding.FragmentSearchBinding
import com.realmarketplace.model.AdvertModel
import com.realmarketplace.model.UserTokenAuth
import com.realmarketplace.ui.advert.AdvertActivity
import com.realmarketplace.ui.advert.AdvertViewModel
import com.realmarketplace.ui.auth.AuthViewModel
import com.realmarketplace.ui.auth.LogOutAuth
import com.realmarketplace.ui.favorite.FavoriteObject
import com.realmarketplace.ui.search.advert.AdvertAdapter


class SearchFragment : Fragment(){
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val createViewModel: SearchViewModel by activityViewModels()
    private lateinit var advertAdapter: AdvertAdapter
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
        createViewModel.advertModel.observe(viewLifecycleOwner, Observer {
            advertAdapter.updateAdvertList(it)
            binding.container.isRefreshing = false
            advertAdapter.notifyDataSetChanged()
        })
        context?.let { createViewModel.loadAllAdverts(it,
            AuthViewModel.userToken.value ?: UserTokenAuth("")
        ) }
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        var advertList = ArrayList<AdvertModel>()
        advertAdapter = AdvertAdapter(advertList, clickAdvert = {
            advert: AdvertModel ->openAdvert(advert)
        })
        binding.advertRecyclerView.adapter = advertAdapter
        binding.advertRecyclerView.layoutManager = LinearLayoutManager(
            FragmentActivity(),
            LinearLayoutManager.VERTICAL,false)
        binding.container.setOnRefreshListener {
            context?.let { createViewModel.loadAllAdverts(it,
                AuthViewModel.userToken.value ?: UserTokenAuth("")
            ) }
        }
        return binding.root
    }
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
                if(logOut!!) LogOutAuth.setLogOut(true)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}