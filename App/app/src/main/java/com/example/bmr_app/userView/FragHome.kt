package com.example.bmr_app.userView

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bmr_app.R
import com.example.bmr_app.database.DataBase
import com.example.bmr_app.databinding.FragmentFragHomeBinding
import com.example.bmr_app.userData.UserDataViewModel


class FragHome : Fragment() {

    private lateinit var mUserViewModel: UserDataViewModel
    private lateinit var binding: FragmentFragHomeBinding
    private lateinit var roomDB: DataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomDB = DataBase.getDatabase(requireContext())

    }


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_frag_home,container,false)

        //Recycler view
        val adapter = ListAdapter()
        val recyclerView = binding.RecycleViewListing
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        //viewModel
        mUserViewModel = ViewModelProvider(this)[UserDataViewModel::class.java]
        mUserViewModel.readAlldata.observe(viewLifecycleOwner) { user ->
            adapter.setData(user)

        }

        individualPersonreturn(mUserViewModel)









        val deleteFloting = binding.deleteFloting
        deleteFloting.setOnClickListener{
            deleteAllUser()

        }


        return binding.root
    }

    override fun onDestroy() {
        binding
        super.onDestroy()
    }

    private fun deleteAllUser(){

        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_,_->
            mUserViewModel.deleteAllUser()

        }
        builder.setNegativeButton("No"){_,_ ->}
        builder.setTitle("Delete all?")
        builder.setMessage("Are you sure you want to delete all?")
        builder.create().show()
    }

//    need to handle null situation
    @SuppressLint("SetTextI18n")
    private fun individualPersonreturn(mUserViewModel: UserDataViewModel){

//    return

        (mUserViewModel.readAlldata.observe(viewLifecycleOwner) {

            if(it.isEmpty()){

                binding.individualTxtUserAge.text = "null"
                binding.individualTxtUserName.text = "Enter your info!"
                binding.individualTxtUserHeight.text = "null"
                binding.individualTxtUserWeight.text = "null"
                binding.individualTxtUserBMR.text = "null"
                binding.individualTxtUserGender.text = "null"
                binding.individualTxtUserRecomCal.text =
                    "null"
                binding.individualTxtUserStep.text = "null"


            } else {

            binding.individualTxtUserAge.text = "Age: ${it[0].age}"
            binding.individualTxtUserName.text = it[0].name
            binding.individualTxtUserHeight.text = "${it[0].Height} cm"
            binding.individualTxtUserWeight.text = "${it[0].weight} kg"
            binding.individualTxtUserBMR.text = "BMR: ${it[0].bmr}"
            binding.individualTxtUserGender.text = it[0].gender
            binding.individualTxtUserRecomCal.text =
                "Recommend kcal: ${it[0].caloriesIntake.toInt()} kcal"
            binding.individualCircleIv.setImageBitmap(it[0].image)
                binding.individualTxtUserStep.text ="Step: ${it[0].step}"
            }
        })


    }





}