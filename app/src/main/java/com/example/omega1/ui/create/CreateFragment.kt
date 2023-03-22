package com.example.omega1.ui.create

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omega1.R
import com.example.omega1.SelectGenre
import com.example.omega1.databinding.FragmentCreateBinding
import com.example.omega1.model.AdvertModel
import com.example.omega1.rest.EnumViewData
import com.example.omega1.ui.auth.AuthViewModel
import com.example.omega1.ui.create.image.ImageAdapter
import com.example.omega1.vendor.usefulTools
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.adapter_create_image.view.*
import kotlinx.coroutines.*
import java.io.File

class CreateFragment : Fragment() {
    private lateinit var createVerification:CreateVerification
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var priceOptions: Array<String>
    private lateinit var condition: Array<String>
    private val createViewModel:CreateViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val enumViewDataModel: EnumViewData by activityViewModels()
    private val maxImage = 5
    private var actualImage = 0
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    private lateinit var pickMultipleMedia :ActivityResultLauncher<PickVisualMediaRequest>
    companion object {
        fun newInstance() = CreateFragment()
    }
    override fun onResume() {
        super.onResume()
        if(this::priceOptions.isInitialized&&this::condition.isInitialized)updatePriceDropDown()
        if(createViewModel.imagesFile.value!=null&&binding.imageCounter.text=="0/$maxImage"){
            actualImage=createViewModel.imagesFile.value!!.size
            for(uri in createViewModel.imagesFile.value!!){
                imageAdapter.addNewImage(uri)
            }
            binding.imageCounter.text = "$actualImage/$maxImage"
        }
        createVerification.checkAll()
    }

