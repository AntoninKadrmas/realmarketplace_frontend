package com.example.omega1.ui.create
import android.app.Activity
import android.content.Context
import android.content.Intent
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
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omega1.R
import com.example.omega1.SelectGenre
import com.example.omega1.databinding.FragmentCreateBinding
import com.example.omega1.model.AdvertModel
import com.example.omega1.ui.create.image.ImageAdapter
import com.example.omega1.rest.EnumViewData
import com.example.omega1.ui.auth.AuthViewModel
import kotlinx.android.synthetic.main.adapter_create_image.view.*


class CreateFragment : Fragment() {

    private lateinit var imageAdapter: ImageAdapter
    private lateinit var priceOptions: Array<String>
    private lateinit var condition: Array<String>
    private val createViewModel:CreateViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val maxImage = 5
    private var actualImage = 0
    private val enumViewDataModel: EnumViewData by activityViewModels()
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!

    private lateinit var pickMultipleMedia :ActivityResultLauncher<PickVisualMediaRequest>
    companion object {
        fun newInstance() = CreateFragment()
    }
    override fun onResume() {
        super.onResume()
        if(this::priceOptions.isInitialized&&this::condition.isInitialized)updatePriceDropDown()
        if(createViewModel.images.value!=null&&binding.imageCounter.text=="0/$maxImage"){
            actualImage=createViewModel.images.value!!.size
            for(uri in createViewModel.images.value!!){
                imageAdapter.addNewImage(uri)
            }
            binding.imageCounter.text = "$actualImage/$maxImage"
        }
        checkAll()
    }
    private fun checkAll(){
        if(binding.editAdvertNameInput.text?.isEmpty() == false)binding.editAdvertNameLayout.helperText=validAdvertName()
        if(binding.editAdvertDescriptionInput.text?.isEmpty() == false)binding.editAdvertDescriptionLayout.helperText=validAdvertDescription()
        if(binding.priceInput.text?.isEmpty() == false)binding.priceLayout.helperText=validPrice()
        if(binding.conditionInput.text?.isEmpty() == false)binding.conditionLayout.helperText=validCondition()
        if(binding.selectGenreInput.text?.isEmpty() == false)binding.selectGenreLayout.helperText=validGenre()
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
        imageAdapter = ImageAdapter(mutableListOf(), clickDelete = {
            removeUri:Uri->imageClickDelete(removeUri)
        },clickAdd={
            addUri:Uri->imageClickAdd(addUri)
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
            checkAll()
            submitForm()
        }
        imageAdapter.addNewImage(Uri.parse("android.resource://com.example.omega1/drawable/camera_add"))
        focusCondition()
        focusAdvertDescription()
        focusPrice()
        focusAdvertName()
        return binding.root
    }
    private fun submitForm(){
        val validAdvertName = binding.editAdvertNameLayout.helperText==null
        val validAdvertDescription = binding.editAdvertDescriptionLayout.helperText==null
        val validAdvertPrice = binding.priceLayout.helperText==null
        val validAdvertCondition = binding.conditionLayout.helperText==null
        val validAdvertGenre = binding.selectGenreLayout.helperText==null
        if( validAdvertName&&
            validAdvertDescription&&
            validAdvertPrice&&
            validAdvertCondition&&
            validAdvertGenre){
            val advertModel = AdvertModel(
                lightUserId ="",
                title =binding.editAdvertNameInput.text.toString(),
                description =binding.editAdvertDescriptionInput.text.toString(),
                createdIn ="",
                condition =binding.conditionInput.text.toString(),
                price =binding.priceInput.text.toString(),
                priceOption =binding.priceOptionInput.text.toString(),
                genreName =binding.selectGenreInput.text.toString().split("/")[0],
                genreType =binding.selectGenreInput.text.toString().split("/")[1],
                place ="",
                mainImage ="",
                imagesUrls =ArrayList<String>()
            )
            val token = authViewModel.userToken.value.toString()
            val imagesUri =
            context?.let { createViewModel.createAdvert(advertModel,token, it) }
        }else{
            Toast.makeText(context,"Some of the input fields are still invalid!",Toast.LENGTH_LONG).show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
                binding.selectGenreLayout.helperText=validGenre()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun imageClickDelete(uri:Uri){
        actualImage--
        val indexOfImage = imageAdapter.positionOfImage(uri)
        imageAdapter.removeNewImage(uri)
        imageAdapter.notifyItemRemoved(indexOfImage)
        binding.imageCounter.setText("$actualImage/$maxImage")
        createViewModel.removeOld(uri)
        if(indexOfImage==1&&actualImage>0)(binding.recyclerView.findViewHolderForLayoutPosition(2) as ImageAdapter.ImageAdapterHolder).itemView.cover_image.visibility = View.VISIBLE
    }
    private fun imageClickAdd(uri:Uri){
        pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    private fun handleOutput(uris:List<Uri>){
        if (uris.isNotEmpty()) {
            var actualMaximum:Int = maxImage-actualImage
            if(uris.size<=actualMaximum)actualMaximum = uris.size
            else Toast.makeText(context,"You can use just 5 photos",Toast.LENGTH_SHORT).show()
            actualImage += if(uris.size<=actualMaximum) uris.size else actualMaximum
            actualMaximum--
            val imageList= ArrayList<Uri>()
            for(value in 0..actualMaximum){
                imageAdapter.addNewImage(uris[value])
                imageList.add(uris[value])
            }
            if(++actualMaximum>0){
                createViewModel.appendNew(imageList)
                imageAdapter.notifyDataSetChanged()
                binding.imageCounter.setText("$actualImage/$maxImage")
            }
        } else {
        }
    }
    private fun focusAdvertName(){
        binding.editAdvertNameInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.editAdvertNameInput.text?.isNotEmpty()==true) {
                binding.editAdvertNameLayout.helperText = validAdvertName()
            }else if(!focused && binding.editAdvertNameLayout.helperText!=resources.getString(R.string.required))binding.editAdvertNameLayout.helperText = resources.getString(R.string.required)
        }
    }
    private fun validAdvertName():String?{
        val advertName = binding.editAdvertNameInput.text.toString()
        val checkAdvertName = advertName.length<=50
        if(!checkAdvertName){
            binding.editAdvertNameInput.error="Limit of advert name is 50 characters."
            return "Invalid advert name"
        }
        binding.editAdvertNameInput.error = null
        return null
    }
    private fun focusAdvertDescription(){
        binding.editAdvertDescriptionInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.editAdvertDescriptionInput.text?.isNotEmpty()==true) {
                binding.editAdvertDescriptionLayout.helperText = validAdvertDescription()
            }else if(!focused && binding.editAdvertDescriptionLayout.helperText!=resources.getString(R.string.required))binding.editAdvertDescriptionLayout.helperText = resources.getString(R.string.required)
        }
    }
    private fun validAdvertDescription():String?{
        val advertDescription = binding.editAdvertDescriptionInput.text.toString()
        val checkAdvertDescription = advertDescription.length<=2000
        if(!checkAdvertDescription){
            binding.editAdvertDescriptionInput.error="Limit of advert description is 2000 characters."
            return "Invalid advert description"
        }
        binding.editAdvertDescriptionInput.error = null
        return null
    }
    private fun focusPrice(){
        binding.priceInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.priceInput.text?.isNotEmpty()==true) {
                binding.priceLayout.helperText = validPrice()
            }else if(!focused && binding.priceLayout.helperText!=resources.getString(R.string.required))binding.priceLayout.helperText = resources.getString(R.string.required)
        }
    }
    private fun validPrice():String?{
        val advertPrice = binding.priceInput.text.toString()
        val checkAdvertPrice = advertPrice.length<=10
        if(!checkAdvertPrice){
            binding.priceInput.error="Limit of price is 10 characters."
            return "Invalid price"
        }
        binding.priceInput.error = null
        return null
    }
    private fun focusCondition(){
        binding.conditionInput.setOnFocusChangeListener(){ _, focused ->
            if(!focused)binding.conditionLayout.helperText = validCondition()
        }
    }
    private fun validCondition():String?{
        if(binding.conditionInput.text?.isNullOrEmpty()==true) return resources.getString(R.string.required)
        else return null
    }
    private fun validGenre():String?{
        if(binding.selectGenreInput.text?.isNullOrEmpty()==true) return resources.getString(R.string.required)
        else return null
    }
}