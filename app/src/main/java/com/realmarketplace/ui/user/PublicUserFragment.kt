package com.realmarketplace.ui.user

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.realmarketplace.R
import com.realmarketplace.databinding.FragmentPublicUserBinding
import com.realmarketplace.model.AdvertModel
import com.realmarketplace.model.LightUser
import com.realmarketplace.model.text.TextModelGlobal
import com.realmarketplace.ui.advert.AdvertActivity
import com.realmarketplace.ui.auth.AuthViewModel
import com.realmarketplace.ui.favorite.FavoriteObject
import com.realmarketplace.ui.search.advert.AdvertAdapter
import com.realmarketplace.viewModel.LoadingBar
import com.squareup.picasso.Picasso

/**
 * A group of *fragment*.
 *
 * Class for fragment_public_user layout and logic there.
 */
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
        userViewModel.userAdverts.observe(viewLifecycleOwner, Observer {
            advertAdapter.updateAdvertList(it)
            advertAdapter.notifyDataSetChanged()
            binding.userAdvertsLayout.visibility = View.VISIBLE
        })
        AuthViewModel.userToken.value?.let {
            context?.let { it1 ->
                LoadingBar.mutableHideLoadingUserActivity.value=false
                userViewModel.loadAllUserPublicAdverts(user,
                    it, it1
                )
            }
        }
        advertAdapter = AdvertAdapter(ArrayList(), clickAdvert = {
                advert: AdvertModel ->openAdvert(advert)
        },false)
        binding.userAdverts.adapter = advertAdapter
            val layoutManager = LinearLayoutManager(
            FragmentActivity(),
            LinearLayoutManager.VERTICAL,false)
        binding.userAdverts.layoutManager = layoutManager
        binding.userAdvertsLayout.visibility = View.GONE
        binding.publicMailCopy.setOnClickListener(){
            addToClipBoard(binding.publicMail.text.toString(),"Email")
        }
        binding.publicPhoneCopy.setOnClickListener(){
            addToClipBoard(binding.publicPhone.text.toString(),"Phone")
        }
        loadUser()
        return binding.root
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used for copy information's into clip board.
     */
    private fun addToClipBoard(text:String,label:String){
        val myClipboard: ClipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val myClip: ClipData = ClipData.newPlainText(label,text)
        myClipboard.setPrimaryClip(myClip)
        Toast.makeText(context,"Copied.",Toast.LENGTH_SHORT).show()
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used for start new activity with given advert content.
     * @param advert advert that going to be displayed in new activity viz. AdvertModel
     */
    private fun openAdvert(advert: AdvertModel){
        var intent = Intent(context, AdvertActivity::class.java)
        if(FavoriteObject.existsAdvertId(advert._id))intent.putExtra("favorite",true)
        else intent.putExtra("favorite",false)
        intent.putExtra("advertModel",advert)
        startActivityForResult(intent,10)
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to load all user information's into fragment layout.
     * Used Picasso module for displaying images by https url.
     */
    private fun loadUser(){
        Picasso.get()
            .load("${TextModelGlobal.REAL_MARKET_URL}/user${user?.mainImageUrl}")
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