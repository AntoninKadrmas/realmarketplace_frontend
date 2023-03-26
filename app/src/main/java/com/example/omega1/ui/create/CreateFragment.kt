package com.example.omega1.ui.create

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
import com.example.omega1.R
import com.example.omega1.SelectGenreActivity
import com.example.omega1.databinding.FragmentCreateBinding
import com.example.omega1.model.AdvertModel
import com.example.omega1.model.UserTokenAuth
import com.example.omega1.ui.advert.AdvertViewModel
import com.example.omega1.viewModel.EnumViewData
import com.example.omega1.ui.auth.AuthViewModel
import com.example.omega1.ui.create.crud.CrudAdvertViewModel
import com.example.omega1.ui.create.image.ImageAdapter
import com.example.omega1.viewModel.PermissionViewModel
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.adapter_create_image.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class CreateFragment : Fragment() {
    private lateinit var createVerification:CreateVerification
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var priceOptions: ArrayList<String>
    private lateinit var condition: ArrayList<String>
    private lateinit var alertBuilder:AlertDialog.Builder
    private val crudAdvertViewModel: CrudAdvertViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val enumViewDataModel: EnumViewData by activityViewModels()
    private val permissionModel: PermissionViewModel by activityViewModels()
    private val advertViewModel:AdvertViewModel by activityViewModels()
    private val maxImage = 5
    private var actualImage = 0
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    companion object {
        fun newInstance() = CreateFragment()
        const val NAME = "Create"
    }
    override fun onResume() {
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

    private fun updateDropDown(){
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
        priceOptions = enumViewDataModel.priceEnum.value!!
        condition = enumViewDataModel.conditionEnum.value!!
        createVerification = CreateVerification(binding,resources)
        imageAdapter = ImageAdapter(ArrayList(),
            ArrayList(),
            Uri.parse("android.resource://com.example.omega1/drawable/camera_add"),
            clickDelete = {
            removeUri:File->imageClickDelete(removeUri)
        },clickAdd={
            addUri:File->imageClickAdd(addUri)
        })
        binding.recyclerView.adapter = imageAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(FragmentActivity(),LinearLayoutManager.HORIZONTAL,false)
        imageAdapter.addNewImage(File(""))
        binding.priceOptionInput.setOnItemClickListener(){parent,view,position,id->
          createVerification.priceOptionHandleResult(position,priceOptions)
        }
        binding.selectGenreInput.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val intent = Intent(context,SelectGenreActivity::class.java)
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
        binding.createButton.setOnClickListener(){
            createVerification.clearFocus()
            submitForm()
        }
        binding.conditionInput.setOnItemClickListener() { _, _, position, _ ->
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
        return binding.root
    }
    private fun submitForm(){
        if(createVerification.submitFormVerification()){
            val token: UserTokenAuth = authViewModel.userToken.value!!
            var noImage = ""
            if(actualImage==0)noImage=" without any image"
            alertBuilder.setTitle("Are you sure you want to create new advert$noImage.")
                .setCancelable(true)
                .setPositiveButton("YES"){_,it->
                    context?.let { crudAdvertViewModel.createAdvert(createVerification.createAdvert(),token.token, it) }
                }
                .setNegativeButton("NO"){_,it->
                }.show()
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
                    Toast.makeText(context,"Image type is not supported.",Toast.LENGTH_LONG).show()
                }
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
        crudAdvertViewModel.removeOldFileByPos(indexOfImage-1)
        println("$indexOfImage $actualImage")
        if(indexOfImage==1&&actualImage>0)(binding.recyclerView.findViewHolderForLayoutPosition(2) as ImageAdapter.ImageAdapterHolder).itemView.cover_image.visibility = View.VISIBLE
    }
    private fun imageClickAdd(uri:File){
        permissionModel.setPermissionStorageAsk(true)
        if(permissionModel.permissionStorage.value==true){
            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), 15)
        }
    }
    private fun handleOutput(uris:List<Uri>){
        if (uris.isNotEmpty()) {
            var actualMaximum:Int = maxImage-actualImage
            if(uris.size<=actualMaximum)actualMaximum = uris.size
            else Toast.makeText(context,"You can use just 5 photos",Toast.LENGTH_SHORT).show()
            actualMaximum--
            val fileArray = ArrayList<File>()
            val extensionList = listOf("png","jpg","svg","jpeg")
            CoroutineScope(Dispatchers.Main).launch {
                for(value in 0..actualMaximum){
                    var file = context?.let { UriToFileConvertor.getRealPathFromURI(it,uris[value])?.let { File(it) } }!!
                    val extension = file?.absolutePath.toString()
                        .substring(file?.absolutePath.toString().lastIndexOf(".") + 1)
                    file = context?.let { Compressor.compress(it, file!!) }!!
                    println(extension)
                    if(extensionList.contains(extension)){
                        actualImage++
                        binding.imageCounter.text = "$actualImage/$maxImage"
                        imageAdapter.addNewImage(file!!)
                        imageAdapter.notifyItemInserted(actualImage)
                        fileArray.add(file!!)
                    }
                    else Toast.makeText(context,"($extension)Allowed file extensions are ${extensionList.joinToString(", ")}.",Toast.LENGTH_LONG).show()
                }
                if(fileArray.size>0){
                    crudAdvertViewModel.appendNewFile(fileArray)
                }
            }
        }
    }
    private fun clearAllData(advert:AdvertModel){
        if(crudAdvertViewModel.imagesFile.value!=null){
            for(uri in crudAdvertViewModel.imagesFile.value!!){
                imageAdapter.removeNewImage(uri)
            }
            imageAdapter.notifyDataSetChanged()
        }
        if(crudAdvertViewModel.executed){
            advertViewModel.addNewMyAdvert(advert)
            crudAdvertViewModel.executed=false
        }
        crudAdvertViewModel.clearFiles()
        updateDropDown()
        createVerification.clearInputData()
    }
}