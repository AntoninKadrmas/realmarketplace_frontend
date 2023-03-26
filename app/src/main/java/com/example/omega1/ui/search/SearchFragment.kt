package com.example.omega1.ui.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omega1.ui.advert.AdvertActivity
import com.example.omega1.databinding.FragmentSearchBinding
import com.example.omega1.model.AdvertModel
import com.example.omega1.ui.advert.AdvertViewModel
import com.example.omega1.ui.auth.AuthViewModel
import com.example.omega1.ui.search.advert.AdvertAdapter

class SearchFragment : Fragment(){
    private var _binding: FragmentSearchBinding? = null
    private val createViewModel: SearchViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val advertViewModel:AdvertViewModel by activityViewModels()

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
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        println("------------------------${result.resultCode}${Activity.RESULT_OK}")
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            val newAdvert:AdvertModel? = data?.getSerializableExtra("newAdvert") as AdvertModel?
            val favorite:Boolean? = data?.getBooleanExtra("favorite",false)
            if(favorite == true) newAdvert?.let { advertViewModel.addNewFavoriteAdvert(newAdvert) }
            else newAdvert?.let { advertViewModel.removeNewFavoriteAdvert(newAdvert) }
        }
    }
    private fun openAdvert(advert:AdvertModel){
        println(advert)
        val intent = Intent(context, AdvertActivity::class.java)
        println(authViewModel.userToken.value)
        intent.putExtra("advertModel",advert)
        intent.putExtra("token",authViewModel.userToken.value)
        resultLauncher.launch(intent)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 10) {
//            if (resultCode == Activity.RESULT_OK) {
//
//            }
//        }
//    }
}