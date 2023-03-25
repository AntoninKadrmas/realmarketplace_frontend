package com.example.omega1

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.omega1.databinding.ActivityAdvertBinding
import com.example.omega1.model.AdvertModel
import com.example.omega1.ui.search.advert.AdapterViewPager
import com.example.omega1.ui.search.advert.AdvertAdapter

class AdvertActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdvertBinding
    private lateinit var advert: AdvertModel
    private lateinit var adapterPager: AdapterViewPager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdvertBinding.inflate(layoutInflater)
        setContentView(binding.root)
        advert = (intent.getSerializableExtra("advertModel") as? AdvertModel)!!
        binding.myToolbar.setNavigationOnClickListener(){
            finish()
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
    }
}