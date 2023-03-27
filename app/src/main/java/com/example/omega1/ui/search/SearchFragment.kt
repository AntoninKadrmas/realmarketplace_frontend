package com.example.omega1.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omega1.ui.advert.AdvertActivity
import com.example.omega1.databinding.FragmentSearchBinding
import com.example.omega1.model.AdvertModel
import com.example.omega1.ui.advert.AdvertViewModel
import com.example.omega1.ui.advert.favoriteObject
import com.example.omega1.ui.auth.AuthViewModel
import com.example.omega1.ui.search.advert.AdvertAdapter

class SearchFragment : Fragment(){
    private var _binding: FragmentSearchBinding? = null
    private val createViewModel: SearchViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val advertViewModel= AdvertViewModel

    private lateinit var advertAdapter:AdvertAdapter
    private val binding get() = _binding!!
    companion object {
        fun newInstance() = SearchFragment()
        const val NAME = "Search"
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        createViewModel.advertModel.observe(viewLifecycleOwner, Observer {
            println(it.size)
            advertAdapter.updateAdvertList(it)
            advertAdapter.notifyDataSetChanged()
        })
        context?.let { createViewModel.loadAllAdverts(it) }
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        var advertList = ArrayList<AdvertModel>()
        advertAdapter = AdvertAdapter(advertList, clickAdvert = {
            advert:AdvertModel->openAdvert(advert)
        })
        binding.advertRecyclerView.adapter = advertAdapter
        binding.advertRecyclerView.layoutManager = LinearLayoutManager(
            FragmentActivity(),
            LinearLayoutManager.VERTICAL,false)
        return binding.root
    }
    private fun openAdvert(advert:AdvertModel){
        val intent = Intent(context, AdvertActivity::class.java)
        intent.putExtra("advertModel",advert)
        if(favoriteObject.existsAdvertId(advert._id)!=-1)intent.putExtra("favorite",true)
        else intent.putExtra("favorite",false)
        intent.putExtra("token",authViewModel.userToken.value)
        startActivityForResult(intent,10)

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}