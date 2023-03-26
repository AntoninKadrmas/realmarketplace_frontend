package com.example.omega1.ui.favorite

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omega1.ui.advert.AdvertActivity
import com.example.omega1.databinding.FragmentFavoriteBinding
import com.example.omega1.model.AdvertModel
import com.example.omega1.ui.advert.AdvertViewModel
import com.example.omega1.ui.advert.UpdateDeleteActivity
import com.example.omega1.ui.auth.AuthViewModel
import com.example.omega1.ui.create.crud.CrudAdvertViewModel
import com.example.omega1.ui.search.advert.AdvertAdapter
import com.example.omega1.viewModel.EnumViewData
import com.google.android.material.tabs.TabLayout

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val favoriteViewModel:FavoriteViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val advertViewModel: AdvertViewModel by activityViewModels()
    private val enumViewDataModel:EnumViewData by activityViewModels()
    private val crudAdvertViewModel:CrudAdvertViewModel by activityViewModels()
    private val binding get() = _binding!!
    private lateinit var advertAdapter:AdvertAdapter
    private lateinit var tabLayout:TabLayout
    private var myAdverts = ArrayList<AdvertModel>()
    private var favoriteAdverts = ArrayList<AdvertModel>()
    companion object {
        fun newInstance() = FavoriteFragment()
        const val NAME = "Favorite"
    }

    override fun onResume() {
        super.onResume()
        if(advertViewModel.myAdverts.value!=null)println(advertViewModel.myAdverts.value!!)
        if(advertViewModel.favoriteAdverts.value!=null)println(advertViewModel.favoriteAdverts.value!!)
        if(advertViewModel.myAdverts.value!=null)myAdverts=advertViewModel.myAdverts.value!!
        if(advertViewModel.favoriteAdverts.value!=null)favoriteAdverts=advertViewModel.favoriteAdverts.value!!
        favoriteViewModel.changeStatus(tabLayout.selectedTabPosition)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        advertViewModel.myAdverts.observe(viewLifecycleOwner, Observer {
            println(it)
            myAdverts = it
            advertAdapter.updateAdvertList(myAdverts)
            advertAdapter.notifyDataSetChanged()
        })
        advertViewModel.favoriteAdverts.observe(viewLifecycleOwner, Observer {
            println(it)
            favoriteAdverts = it
            advertAdapter.updateAdvertList(favoriteAdverts)
            advertAdapter.notifyDataSetChanged()
        })
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        tabLayout = binding.tabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Favorite Adverts"))
        tabLayout.addTab(tabLayout.newTab().setText("My Adverts"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                println("switch")
                favoriteViewModel.changeStatus(tab!!.position)
                if(tab!!.position==0){
                    println(favoriteAdverts)
                    advertAdapter.updateAdvertList(favoriteAdverts)
                }
                else{
                    println(myAdverts)
                    advertAdapter.updateAdvertList(myAdverts)
                }
                advertAdapter.notifyDataSetChanged()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
                favoriteViewModel.changeStatus(tab!!.position)
                advertAdapter.notifyDataSetChanged()
            }
        })
        advertAdapter = AdvertAdapter(ArrayList(), clickAdvert = {
                advert: AdvertModel ->openAdvert(advert)
        })
        binding.advertRecyclerView.adapter = advertAdapter
        binding.advertRecyclerView.layoutManager = LinearLayoutManager(
            FragmentActivity(),
            LinearLayoutManager.VERTICAL,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun openAdvert(advert:AdvertModel){
        var intent:Intent
        if(favoriteViewModel.favoriteTab.value==null||favoriteViewModel.favoriteTab.value==0){
            intent = Intent(context, AdvertActivity::class.java)
        } else {
            intent = Intent(context, UpdateDeleteActivity::class.java)
            intent.putExtra("priceEnum",enumViewDataModel.priceEnum.value!!)
            intent.putExtra("conditionEnum",enumViewDataModel.conditionEnum.value!!)
            intent.putExtra("genreGenreEnum",enumViewDataModel.genreGenreEnum.value!!)
        }
        intent.putExtra("advertModel",advert)
        intent.putExtra("token",authViewModel.userToken.value)
        startActivityForResult(intent,10)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK) {
                val logOut: Boolean? = data?.getBooleanExtra("logOut",false)
                val previousAdvert: String? = data?.getStringExtra("prevAdvertId")
                val newAdvert:AdvertModel? = data?.getSerializableExtra("newAdvert") as AdvertModel?
                val favorite:Boolean? = data?.getBooleanExtra("favorite",false)
                if(logOut!!){
                    crudAdvertViewModel.setLogOut(true)
                }
                else if(newAdvert==null){//delete
                    deleteAdvertById(previousAdvert!!)
                }
                else{//update
                    if (previousAdvert != null) {
                        if (favorite != null) {
                            updateAdvertById(previousAdvert,newAdvert,favorite)
                        }
                    }
                }
            }
        }
    }
    private fun deleteAdvertById(advertId:String){
        val position:Int =advertViewModel.myAdverts.value?.indexOf(advertViewModel.myAdverts.value!!.filter {
            it->it._id==advertId
        }[0])!!
        advertViewModel.removeMyAdvertByIndex(position)
        advertViewModel.removeNewFavoriteAdvert(advertViewModel.myAdverts.value!!.filter {
                it->it._id==advertId
        }[0])
        advertAdapter.updateAdvertList(advertViewModel.myAdverts.value!!)
        advertAdapter.notifyItemRemoved(position)
    }
    private fun updateAdvertById(previousAdvert:String,newAdvert:AdvertModel,favorite:Boolean){
        if(previousAdvert!=""){
            val previous = advertViewModel.myAdverts.value?.filter { it->
                it._id==previousAdvert
            }?.get(0)
            val position = advertViewModel.myAdverts.value?.indexOf(previous)
            if (position != null) {
                advertViewModel.myAdverts.value?.set(position, newAdvert)
                advertAdapter.notifyItemChanged(position)
            }
        }
        if(favorite)advertViewModel.addNewFavoriteAdvert(newAdvert)
        else advertViewModel.removeNewFavoriteAdvert(newAdvert)
    }
}