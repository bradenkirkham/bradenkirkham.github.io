package com.example.bmr_app

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.lang.ClassCastException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class frag_profile : Fragment(), View.OnClickListener {

//    private var mProfilePic: Bitmap? = null

    private var mUserName: String? = null

    //variable for different views.
    private var mIVProfilePic: ImageView? = null
    private var mButtonSubmit: Button? = null
    private var mEtUserName: EditText? = null

    //bitmap for img
    private var ThumbNailImg: Bitmap? = null //i think bitmap for thumbnail and filepath for full photo?

    private var mFile_Path: String? = null

//    var mDataPasser: DataPassingInterface? = null



    //done button will take picture and take name

    //Callback interface
//    interface DataPassingInterface {
//        fun passData(data: Array<String?>?)
//    }

    //Associate the callback with this Fragment
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        mDataPasser = try {
//            context as DataPassingInterface
//        } catch (e: ClassCastException) {
//            throw ClassCastException("$context must implement SubmitFragment.DataPassingInterface")
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mEtUserName = view?.findViewById(R.id.userName) //not sure what the ? is or why we need it?

//        mDataPasser
        mIVProfilePic = view?.findViewById(R.id.profileViewer)
        mButtonSubmit = view?.findViewById(R.id.Done)

        mIVProfilePic!!.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                cameraLauncher.launch(cameraIntent)

            } catch (ex: ActivityNotFoundException) {
                //do something

            }

        }

        mButtonSubmit!!.setOnClickListener(this)



    }

    override fun onClick(view: View) {
        when (view.id)
        {
            R.id.Done ->
            {
                mUserName = mEtUserName!!.text.toString()

                //Check if the EditText string is empty
                if (mUserName.isNullOrBlank()) {
                    //Complain that there's no text
                    Toast.makeText(activity, "Enter username first!", Toast.LENGTH_SHORT).show()
                } else {
                    //Remove any leading spaces or tabs
                    mUserName = mUserName!!.replace("^\\s+".toRegex(), "")

                    if (mUserName != null) {
//                        val userData= arrayOf<String?>(mUserName, mFile_Path)

                        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return

                        with(sharedPref.edit()){
                            putString("Name", mUserName)
                            putString("ProfPic", mFile_Path)
                            apply()
                        }
                        parentFragmentManager.popBackStack()
//                        mDataPasser!!.passData(userData)




                    } else {
                        Toast.makeText(
                            activity,
                            "Username must be one word",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private var cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK)
        {
            val extras = result.data!!.extras
            ThumbNailImg = extras!!["data"] as Bitmap?

            mIVProfilePic!!.setImageBitmap(ThumbNailImg)

            var mDisplayActIntent: Intent? = Intent(activity, frag_profile::class.java)

            if(isExternalStorageWriteable)
            {
                mFile_Path = saveImage(ThumbNailImg)
                mDisplayActIntent!!.putExtra("imagePath", mFile_Path)
            }
            else
            {
                Toast.makeText(activity, "External storage not writeable", Toast.LENGTH_SHORT).show() //not sure how camera works with frag
            }
        }
    }

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
}