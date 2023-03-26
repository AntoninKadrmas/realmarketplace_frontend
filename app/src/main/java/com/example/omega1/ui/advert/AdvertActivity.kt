package com.example.omega1.ui.advert

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.omega1.databinding.ActivityAdvertBinding
import com.example.omega1.model.AdvertModel
import com.example.omega1.model.UserTokenAuth
import com.example.omega1.rest.*
import com.example.omega1.ui.search.advert.AdapterViewPager
import com.example.omega1.ui.search.advert.AdvertAdapter

class AdvertActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdvertBinding
    private lateinit var advert: AdvertModel
    private lateinit var adapterPager: AdapterViewPager
    private lateinit var token:UserTokenAuth
    private val advertViewModel:AdvertViewModel by viewModels()
    private lateinit var dataIntent:Intent
    private var favorite = false
    override fun onDestroy() {
        super.onDestroy()
        dataIntent.putExtra("prevAdvertId","")
        dataIntent.putExtra("logOut",false)
        dataIntent.putExtra("newAdvert",advert)
        dataIntent.putExtra("favorite",favorite)
        setResult(RECEIVER_EXPORTED, dataIntent)
        if(favorite){
            advertViewModel.addFavoriteAdvert(advert,token,this)
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdvertBinding.inflate(layoutInflater)

        setContentView(binding.root)
        token = if(intent.getSerializableExtra("token")!=null) (intent.getSerializableExtra("token") as? UserTokenAuth)!!
        else UserTokenAuth("")
        advert = (intent.getSerializableExtra("advertModel") as? AdvertModel)!!
        binding.myToolbar.setNavigationOnClickListener(){
            finish()
        }
        binding.myToolbar.menu.getItem(0).setOnMenuItemClickListener {
            if(token.token!=""){
                favorite=!favorite
                binding.myToolbar.menu.getItem(0).isVisible = false
                binding.myToolbar.menu.getItem(1).isVisible = true
            }
            else{
                Toast.makeText(this,"For this action you have to login.",Toast.LENGTH_SHORT).show()
            }
            true
        }
        binding.myToolbar.menu.getItem(1).setOnMenuItemClickListener {
            if(token.token!=""){
                favorite=!favorite
                binding.myToolbar.menu.getItem(0).isVisible = true
                binding.myToolbar.menu.getItem(1).isVisible = false
            }
            else{
                Toast.makeText(this,"For this action you have to login.",Toast.LENGTH_SHORT).show()
            }
            true
        }
        init()
    }
    private fun init(){
        adapterPager = AdapterViewPager(advert.imagesUrls)
        binding.viewPager.adapter = adapterPager
        binding.myToolbar.title=advert.title
        binding.advertGenreText.text = "${advert.genreName}/${advert.genreType}"
        binding.advertTitleText.text=advert.title
        binding.advertAuthorText.text=advert.author
        binding.advertPriceText.text = advert.price
        binding.advertDescriptionText.text = advert.description
        binding.advertConditionText.text = advert.condition
        binding.advertCreatedInText.text =AdvertAdapter.formatDate(advert.createdIn)
        dataIntent = Intent()
    }

}