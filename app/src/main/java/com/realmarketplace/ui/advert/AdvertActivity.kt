package com.realmarketplace.ui.advert

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.realmarketplace.R
import com.realmarketplace.databinding.ActivityAdvertBinding
import com.realmarketplace.model.AdvertModel
import com.realmarketplace.model.UserTokenAuth
import com.realmarketplace.model.text.TextModelGlobal
import com.realmarketplace.ui.auth.AuthViewModel
import com.realmarketplace.ui.auth.LogOutAuth
import com.realmarketplace.ui.favorite.FavoriteObject
import com.realmarketplace.ui.search.advert.AdapterViewPager
import com.realmarketplace.ui.search.advert.AdvertAdapter
import com.realmarketplace.ui.user.UserActivity
import com.realmarketplace.viewModel.LoadingBar
import com.realmarketplace.viewModel.ToastObject
import com.squareup.picasso.Picasso
import me.relex.circleindicator.CircleIndicator3

/**
 * A group of *activity*.
 *
 * Class for activity_advert layout and logic there.
 */
class AdvertActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdvertBinding
    private lateinit var advert: AdvertModel
    private lateinit var adapterPager: AdapterViewPager
    private lateinit var token: UserTokenAuth
    private var favorite = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogOutAuth.logOut.observe(this, Observer {
            if(it){
                val dataIntent = Intent()
                dataIntent.putExtra("prevAdvertId",advert._id)
                dataIntent.putExtra("logOut",true)
                setResult(RESULT_OK, dataIntent)
                finish()
            }
        })
        binding = ActivityAdvertBinding.inflate(layoutInflater)
        setContentView(binding.root)
        token = if(AuthViewModel.userToken.value!=null) AuthViewModel.userToken.value!!
        else UserTokenAuth("")
        if(intent.getBooleanExtra("favorite",false))switchFavorite(true)
        else switchFavorite(false)
        advert = (intent.getSerializableExtra("advertModel") as? AdvertModel)!!
        binding.myToolbar.setNavigationOnClickListener(){
            finish()
        }
        AdvertViewModel.enableButton.observe(this, Observer {
            LoadingBar.mutableHideLoadingAdvertActivity.value=true
            binding.myToolbar.menu.getItem(it).isEnabled=true
        })
        binding.myToolbar.menu.getItem(AdvertViewModel.FAVORITE_OFF).setOnMenuItemClickListener {
            if(token.token!=""){
                switchFavorite(true)
                LoadingBar.mutableHideLoadingAdvertActivity.value=false
                AdvertViewModel.addFavoriteAdvert(advert, token, this)
                binding.myToolbar.menu.getItem(AdvertViewModel.FAVORITE_ON).isEnabled=false
                FavoriteObject.addNewAdvertId(advert)
            }
            else{
                ToastObject.showToast(this,"For this action you have to login.",Toast.LENGTH_SHORT)
            }
            true
        }
        binding.myToolbar.menu.getItem(AdvertViewModel.FAVORITE_ON).setOnMenuItemClickListener {
            if(token.token!=""){
                switchFavorite(false)
                LoadingBar.mutableHideLoadingAdvertActivity.value=false
                AdvertViewModel.deleteFavoriteAdvert(advert, token, this)
                binding.myToolbar.menu.getItem(AdvertViewModel.FAVORITE_OFF).isEnabled=false
                FavoriteObject.removeAdvertId(advert._id)
            }
            else{
                ToastObject.showToast(this,"For this action you have to login.",Toast.LENGTH_SHORT)
            }
            true
        }
        binding.userInfoLayout.setOnClickListener(){
            val intent = Intent(this, UserActivity::class.java)
            intent.putExtra("advert",advert)
            intent.putExtra("publicUserProfile",true)
            startActivityForResult(intent,10)
        }
        LoadingBar.mutableHideLoadingAdvertActivity.observe(this,Observer{
            if(!it)binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        })
        init()
    }
    /**
     * A group of *advert_functions*.
     *
     * Function used to switch between favorite icons.
     *
     * @param state boolean variable that decide if favorite state should be in true (favorite) or false (not favorite) state
     */
    private fun switchFavorite(state:Boolean){
        if(state){
            favorite=true
            binding.myToolbar.menu.getItem(AdvertViewModel.FAVORITE_OFF).isVisible = false
            binding.myToolbar.menu.getItem(AdvertViewModel.FAVORITE_ON).isVisible = true
        }
        else{
            favorite=false
            binding.myToolbar.menu.getItem(AdvertViewModel.FAVORITE_OFF).isVisible = true
            binding.myToolbar.menu.getItem(AdvertViewModel.FAVORITE_ON).isVisible = false
        }
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
    /**
     * A group of *advert_functions*.
     *
     * Function used to load all information about advert into layout.
     * Used Picasso module for displaying images by https url.

     */
    private fun init(){
        adapterPager = AdapterViewPager(advert.imagesUrls)
        binding.viewPager.adapter = adapterPager
        val indicator: CircleIndicator3 = binding.indicator
        indicator.setViewPager(binding.viewPager)
        adapterPager.registerAdapterDataObserver(indicator.getAdapterDataObserver());
        binding.myToolbar.title=advert.title
        binding.advertGenreText.text = "${advert.genreName}/${advert.genreType}"
        binding.advertTitleText.text=advert.title
        binding.advertAuthorText.text=advert.author
        binding.advertPriceText.text = AdvertAdapter.convertPrice(advert.price)
        binding.advertDescriptionText.text = advert.description
        binding.advertConditionText.text = advert.condition
        binding.advertCreatedInText.text = AdvertAdapter.formatDate(advert.createdIn!!)
        binding.myToolbar.menu.getItem(AdvertViewModel.VISIBLE_OFF).isVisible = false
        binding.myToolbar.menu.getItem(AdvertViewModel.VISIBLE_ON).isVisible = false
        if(advert.user==null){
            if(AuthViewModel.userToken.value==null)binding.userInfoLayout.visibility= View.GONE
            else if(AuthViewModel.userToken.value!!.token=="")binding.userInfoLayout.visibility= View.GONE
        }
        else{
            binding.userInfoLayout.visibility= View.VISIBLE
            binding.lastNameText.text= advert.user?.lastName ?: ""
            binding.firstNameText.text= advert.user?.firstName ?: ""
        }
        Picasso.get()
            .load("${TextModelGlobal.REAL_MARKET_URL}/user${advert.user?.mainImageUrl}")
            .placeholder(R.drawable.baseline_account_circle)
            .into(binding.imageUserAdvert)
    }
}