    private fun updatePriceDropDown(){
        val priceArrayAdapter = ArrayAdapter(requireContext(),R.layout.adapter_drop_down_price_option,priceOptions)
        val conditionArrayAdapter = ArrayAdapter(requireContext(),R.layout.adapter_drop_down_price_option,condition)
        binding.priceOptionInput.setText(priceOptions[0])
        binding.priceOptionInput.setAdapter(priceArrayAdapter)
        binding.conditionInput.setAdapter(conditionArrayAdapter)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        pickMultipleMedia=
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(maxImage)) { uris ->
                    handleOutput(uris)
            }
        priceOptions = enumViewDataModel.priceEnum.value!!
        condition = enumViewDataModel.conditionEnum.value!!
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        createVerification = CreateVerification(binding,resources)
        imageAdapter = ImageAdapter(mutableListOf(), clickDelete = {
            removeUri:File->imageClickDelete(removeUri)
        },clickAdd={
            addUri:File->imageClickAdd(addUri)
        })
        binding.recyclerView.adapter = imageAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(FragmentActivity(),LinearLayoutManager.HORIZONTAL,false)
        binding.priceOptionInput.setOnItemClickListener(){parent,view,position,id->
            if(position==0){
                binding.priceInput.isEnabled=true
                binding.priceLayout.isCounterEnabled=true
                binding.priceLayout.helperText=resources.getString(R.string.required)
                binding.priceInput.text = null
            }else{
                binding.priceInput.isEnabled=false
                binding.priceLayout.isCounterEnabled=false
                binding.priceLayout.helperText=null
                binding.priceInput.setText(priceOptions[position])
            }
        }
        binding.selectGenreInput.setOnClickListener(){
                val intent = Intent(context,SelectGenre::class.java)
                val list = ArrayList<String>()
                for(item in enumViewDataModel.genreGenreEnum.value!!){
                    list.add("${item.name}|${item.type}")
                }
                intent.putStringArrayListExtra("genreArray",list)
                startActivityForResult(intent,10)
        }
        binding.createButton.setOnClickListener(){
            binding.editAdvertNameInput.clearFocus()
            binding.editAdvertDescriptionInput.clearFocus()
            binding.priceInput.clearFocus()
            binding.conditionInput.clearFocus()
            createVerification.checkAll()
            submitForm()
        }
        println(Uri.parse("android.resource://com.example.omega1/drawable/camera_add"))
        imageAdapter.addNewImage(File(Uri.parse("android.resource://com.example.omega1/drawable/camera_add").path))
        createVerification.focusCondition()
        createVerification.focusAdvertDescription()
        createVerification.focusPrice()
        createVerification.focusAdvertName()
        createVerification.focusAdvertAuthor()
        return binding.root
    }
    private fun submitForm(){
        val validAdvertName = binding.editAdvertNameLayout.helperText==null
        val validAdvertDescription = binding.editAdvertDescriptionLayout.helperText==null
        val validAdvertPrice = binding.priceLayout.helperText==null
        val validAdvertCondition = binding.conditionLayout.helperText==null
        val validAdvertGenre = binding.selectGenreLayout.helperText==null
        val validAdvertAuthor = binding.editAdvertAuthorLayout.helperText==null
        if( validAdvertName&&
            validAdvertDescription&&
            validAdvertPrice&&
            validAdvertCondition&&
            validAdvertGenre&&
            validAdvertAuthor){
            val advertModel = AdvertModel(
                userId ="",
                title =binding.editAdvertNameInput.text.toString(),
                author=binding.editAdvertAuthorInput.text.toString(),
                description =binding.editAdvertDescriptionInput.text.toString(),
                createdIn ="",
                condition =binding.conditionInput.text.toString(),
                price =binding.priceInput.text.toString(),
                priceOption =binding.priceOptionInput.text.toString(),
                genreName =binding.selectGenreInput.text.toString().split("/")[0],
                genreType =binding.selectGenreInput.text.toString().split("/")[1],
                place ="",
                mainImage ="",
                imagesUrls =ArrayList()
            )
            val token = authViewModel.userToken.value.toString()
            val successful:Boolean =
                context?.let { createViewModel.createAdvert(advertModel,token, it) } == true
            if(successful){
                clearAllData()
                createViewModel.clean.value=false
            }
        }else{
            Toast.makeText(context,"Some of the input fields are still invalid!",Toast.LENGTH_LONG).show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK) {
                val name: String? = data?.getStringExtra("name")
                val type: String? = data?.getStringExtra("type")
                if (name?.isNotEmpty()==true && type?.isNotEmpty()==true){
                    binding.selectGenreInput.setText("$name/$type")
                }
                else{
                    binding.selectGenreInput.text = null
                }
                binding.selectGenreLayout.helperText=createVerification.validGenre()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun imageClickDelete(file:File){
        actualImage--
        val indexOfImage = imageAdapter.positionOfImage(file)
        imageAdapter.removeNewImage(file)
        imageAdapter.notifyItemRemoved(indexOfImage)
        binding.imageCounter.text = "$actualImage/$maxImage"
        createViewModel.removeOldFileByPos(indexOfImage-1)
        println("$indexOfImage $actualImage")
        if(indexOfImage==1&&actualImage>0)(binding.recyclerView.findViewHolderForLayoutPosition(2) as ImageAdapter.ImageAdapterHolder).itemView.cover_image.visibility = View.VISIBLE
    }
    private fun imageClickAdd(uri:File){
        when {
            context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            } == PackageManager.PERMISSION_GRANTED -> {
                pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
            }
        }
    }
    private fun handleOutput(uris:List<Uri>){
        if (uris.isNotEmpty()) {
            var actualMaximum:Int = maxImage-actualImage
            if(uris.size<=actualMaximum)actualMaximum = uris.size
            else Toast.makeText(context,"You can use just 5 photos",Toast.LENGTH_SHORT).show()
            actualMaximum--
            val fileArray = ArrayList<File>()
            val extensionList = listOf(".png",".jpg",".svg",".jpeg")
            CoroutineScope(Dispatchers.Main).launch {
                for(value in 0..actualMaximum){
                    var file = context?.let { usefulTools.getFileFromUri(uris[value], it) }
                    val extension = file?.absolutePath?.lastIndexOf(".")
                        ?.let { file?.absolutePath?.substring(it) }
                    file = context?.let { Compressor.compress(it, file!!) }!!
                    if(extensionList.contains(extension)){
                            actualImage++
                            binding.imageCounter.text = "$actualImage/$maxImage"
                            imageAdapter.addNewImage(file!!)
                            imageAdapter.notifyItemInserted(actualImage)
                            fileArray.add(file!!)
                    }
                    else Toast.makeText(context,"Allowed file extensions are ${extensionList.joinToString(", ")}.$value. extension is $extension",Toast.LENGTH_SHORT).show()
                }
                if(fileArray.size>0){
                    createViewModel.appendNewFile(fileArray)
                }
            }
        } else {
        }
    }
    private fun clearAllData(){
        binding.editAdvertNameInput.text = null
        binding.editAdvertDescriptionInput.text= null
        binding.priceOptionInput.setText(priceOptions[0])
        binding.priceInput.text=null
        binding.conditionInput.text=null
        binding.selectGenreInput.text=null
        binding.editAdvertAuthorInput.text=null
        if(createViewModel.imagesFile.value!=null){
            for(uri in createViewModel.imagesFile.value!!){
                imageAdapter.removeNewImage(uri)
            }
            imageAdapter.notifyDataSetChanged()
        }
        createViewModel.clearFiles()
        createVerification.checkAll()
    }
}