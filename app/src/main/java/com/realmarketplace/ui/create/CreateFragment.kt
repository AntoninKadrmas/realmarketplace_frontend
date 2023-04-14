package com.realmarketplace.ui.create

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.realmarketplace.databinding.FragmentCreateBinding
import com.realmarketplace.model.AdvertModel
import com.realmarketplace.model.UserTokenAuth
import com.realmarketplace.ui.advert.AdvertViewModel
import com.realmarketplace.viewModel.EnumViewData
import com.realmarketplace.ui.auth.AuthViewModel
import com.realmarketplace.ui.auth.LogOutAuth
import com.realmarketplace.ui.create.crud.CrudAdvertViewModel
import com.realmarketplace.ui.create.crud.CrudShared
import com.realmarketplace.ui.create.genre.SelectGenreActivity
import com.realmarketplace.ui.create.image.ImageAdapter
import com.realmarketplace.viewModel.LoadingBar
import com.realmarketplace.viewModel.PermissionViewModel
import com.realmarketplace.viewModel.ToastObject
import java.io.File

class CreateFragment : Fragment() {
    private lateinit var createVerification: CreateVerification
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var priceOptions: ArrayList<String>
    private lateinit var condition: ArrayList<String>
    private lateinit var alertBuilder:AlertDialog.Builder
    private lateinit var crudShared: CrudShared
    private val crudAdvertViewModel: CrudAdvertViewModel by activityViewModels()
    private val enumViewDataModel: EnumViewData by activityViewModels()
    private val permissionModel: PermissionViewModel by activityViewModels()
    private val advertViewModel= AdvertViewModel
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    var buttonEnables = true

    companion object {
        fun newInstance() = CreateFragment()
        const val NAME = "Create"
    }
    override fun onResume() {
        super.onResume()
        if(this::priceOptions.isInitialized&&this::condition.isInitialized) context?.let {
            crudShared.updateDropDown(
                it,priceOptions,condition)
        }
        crudShared.loadImages()
        createVerification.checkAll()
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        alertBuilder = AlertDialog.Builder(context)
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        priceOptions = enumViewDataModel.priceEnum.value!!
        condition = enumViewDataModel.conditionEnum.value!!
        createVerification = CreateVerification(binding,resources)
        imageAdapter = ImageAdapter(ArrayList(),
            ArrayList(),
            Uri.parse("android.resource://com.realmarketplace/drawable/camera_add"),
            clickDelete = {
            removeUri:File->crudShared.clickDelete(removeUri)
        },clickAdd={
            addUri:Boolean->imageClickAdd(addUri)
        }, viewLifecycleOwner)
        binding.recyclerView.adapter = imageAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(FragmentActivity(),LinearLayoutManager.HORIZONTAL,false)
        imageAdapter.addNewImage(File(""))
        binding.priceOptionInput.setOnItemClickListener(){_,_,position,_->
          createVerification.priceOptionHandleResult(position,priceOptions)
        }
        binding.selectGenreInput.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val intent = Intent(context, SelectGenreActivity::class.java)
                        val list = ArrayList<String>()
                        for(item in enumViewDataModel.genreGenreEnum.value!!){
                            list.add("${item.name}|${item.type}")
                        }
                        intent.putStringArrayListExtra("genreArray",list)
                        startActivityForResult(intent,10)
                    }
                }
                return v?.onTouchEvent(event) ?: true
            }
        })
        crudAdvertViewModel.buttonsEnabled.observe(viewLifecycleOwner,Observer{
            LoadingBar.mutableHideLoadingMainActivity.value=true
            buttonEnables=true
        })
        binding.createButton.setOnClickListener(){
            createVerification.clearFocus()
            submitForm()
        }
        binding.conditionInput.setOnItemClickListener() { _, _, _, _ ->
            binding.conditionLayout.helperText=createVerification.validCondition()
        }
        crudAdvertViewModel.clean.observe(viewLifecycleOwner, Observer {
            clearAllData(it)
        })
        createVerification.focusCondition()
        createVerification.focusAdvertDescription()
        createVerification.focusPrice()
        createVerification.focusAdvertName()
        createVerification.focusAdvertAuthor()
        crudShared = CrudShared(imageAdapter,crudAdvertViewModel,binding)
        AdvertViewModel.changeToolBarState(true)
        return binding.root
    }
    private fun submitForm(){
        if(createVerification.submitFormVerification()&&buttonEnables){
            val token: UserTokenAuth = AuthViewModel.userToken.value!!
            var noImage = ""
            if(crudShared.actualImage==0)noImage=" without any image"
            alertBuilder.setTitle("Are you sure you want to create new advert$noImage.")
                .setCancelable(true)
                .setPositiveButton("YES"){_,_->
                    buttonEnables=false
                    LoadingBar.mutableHideLoadingMainActivity.value=false
                    context?.let { crudAdvertViewModel.createAdvert(createVerification.createAdvert(),token.token, it) }
                }
                .setNegativeButton("NO"){_,_->
                }.show()
        }else{
            if(buttonEnables) context?.let { ToastObject.showToast(it,"Some of the input fields are still invalid!",Toast.LENGTH_LONG) }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK) {
                val name: String? = data?.getStringExtra("name")
                val type: String? = data?.getStringExtra("type")
                val logOut:Boolean? = data?.getBooleanExtra("logOut",false)
                LogOutAuth.setLogOut(logOut!!)
                if (name?.isNotEmpty()==true && type?.isNotEmpty()==true){
                    binding.selectGenreInput.setText("$name/$type")
                }
                else{
                    binding.selectGenreInput.text = null
                }
                binding.selectGenreLayout.helperText=createVerification.validGenre()
            }
        }
        if(requestCode==15){
            if(resultCode==Activity.RESULT_OK){
                try{
                    val uris = ArrayList<Uri>()
                    val length:Int = data?.clipData?.itemCount!!
                    for(value in 0 until length){
                        data?.clipData?.getItemAt(value)?.let { uris.add(it.uri) }
                    }
                    if(uris.size>0) context?.let { crudShared.handleOutput(uris, it) }
                }catch (e:Exception){
                    context?.let { ToastObject.showToast(it,"Image type is not supported.",Toast.LENGTH_LONG) }
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun imageClickAdd(state:Boolean){
        val intent = crudShared.clickAdd(state,permissionModel)
        if(intent!=null) startActivityForResult(Intent.createChooser(intent,"Select Picture"), 15)
    }
    private fun clearAllData(advert: AdvertModel){
        if(crudAdvertViewModel.imagesFile.value!=null){
            for(uri in crudAdvertViewModel.imagesFile.value!!){
                imageAdapter.removeNewImage(uri)
            }
            imageAdapter.notifyDataSetChanged()
        }
        if(crudAdvertViewModel.executed){
            advertViewModel.addNewMyAdvertItem(advert)
            crudAdvertViewModel.executed=false
        }
        crudAdvertViewModel.clearFiles()
        context?.let { crudShared.updateDropDown(it,priceOptions,condition) }
        createVerification.clearInputData()
    }
}