package com.realmarketplace.ui.advert.fullscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.realmarketplace.databinding.ActivityShowFullScreanBinding
import com.realmarketplace.model.AdvertModel

/**
 * A group of *activity*.
 *
 * Class for activity_show_full_screan layout and logic there.
 */
class ShowFullScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowFullScreanBinding
    private lateinit var adapterPager: AdapterViewPagerFullScreen
    private lateinit var advert:AdvertModel
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        advert = (intent.getSerializableExtra("advertModel") as? AdvertModel)!!
        val position = (intent.getSerializableExtra("showPosition") as? Int)?:0
        binding = ActivityShowFullScreanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapterPager = AdapterViewPagerFullScreen(advert.imagesUrls, blockMoving ={
                zoom:Float->blockViewPagerZoom(zoom)
        } )
        binding.viewPager.adapter = adapterPager
        binding.viewPager.post {
            binding.viewPager.setCurrentItem(position, false)
        }
        binding.myToolbar.setNavigationOnClickListener(){
            finish()
        }
        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val dataIntent = Intent()
                dataIntent.putExtra("showPosition",position)
                setResult(RESULT_OK, dataIntent)
                binding.myToolbar.subtitle="${position+1}/${advert.imagesUrls.size}"
            }

        })

    }
    fun blockViewPagerZoom(zoom:Float){
        binding.viewPager.isUserInputEnabled = zoom==1f
    }
}

