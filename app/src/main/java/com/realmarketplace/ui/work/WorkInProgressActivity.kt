package com.realmarketplace.ui.work

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.realmarketplace.MainActivity
import com.realmarketplace.databinding.ActivityWorkInProgressBinding

class WorkInProgressActivity : AppCompatActivity() {
    private lateinit var advertAdapter: WorkAdapter

    private lateinit var binding: ActivityWorkInProgressBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WorkViewModel.advertProductionEnable.observe(this, Observer {
            binding.container.isRefreshing=false
            if(it){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        })
        binding = ActivityWorkInProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var advertList = ArrayList<Int>()
        advertList.add(0)
        advertAdapter = WorkAdapter(advertList)
        binding.workAdapter.adapter = advertAdapter
        val layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,false)
        binding.workAdapter.layoutManager = layoutManager
        binding.container.setOnRefreshListener {
            WorkViewModel.checkEnableProduction()
        }
    }
}