package com.realmarketplace.ui.create

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.realmarketplace.R
import com.realmarketplace.SelectGenreActivity
import com.realmarketplace.databinding.FragmentCreateBinding
import com.realmarketplace.model.AdvertModel
import com.realmarketplace.model.UserTokenAuth
import com.realmarketplace.model.text.TextModelAuth
import com.realmarketplace.ui.advert.AdvertViewModel
import com.realmarketplace.viewModel.EnumViewData
import com.realmarketplace.ui.auth.AuthViewModel
import com.realmarketplace.ui.auth.LogOutAuth
import com.realmarketplace.ui.create.crud.CrudAdvertViewModel
import com.realmarketplace.ui.create.image.ImageAdapter
import com.realmarketplace.viewModel.PermissionViewModel
import com.realmarketplace.viewModel.ToastObject
import id.zelory.compressor.Compressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class UpdateDeleteFragment : Fragment() {
    private lateinit var createVerification: CreateVerification
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var priceOptions: ArrayList<String>
    private lateinit var condition: ArrayList<String>
    private lateinit var alertBuilder:AlertDialog.Builder
    private val crudAdvertViewModel: CrudAdvertViewModel by activityViewModels()
    private val enumViewDataModel: EnumViewData by activityViewModels()
    private val permissionModel: PermissionViewModel by activityViewModels()
    private val advertViewModel= AdvertViewModel
    private var originalFiles= ArrayList<File>()
    private lateinit var advert: AdvertModel
    private var deleteUrls = ArrayList<String>()
    private val maxImage = 5
    private var actualImage = 0
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    var buttonEnables = true
    companion object {
        fun newInstance() = UpdateDeleteFragment()
    }
    override fun onResume() {//stejny
        super.onResume()
        if(this::priceOptions.isInitialized&&this::condition.isInitialized)updateDropDown()
        if(crudAdvertViewModel.imagesFile.value!=null&&binding.imageCounter.text=="0/$maxImage"){
            actualImage=crudAdvertViewModel.imagesFile.value!!.size
            for(uri in crudAdvertViewModel.imagesFile.value!!){
                imageAdapter.addNewImage(uri)
            }
            binding.imageCounter.text = "$actualImage/$maxImage"
        }
        createVerification.checkAll()
    }
    private fun updateDropDown(){//stejny
        val priceArrayAdapter =
            context?.let { ArrayAdapter(it,R.layout.adapter_drop_down_price_option,priceOptions) }
        val conditionArrayAdapter =
            context?.let { ArrayAdapter(it,R.layout.adapter_drop_down_price_option,condition) }
        if(binding.priceInput.text.toString()==""){
            binding.priceOptionInput.setText(priceOptions[0])
            binding.priceInput.setText("")
        }
        binding.priceOptionInput.setAdapter(priceArrayAdapter)
        binding.conditionInput.setAdapter(conditionArrayAdapter)
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        alertBuilder = AlertDialog.Builder(context)
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        binding.createLayout.visibility=View.GONE
        binding.updateLayout.visibility=View.VISIBLE
        binding.deleteLayout.visibility=View.VISIBLE
        priceOptions = enumViewDataModel.priceEnum.value!!
        condition = enumViewDataModel.conditionEnum.value!!
        createVerification = CreateVerification(binding,resources)
        imageAdapter = ImageAdapter(ArrayList(),
            ArrayList(),
            Uri.parse("android.resource://com.realmarketplace/drawable/camera_add"),
            clickDelete = {
                    removeUri:File->imageClickDelete(removeUri)
            },clickAdd={
                    addUri:File->imageClickAdd(addUri)
        }, viewLifecycleOwner
        )
        binding.recyclerView.adapter = imageAdapter
        imageAdapter.addNewImage(File(""))
        if(advertViewModel.myAdverts.value!=null){
            advert= advertViewModel.showAdvert.value!!
            imageAdapter.addUrls(advert.imagesUrls)
            val fileInsertList = ArrayList<File>()
            for(x in advert.imagesUrls){
                val newFile = File(x)
                imageAdapter.addNewImage(newFile)
                fileInsertList.add(newFile)
            }
            crudAdvertViewModel.appendNewFile(fileInsertList)
            originalFiles=fileInsertList
            actualImage=advert.imagesUrls.size
            binding.imageCounter.text = "$actualImage/$maxImage"
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(FragmentActivity(),LinearLayoutManager.HORIZONTAL,false)
        binding.priceOptionInput.setOnItemClickListener(){parent,view,position,id->
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
            buttonEnables=true
        })
        binding.conditionInput.setOnItemClickListener() { _, _, position, _ ->
            binding.conditionLayout.helperText=createVerification.validCondition()
        }
        binding.deleteButton.setOnClickListener(){
            submitFormDelete()
        }
        binding.updateButton.setOnClickListener(){
            createVerification.clearFocus()
            submitFormUpdate()
        }
        createVerification.uploadAdvertDataIntoForm(advert)
        createVerification.focusCondition()
        createVerification.focusAdvertDescription()
        createVerification.focusPrice()
        createVerification.focusAdvertName()
        createVerification.focusAdvertAuthor()
        return binding.root
    }
    private fun submitFormDelete(){
        if(buttonEnables){
            val token: UserTokenAuth = AuthViewModel.userToken.value!!
            alertBuilder.setTitle("Are you sure you want to delete this advert. It will be deleted permanently.")
                .setCancelable(true)
                .setPositiveButton("YES"){_,it->
                    buttonEnables=false
                    context?.let { crudAdvertViewModel.deleteAdvert(advert._id,token.token, it) }
                }
                .setNegativeButton("NO"){_,it->
            }.show()
        }
    }
    private fun submitFormUpdate(){
        var newAdvert: AdvertModel = createVerification.createAdvert()
        val filesChanged = originalFiles==crudAdvertViewModel.imagesFile.value
        println(originalFiles)
        println(crudAdvertViewModel.imagesFile.value)
        if(createVerification.submitFormVerification()){
            if((!createVerification.checkOldAndNewAdvertAreSimilar(advert,newAdvert)||!filesChanged)&&buttonEnables){
                val token: UserTokenAuth = AuthViewModel.userToken.value!!
                val useUrls = advert.imagesUrls
                if(crudAdvertViewModel.imagesFile.value!=null){
                    if(crudAdvertViewModel.imagesFile.value?.size!!>0){
                        if(useUrls.contains(crudAdvertViewModel.imagesFile.value?.get(0)!!.path))newAdvert.mainImageUrl=crudAdvertViewModel.imagesFile.value?.get(0)!!.path
                        for(url in useUrls){
                            if(deleteUrls.contains(url))useUrls.remove(url)
                        }
                        newAdvert.imagesUrls=useUrls
                    }
                }
                newAdvert._id=advert._id
                newAdvert.userId=advert.userId
                newAdvert.createdIn=advert.createdIn
                alertBuilder.setTitle("Are you sure you want to update advert?")
                    .setCancelable(true)
                    .setPositiveButton("YES"){_,it->
                        buttonEnables=false
                        context?.let { crudAdvertViewModel.updateAdvert(newAdvert,deleteUrls,token.token, it) }
                    }
                    .setNegativeButton("NO"){_,it->
                }.show()
            }else{
                if(buttonEnables) context?.let { ToastObject.showToast(it,"You didn't do any changes.",Toast.LENGTH_SHORT) }
            }
        }else{
            if(buttonEnables) context?.let { ToastObject.showToast(it,
                TextModelAuth.SOME_INVALID_FIELDS,Toast.LENGTH_LONG) }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {//stejne
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
                    for(value in 0..length-1){
                        data?.clipData?.getItemAt(value)?.let { uris.add(it.uri) }
                    }
                    if(uris!=null) handleOutput(uris)
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
    private fun imageClickDelete(file:File){//stejne
        actualImage--
        val indexOfImage = imageAdapter.positionOfImage(file)
        imageAdapter.removeNewImage(file)
        imageAdapter.notifyItemRemoved(indexOfImage)
        binding.imageCounter.text = "$actualImage/$maxImage"
        crudAdvertViewModel.removeOldFileByPos(indexOfImage-1)
        deleteUrls.add(file.path)
    }
    private fun imageClickAdd(uri:File){//stejne
        permissionModel.setPermissionStorageAsk(true)
        if(permissionModel.permissionStorage.value==true){
            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), 15)
        }
    }
    private fun handleOutput(uris:List<Uri>){//stejne
        if (uris.isNotEmpty()) {
            var actualMaximum:Int = maxImage-actualImage
            if(uris.size<=actualMaximum)actualMaximum = uris.size
            else context?.let { ToastObject.showToast(it,"You can use just 5 photos",Toast.LENGTH_SHORT) }
            actualMaximum--
            val fileArray = ArrayList<File>()
            val extensionList = listOf("png","jpg","svg","jpeg")
            CoroutineScope(Dispatchers.Main).launch {
                for(value in 0..actualMaximum){
                    var file = context?.let { UriToFileConvertor.getRealPathFromURI(it,uris[value])?.let { File(it) } }!!
                    val extension = file?.absolutePath.toString()
                        .substring(file?.absolutePath.toString().lastIndexOf(".") + 1)
                    file = context?.let { Compressor.compress(it, file!!) }!!
                    if(extensionList.contains(extension)){
                        actualImage++
                        binding.imageCounter.text = "$actualImage/$maxImage"
                        imageAdapter.addNewImage(file!!)
                        imageAdapter.notifyItemInserted(actualImage)
                        fileArray.add(file!!)
                    }
                    else ToastObject.showToast(requireContext(),"($extension)Allowed file extensions are ${extensionList.joinToString(", ")}.",Toast.LENGTH_LONG)
                }
                if(fileArray.size>0){
                    crudAdvertViewModel.appendNewFile(fileArray)
                }
            }
        }
    }
}