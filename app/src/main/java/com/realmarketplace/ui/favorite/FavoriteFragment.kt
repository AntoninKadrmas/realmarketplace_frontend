package com.realmarketplace.ui.favorite

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
import com.realmarketplace.ui.advert.AdvertActivity
import com.realmarketplace.databinding.FragmentFavoriteBinding
import com.realmarketplace.model.AdvertModel
import com.realmarketplace.ui.advert.AdvertViewModel
import com.realmarketplace.ui.advert.UpdateDeleteActivity
import com.realmarketplace.ui.auth.AuthViewModel
import com.realmarketplace.ui.auth.LogOutAuth
import com.realmarketplace.ui.search.advert.AdvertAdapter
import com.realmarketplace.viewModel.EnumViewData
import com.realmarketplace.viewModel.PermissionViewModel
import com.google.android.material.tabs.TabLayout

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val advertViewModel= AdvertViewModel
    private val enumViewDataModel: EnumViewData by activityViewModels()
    private val permissionViewModel: PermissionViewModel by activityViewModels()
    private val binding get() = _binding!!
    private lateinit var advertAdapter: AdvertAdapter
    private lateinit var tabLayout:TabLayout
    private var myAdverts = ArrayList<AdvertModel>()
    private var favoriteAdverts = ArrayList<AdvertModel>()
    companion object {
        fun newInstance() = FavoriteFragment()
        const val NAME = "Favorite"
    }

    override fun onResume() {
        super.onResume()
        if(advertViewModel.myAdverts.value!=null)myAdverts=advertViewModel.myAdverts.value!!
        if(FavoriteViewModel.favoriteAdverts.value!=null)favoriteAdverts=
            FavoriteViewModel.favoriteAdverts.value!!
        FavoriteViewModel.changeStatus(tabLayout.selectedTabPosition)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        context?.let {
            FavoriteViewModel.loadAllFavoriteAdverts(
                AuthViewModel.userToken.value!!,
                it
            )
        }
        advertViewModel.myAdverts.observe(viewLifecycleOwner, Observer {
            myAdverts = it
            if(tabLayout.selectedTabPosition==1){
                advertAdapter.updateAdvertList(myAdverts)
                if(myAdverts.size==0) binding.noAdverts.text = "You have 0 created adverts yet."
                advertAdapter.notifyDataSetChanged()

            }
        })
        FavoriteViewModel.favoriteAdverts.observe(viewLifecycleOwner, Observer {
            favoriteAdverts = it
            if(tabLayout.selectedTabPosition==0){
                advertAdapter.updateAdvertList(favoriteAdverts)
                if(favoriteAdverts.size==0) binding.noAdverts.text = "You have 0 favorite adverts yet."
                advertAdapter.notifyDataSetChanged()
            }

        })
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        tabLayout = binding.tabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Favorite Adverts"))
        tabLayout.addTab(tabLayout.newTab().setText("My Adverts"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        binding.noAdverts.text = "You have 0 favorite adverts yet."
        tabLayout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                FavoriteViewModel.changeStatus(tab!!.position)
                if(tab!!.position==0){
                    if(favoriteAdverts.size==0) binding.noAdverts.text = "You have 0 favorite adverts yet."
                    else binding.noAdverts.text = ""
                    advertAdapter.updateAdvertList(favoriteAdverts)
                    advertAdapter.notifyDataSetChanged()
                    context?.let {
                        FavoriteViewModel.loadAllFavoriteAdverts(
                            AuthViewModel.userToken.value!!,
                            it
                        )
                    }
                }
                else{
                    if(myAdverts.size==0) binding.noAdverts.text = "You have 0 created adverts yet."
                    else binding.noAdverts.text = ""
                    advertAdapter.updateAdvertList(myAdverts)
                    advertAdapter.notifyDataSetChanged()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
                FavoriteViewModel.changeStatus(tab!!.position)
                advertAdapter.notifyDataSetChanged()
            }
        })
        advertAdapter = AdvertAdapter(ArrayList(), clickAdvert = {
                advert: AdvertModel ->openAdvert(advert)
        },true)
        binding.advertRecyclerView.adapter = advertAdapter
        binding.advertRecyclerView.adapter = advertAdapter
        val layoutManager = LinearLayoutManager(
            FragmentActivity(),
            LinearLayoutManager.VERTICAL,false)
        binding.advertRecyclerView.layoutManager = layoutManager
        AdvertViewModel.changeToolBarState(true)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun openAdvert(advert: AdvertModel){
        var intent:Intent
        if(FavoriteViewModel.favoriteTab.value==null|| FavoriteViewModel.favoriteTab.value==0){
            intent = Intent(context, AdvertActivity::class.java)
        } else {
            intent = Intent(context, UpdateDeleteActivity::class.java)
            intent.putExtra("permissionStorage",permissionViewModel.permissionStorage?.value ?:false)
            intent.putExtra("priceEnum",enumViewDataModel.priceEnum.value!!)
            intent.putExtra("conditionEnum",enumViewDataModel.conditionEnum.value!!)
            intent.putExtra("genreGenreEnum",enumViewDataModel.genreGenreEnum.value!!)
        }
        if(FavoriteObject.existsAdvertId(advert._id))intent.putExtra("favorite",true)
        else intent.putExtra("favorite",false)
        intent.putExtra("advertModel",advert)
        startActivityForResult(intent,10)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK) {
                val logOut: Boolean? = data?.getBooleanExtra("logOut",false)
                val previousAdvert: String? = data?.getStringExtra("prevAdvertId")
                val newAdvert: AdvertModel? = data?.getSerializableExtra("newAdvert") as AdvertModel?
                if(logOut!!){
                    LogOutAuth.setLogOut(true)
                }
                else if(newAdvert==null){//delete
                    deleteAdvertById(previousAdvert!!)
                }
                else if(newAdvert!=null){//update
                    if (previousAdvert != null) {
                            updateAdvertById(previousAdvert,newAdvert)
                    }
                }
            }
        }
    }
    private fun deleteAdvertById(advertId:String){
        val position:Int =advertViewModel.myAdverts.value?.indexOf(advertViewModel.myAdverts.value!!.filter {
                it->it._id==advertId
        }[0])!!
        advertViewModel.removeNewMyAdvert(position)
        if(FavoriteObject.existsAdvertId(advertId)) FavoriteObject.removeAdvertId(advertId)
        advertAdapter.notifyItemRemoved(advertAdapter.itemCount-position)
    }
    private fun updateAdvertById(previousAdvert:String,newAdvert: AdvertModel){
        println("$previousAdvert $newAdvert")
        val position = advertViewModel.updateNewMyAdvert(previousAdvert,newAdvert)
        advertAdapter.updateAdvertList(advertViewModel.myAdverts.value!!)
        advertAdapter.notifyItemChanged(advertAdapter.itemCount-position)
    }
}