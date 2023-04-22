package com.realmarketplace

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.realmarketplace.ui.work.WorkInProgressActivity
import com.realmarketplace.ui.work.WorkViewModel

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WorkViewModel.advertProductionEnable.observe(this, Observer {
            if(it)startActivity(Intent(this,MainActivity::class.java))
            else startActivity(Intent(this,WorkInProgressActivity::class.java))
            finish()
        })
        WorkViewModel.checkEnableProduction()
    }
}