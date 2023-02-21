package com.example.bmr_app.userView

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.bmr_app.AWSHelper
import com.example.bmr_app.R
import com.example.bmr_app.databinding.FragmentProfileBinding
import com.example.bmr_app.userData.UserDataClass
import com.example.bmr_app.userData.UserDataViewModel
import com.example.bmr_app.viewModels.LocationViewModel
import com.example.bmr_app.weatherData.weatherApplication
import com.example.bmr_app.weatherData.weatherDatabase
import com.example.bmr_app.weatherData.weatherRepository
import com.example.bmr_app.widget.UserProfileWidget
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class FragProfile : Fragment() {

    private val viewModelstep by activityViewModels<LocationViewModel>()

    //bitmap for img, image stuff
    private var ThumbNailImg: Bitmap? = null //i think bitmap for thumbnail and filepath for full photo?
    private var mFile_Path: String? = null
    private var mIVProfilePic: ImageView? = null
    private var gender:String? = null
    private var activiyLevelview:TextView?=null
    //viewModel and binding
    private lateinit var mUserViewModel: UserDataViewModel
    private lateinit var binding:FragmentProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container,false)
        mUserViewModel = ViewModelProvider(this)[UserDataViewModel::class.java]

        var isChecked = true
        val maleBtn = binding.maleToggleBtn
        val femalebtn = binding.femaleToggleBtn
//        var gender: String
        maleBtn.setOnClickListener {
            Log.i("gender check","It's a boy!")
            gender = "Male"
            println(gender)
        }
        femalebtn.setOnClickListener {
            Log.i("gender check","It's a girl!")
            gender = "Female"
            isChecked = false
            println(gender)
        }

        //age
        val ageSeek = binding.ageSeek
        val ageTextview = binding.ageView
        ageSeek.min = 0
        ageSeek.max = 100
        UserProfileWidget.SeekBarHelper(ageSeek,0,100,ageTextview)

        //weight
        val weight = binding.weightSeek
        val weightTextView=binding.weightView
        weight.min = 30
        weight.max = 200
        UserProfileWidget.SeekBarHelper(weight,30,200,weightTextView)

        //height
        val heightSeek = binding.heightSeek
        val heightTextview = binding.heightView
        heightSeek.min = 50
        heightSeek.max = 300
        UserProfileWidget.SeekBarHelper(heightSeek,50,300,heightTextview)

        //physical activity
        val activityLevel = binding.activitySeek
        activiyLevelview = binding.activityView
        activityLevel.min = 0
        activityLevel.max = 4
        UserProfileWidget.ActivitySeekBarHelper(activityLevel,0,4,activiyLevelview!!)

        //done btn
        val doneBtn = binding.Done
        doneBtn.setOnClickListener {
            Log.i("ProfileInput btn","Done has clicked")
            insertDataToDatabase()
            AWSHelper.uploadFile()
        }
        val restoreBtn = binding.download
        restoreBtn.setOnClickListener{
            AWSHelper.downloadFile()
            Toast.makeText(requireContext(),"User data downloded successfully, please restart app.",Toast.LENGTH_LONG).show()
            Log.i("amplify download", "download finsihed")

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mIVProfilePic = binding.profileViewer
        val photbtn =binding.photoFlotingbtn
        photbtn.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                cameraLauncher.launch(cameraIntent)
            } catch (ex: ActivityNotFoundException) {
                //do something
            }
        }
    }

    override fun onDestroy() {
        binding
        super.onDestroy()
    }

    private fun insertDataToDatabase() {
        val name = binding.userName.text.toString()
        val age = binding.ageView.text.toString()
        val height = binding.heightView.text.toString()
        val weight = binding.weightView.text.toString()
        //이미지뷰에서 bitmap 이미지 가져오자...걍 룸에 바로 저장되게도 할 수 있는데 그건 내 대가리가 아프다. 쉽게쉽게 하자
        val image = binding.profileViewer.drawable.toBitmap()
        val activity=binding.activityView.text.toString()

        val step = viewModelstep.getStep_().toString()

//check before insert data into databases
        if(gender?.let { inputCheck(name,age,height, weight, step,it) } == true){
            val bmr = UserProfileWidget.calculateBMR(gender!!,weight.toDouble(), height.toDouble(), age.toInt())
            val rCal = UserProfileWidget.calDailyCalIntake(bmr,activity)

            val user = UserDataClass(0,name,Integer.parseInt(age),height.toDouble(),weight.toDouble(),bmr,gender!!,image,
                rCal, step.toInt())

            mUserViewModel.addUser(user)
            Toast.makeText(requireContext(),"insertDATAtoDatabase was on the move",Toast.LENGTH_LONG).show()
            Log.e("ActivityvIEW",activity)

        }
        else
        {
            Toast.makeText(requireContext(),"I missed out some inputs",Toast.LENGTH_LONG).show()
        }
    }

    //for curious...
    private fun inputCheck(name:String, age: String, height: String, weight: String,  gender:String,step:String):Boolean{
        return!(TextUtils.isEmpty(name) || TextUtils.isEmpty(age)||
                TextUtils.isEmpty(height) || TextUtils.isEmpty(weight) || TextUtils.isEmpty(gender) || TextUtils.isEmpty(step) )
    }

    private var cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK)
        {
            val extras = result.data!!.extras
            ThumbNailImg = extras!!["data"] as Bitmap?

            mIVProfilePic!!.setImageBitmap(ThumbNailImg)

            val mDisplayActIntent = Intent(activity, FragProfile::class.java)

            if(isExternalStorageWriteable)
            {
                mFile_Path = saveImage(ThumbNailImg)
                mDisplayActIntent.putExtra("imagePath", mFile_Path)
            }
            else
            {
                Toast.makeText(activity, "External storage not writeable", Toast.LENGTH_SHORT).show() //not sure how camera works with frag
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun saveImage(finalBitmap: Bitmap?): String
    {
        val root = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES) // not sure what this requires
        val myDir = File("$root/saved_images")
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fName = "Thumbnail_$timeStamp.jpg"
        val file = File(myDir, fName)
        if(file.exists()) file.delete()
        try
        {
            val out = FileOutputStream(file)
            finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private val isExternalStorageWriteable: Boolean
        get()
        {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

    //simple requestPermission
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
            } else {
                Log.i("Permission: ", "Denied")
            }
        }





}//end of fragment