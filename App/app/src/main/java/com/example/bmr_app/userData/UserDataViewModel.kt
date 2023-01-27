package com.example.bmr_app.userData

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.bmr_app.database.DataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//viewModel is acting as a communication hq between repo and the UI
//It will provide data to the ui and being resistance to configuration change

//it contains application reference
class UserDataViewModel(application: Application): AndroidViewModel(application) {

    val readAlldata: LiveData<List<UserDataClass>>


    private val repository: UserRepository

    init{
        Log.e("User Data View Model","Start")
        val userDAO = DataBase.getDatabase(application).userDataDao()
        repository = UserRepository(userDAO)
        readAlldata = repository.readAllData


    }

    fun addUser(user: UserDataClass){
        viewModelScope.launch(Dispatchers.IO){
            repository.addUser(user)
        }
    }

    fun deleteUser(user: UserDataClass){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteUser(user)
        }
    }

    fun deleteAllUser(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllUser()
        }
    }

    fun updateUser(user: UserDataClass){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateUser(user)
        }

    }

    fun readOneUser(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.readOneUser()
        }
    }




}