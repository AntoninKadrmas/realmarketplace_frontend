package com.realmarketplace.ui.user

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import com.realmarketplace.R
import com.realmarketplace.databinding.FragmentPublicUserBinding
import com.realmarketplace.model.AdvertModel
import com.realmarketplace.model.LightUser
import com.realmarketplace.ui.advert.AdvertActivity
import com.realmarketplace.ui.auth.AuthViewModel
import com.realmarketplace.ui.favorite.FavoriteObject
import com.realmarketplace.ui.search.advert.AdvertAdapter
import com.squareup.picasso.Picasso

class PublicUserFragment : Fragment() {
    private var _binding: FragmentPublicUserBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var advertAdapter: AdvertAdapter
    private lateinit var user: LightUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPublicUserBinding.inflate(inflater, container, false)
        user = userViewModel.user.value!!
        AuthViewModel.userToken.value?.let {
            context?.let { it1 ->
                userViewModel.loadAllUserPublicAdverts(user,
                    it, it1
                )
            }
        }
        advertAdapter = AdvertAdapter(ArrayList(), clickAdvert = {
                advert: AdvertModel ->openAdvert(advert)
        })
        binding.userAdverts.adapter = advertAdapter
        binding.userAdverts.layoutManager = LinearLayoutManager(
            FragmentActivity(),
            LinearLayoutManager.VERTICAL,false)
        if(userViewModel.userAdverts.value!=null){
            advertAdapter.updateAdvertList(userViewModel.userAdverts.value!!)
            advertAdapter.notifyDataSetChanged()
            binding.userAdvertsLayout.visibility = View.VISIBLE
        }
        else{
            binding.userAdvertsLayout.visibility = View.GONE
        }
        userViewModel.userAdverts.observe(viewLifecycleOwner, Observer {
            advertAdapter.updateAdvertList(it)
            advertAdapter.notifyDataSetChanged()
            binding.userAdvertsLayout.visibility = View.VISIBLE
        })
        loadUser()
        binding.publicMailCopy.setOnClickListener(){
            addToClipBoard(binding.publicMail.text.toString(),"Email")
        }
        binding.publicPhoneCopy.setOnClickListener(){
            addToClipBoard(binding.publicPhone.text.toString(),"Phone")
        }
        return binding.root
    }
    private fun addToClipBoard(text:String,label:String){
        val myClipboard: ClipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val myClip: ClipData = ClipData.newPlainText(label,text)
        myClipboard.setPrimaryClip(myClip)
    }
    private fun openAdvert(advert: AdvertModel){
        var intent = Intent(context, AdvertActivity::class.java)
        if(FavoriteObject.existsAdvertId(advert._id))intent.putExtra("favorite",true)
        else intent.putExtra("favorite",false)
        intent.putExtra("advertModel",advert)
        startActivityForResult(intent,10)
    }
    private fun loadUser(){
        Picasso.get()
            .load("https://realmarketplace.shop/user${user?.mainImageUrl}")
            .placeholder(R.drawable.baseline_account_circle)
            .into(binding.publicImageUser)
        binding.publicUserFirstName.text = user.firstName
        binding.publicUserLastname.text = user.lastName
        binding.publicMail.text = user.email
        binding.publicPhone.text = user.phone
        binding.createdIn.text = AdvertAdapter.formatDate(user.createdIn)
    }
    companion object {
        fun newInstance() = PublicUserFragment()
    }
}